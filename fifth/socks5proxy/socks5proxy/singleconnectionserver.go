package socks5proxy

import (
	"fmt"
	"io"
	"net"
	"strconv"
	"time"
)

type messageSource interface {
	readGreetingMessage() (socks5GreetingMessage, error)
	writeGreetingAnswer(socks5GreetingAnswer) error
	readClientMessage() (socks5ClientMessage, error)
	writeServerAnswer(socks5ServerAnswer) error
}

type singleConnectionServer struct {
	messageSource messageSource
	timeout       time.Duration
	tcpClientConn *net.TCPConn
}

func newSingleConnectionServer(tcpClientConn *net.TCPConn) *singleConnectionServer {
	return &singleConnectionServer{
		messageSource: newByteSliceMessageSource(tcpClientConn, tcpClientConn),
		timeout:       time.Second * 10,
		tcpClientConn: tcpClientConn,
	}
}

func (s *singleConnectionServer) Serve() error {
	defer s.tcpClientConn.Close()

	var err error

	_, err = s.messageSource.readGreetingMessage()
	if err != nil {
		return fmt.Errorf("serve: error reading greeting message: %w", err)
	}
	fmt.Println("wrote greeting from client")

	greetingAnswer := socks5GreetingAnswer{SocksVersion: 5, AuthMethod: 0}
	err = s.messageSource.writeGreetingAnswer(greetingAnswer)
	if err != nil {
		return fmt.Errorf("serve: error writing greeting answer: %w", err)
	}
	fmt.Println("wrote greeting to client")

	var clientMessage socks5ClientMessage
	clientMessage, err = s.messageSource.readClientMessage()
	if err != nil {
		return fmt.Errorf("serve: error reading client message: %w", err)
	}
	fmt.Println("read client message")

	switch clientMessage.MessageCode {
	case 1:
		switch clientMessage.AddressType {
		case 1, 3, 4:
			tcpRemoteConn, err := net.DialTimeout("tcp", string(clientMessage.AddressPayload)+":"+strconv.Itoa(int(clientMessage.Port)), s.timeout)
			if err != nil {
				err = s.sendHostUnreachable(clientMessage)
				if err != nil {
					return fmt.Errorf("serve: error sending host unreachable answer: %w", err)
				}
				return fmt.Errorf("serve: error connecting to remote host: %w", err)
			}
			defer tcpRemoteConn.Close()
			err = s.sendCompleted(clientMessage, clientMessage.AddressPayload, clientMessage.AddressType)
			if err != nil {
				return fmt.Errorf("serve: error sending completed answer: %w", err)
			}
			fmt.Println("Started transmitting")
			return s.startTransmitting(tcpRemoteConn.(*net.TCPConn))
		default:
		}
	default:

	}
	return nil
}

func (s *singleConnectionServer) Close() error {
	err := s.tcpClientConn.Close()
	if err != nil {
		return fmt.Errorf("close: error closing client tcp connection: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) startTransmitting(tcpRemoteConn *net.TCPConn) error {
	clientBuffer := make([]byte, 1400)
	remoteBuffer := make([]byte, 1400)

	var (
		remoteTotallyWroteBytes = 0
		remoteWroteBytes        int
		remoteReadBytes         int
		remoteErr               error
		clientTotallyWroteBytes = 0
		clientWroteBytes        int
		clientReadBytes         int
		clientErr               error
		waitingChan             chan bool
	)

	go func() {
		for {
			remoteTotallyWroteBytes = 0
			remoteReadBytes, remoteErr = tcpRemoteConn.Read(remoteBuffer)
			if remoteErr != nil {
				_ = s.tcpClientConn.Close()
				if remoteErr != io.EOF {
					remoteErr = fmt.Errorf("remote connection: error reading: %w", remoteErr)
				}
				break
			}
			for remoteTotallyWroteBytes != remoteReadBytes {
				remoteWroteBytes, remoteErr = s.tcpClientConn.Write(remoteBuffer[remoteTotallyWroteBytes:remoteReadBytes])
				if remoteErr != nil {
					remoteErr = fmt.Errorf("client connection: error writing: %w", remoteErr)
					_ = s.tcpClientConn.Close()
					break
				}
				remoteTotallyWroteBytes += remoteWroteBytes
			}
		}
		waitingChan <- true
	}()

	for {
		clientTotallyWroteBytes = 0
		clientReadBytes, clientErr = s.tcpClientConn.Read(clientBuffer)
		if clientErr != nil {
			_ = tcpRemoteConn.Close()
			if clientErr != io.EOF {
				clientErr = fmt.Errorf("client connection: error reading: %w", remoteErr)
			}
			break
		}
		for clientTotallyWroteBytes != clientReadBytes {
			clientWroteBytes, clientErr = tcpRemoteConn.Write(clientBuffer[clientTotallyWroteBytes:clientReadBytes])
			if clientErr != nil {
				_ = tcpRemoteConn.Close()
				if clientErr != io.EOF {
					clientErr = fmt.Errorf("remote connection: error writing: %w", clientErr)
				}
				break
			}
			clientTotallyWroteBytes += clientWroteBytes
		}
	}
	<-waitingChan
	if clientErr == io.EOF && remoteErr == io.EOF {
		return nil
	} else if clientErr != io.EOF && remoteErr == io.EOF {
		return fmt.Errorf("startTransmitting: %w", clientErr)
	} else if clientErr == io.EOF && remoteErr != io.EOF {
		return fmt.Errorf("startTransmitting: %w", remoteErr)
	} else {
		return fmt.Errorf("startTransmitting: both coroutines finished with errors (%w) and (%w)", clientErr, remoteErr)
	}
}

func (s *singleConnectionServer) sendCompleted(clientMessage socks5ClientMessage, resolvedIP []byte, addressType byte) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   5,
		AnswerCode:     0,
		AddressType:    addressType,
		AddressPayload: resolvedIP,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.writeServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("sendCompleted: error writing server answer: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendHostUnreachable(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   5,
		AnswerCode:     4,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.writeServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("sendHostUnreachable: error writing server answer: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendUnsupported(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   5,
		AnswerCode:     7,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.writeServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("sendUnsupported: error writing server answer: %w", err)
	}
	return nil
}
