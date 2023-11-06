package socks5proxy

import (
	"errors"
	"fmt"
	"io"
	"net"
	"strconv"
	"syscall"
	"time"
)

type messageSource interface {
	ReadGreetingMessage() (socks5GreetingMessage, error)
	WriteGreetingAnswer(socks5GreetingAnswer) error
	ReadClientMessage() (socks5ClientMessage, error)
	WriteServerAnswer(socks5ServerAnswer) error
}

type singleConnectionServer struct {
	messageSource messageSource
	timeout       time.Duration
	tcpClientConn *net.TCPConn
	clientStats   *connectionStats
	remoteStats   *connectionStats
}

func newSingleConnectionServer(tcpClientConn *net.TCPConn) *singleConnectionServer {
	server := singleConnectionServer{
		messageSource: newByteSliceMessageSource(tcpClientConn, tcpClientConn),
		timeout:       time.Second * 10,
		tcpClientConn: tcpClientConn,
		clientStats:   nil,
		remoteStats:   nil,
	}
	return &server
}

func (s *singleConnectionServer) Serve() error {
	defer s.tcpClientConn.Close()

	var err error

	_, err = s.messageSource.ReadGreetingMessage()
	err = s.handleMessageSourceError(err)
	if err != nil {
		return fmt.Errorf("serve: %w", err)
	}

	greetingAnswer := socks5GreetingAnswer{SocksVersion: socks5, AuthMethod: 0}
	err = s.messageSource.WriteGreetingAnswer(greetingAnswer)
	if err != nil {
		return fmt.Errorf("serve: %w", err)
	}

	var clientMessage socks5ClientMessage
	clientMessage, err = s.messageSource.ReadClientMessage()
	err = s.handleMessageSourceError(err)
	if err != nil {
		return fmt.Errorf("serve: %w", err)
	}

	switch clientMessage.MessageCode {
	case establishTCP:
		err = s.establishTCP(clientMessage)
		if err != nil {
			return fmt.Errorf("serve: %w", err)
		}
	case bindPort, associateUDPPort:
		err = s.sendUnsupported(clientMessage)
		if err != nil {
			return fmt.Errorf("serve: %w", err)
		}
	}
	return nil
}

func (s *singleConnectionServer) Close() error {
	err := s.tcpClientConn.Close()
	if err != nil {
		return fmt.Errorf("close: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) Stats() (StatsResult, StatsResult, error) {
	if s.clientStats == nil || s.remoteStats == nil {
		return StatsResult{}, StatsResult{}, fmt.Errorf("stats: no stats available")
	} else {
		return s.clientStats.Stats(), s.remoteStats.Stats(), nil
	}
}

func (s *singleConnectionServer) handleMessageSourceError(err error) error {
	if err != nil {
		if errors.Is(err, errProtocol) {
			sendErr := s.sendProtocolError()
			if sendErr != nil {
				return fmt.Errorf("handle message source error: on %w - %w", err, sendErr)
			}
			return fmt.Errorf("handle message source error: %w", err)
		}
	}
	return nil
}

func (s *singleConnectionServer) establishTCP(clientMessage socks5ClientMessage) error {
	tcpRemoteConn, err := net.DialTimeout("tcp", string(clientMessage.AddressPayload)+":"+strconv.Itoa(int(clientMessage.Port)), s.timeout)
	err = s.handleDialTimeoutError(err, clientMessage)
	if err != nil {
		return fmt.Errorf("establish tcp: %w", err)
	}

	defer tcpRemoteConn.Close()

	addrType, serverIP, port := tcpLocalAddrInfo(tcpRemoteConn.(*net.TCPConn))

	err = s.sendRequestGranted(serverIP, addrType, port)
	if err != nil {
		return fmt.Errorf("serve: error sending completed answer: %w", err)
	}
	err = s.startTransmitting(tcpRemoteConn.(*net.TCPConn))
	if err != nil {
		return fmt.Errorf("establish tcp: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) handleDialTimeoutError(err error, clientMessage socks5ClientMessage) error {
	if err != nil {
		var sendErr error
		if errors.Is(err, syscall.EPERM) {
			sendErr = s.sendConnNotAllowed(clientMessage)
		} else if errors.Is(err, syscall.ENETUNREACH) {
			sendErr = s.sendNetworkUnreachable(clientMessage)
		} else if errors.Is(err, syscall.EHOSTUNREACH) || err.(net.Error).Timeout() {
			sendErr = s.sendHostUnreachable(clientMessage)
		} else if errors.Is(err, syscall.ECONNREFUSED) {
			sendErr = s.sendConnectionRefused(clientMessage)
		} else {
			sendErr = s.sendGeneralFailure(clientMessage.AddressPayload, clientMessage.AddressType, clientMessage.Port)
		}
		if sendErr != nil {
			return fmt.Errorf("handle dial timeout error: on %w - %w", err, sendErr)
		}
		return fmt.Errorf("handle dial timeout error: %w", err)
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
		waitingChan             = make(chan bool)
	)

	s.remoteStats = newConnectionStats(
		tcpRemoteConn.RemoteAddr().(*net.TCPAddr).IP,
		uint16(tcpRemoteConn.RemoteAddr().(*net.TCPAddr).Port),
	)
	s.clientStats = newConnectionStats(
		s.tcpClientConn.RemoteAddr().(*net.TCPAddr).IP,
		uint16(s.tcpClientConn.RemoteAddr().(*net.TCPAddr).Port),
	)

	go func() {
		stopped := false

		for {
			remoteReadBytes, remoteErr = tcpRemoteConn.Read(remoteBuffer)
			if remoteErr != nil {
				_ = s.tcpClientConn.Close()
				remoteErr = fmt.Errorf("remote connection: %w", remoteErr)
				break
			}
			s.remoteStats.AddReadBytes(uint64(remoteReadBytes))

			clientTotallyWroteBytes = 0
			for clientTotallyWroteBytes != remoteReadBytes {
				clientWroteBytes, remoteErr = s.tcpClientConn.Write(remoteBuffer[clientTotallyWroteBytes:remoteReadBytes])
				if remoteErr != nil {
					_ = s.tcpClientConn.Close()
					remoteErr = fmt.Errorf("client connection: %w", remoteErr)
					stopped = true
					break
				}
				clientTotallyWroteBytes += clientWroteBytes
				s.clientStats.AddWroteBytes(uint64(clientWroteBytes))
			}
			if stopped {
				break
			}
		}
		waitingChan <- true
	}()

	stopped := false

	for {
		clientReadBytes, clientErr = s.tcpClientConn.Read(clientBuffer)
		if clientErr != nil {
			_ = tcpRemoteConn.Close()
			clientErr = fmt.Errorf("client connection: error reading: %w", clientErr)
			break
		}
		s.clientStats.AddReadBytes(uint64(clientReadBytes))

		remoteTotallyWroteBytes = 0
		for remoteTotallyWroteBytes != clientReadBytes {
			remoteWroteBytes, clientErr = tcpRemoteConn.Write(clientBuffer[remoteTotallyWroteBytes:clientReadBytes])
			if clientErr != nil {
				_ = tcpRemoteConn.Close()
				clientErr = fmt.Errorf("remote connection: error writing: %w", clientErr)
				stopped = true
				break
			}
			remoteTotallyWroteBytes += remoteWroteBytes
			s.remoteStats.AddWroteBytes(uint64(remoteWroteBytes))
		}
		if stopped {
			break
		}
	}
	<-waitingChan

	isExpectedClientErr := errors.Is(clientErr, io.EOF) || errors.Is(clientErr, net.ErrClosed) || (clientErr == nil)
	isExpectedRemoteErr := errors.Is(remoteErr, io.EOF) || errors.Is(remoteErr, net.ErrClosed) || (remoteErr == nil)

	if isExpectedClientErr && isExpectedRemoteErr {
		return nil
	} else if !isExpectedClientErr && isExpectedRemoteErr {
		return fmt.Errorf("startTransmitting: %w", clientErr)
	} else if isExpectedClientErr && !isExpectedRemoteErr {
		return fmt.Errorf("startTransmitting: %w", remoteErr)
	} else {
		return fmt.Errorf("startTransmitting: both coroutines finished with errors (%w) and (%w)", clientErr, remoteErr)
	}
}

func (s *singleConnectionServer) sendRequestGranted(serverIP []byte, addrType addressType, port uint16) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     requestGranted,
		AddressType:    addrType,
		AddressPayload: serverIP,
		Port:           port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send request granted: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendGeneralFailure(serverIP []byte, addrType addressType, port uint16) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     generalFailure,
		AddressType:    addrType,
		AddressPayload: serverIP,
		Port:           port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send general failure: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendConnNotAllowed(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     notAllowedByRuleset,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send connection not allowed: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendNetworkUnreachable(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     networkUnreachable,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send network unreachable: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendHostUnreachable(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     hostUnreachable,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send host unreachable: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendConnectionRefused(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     connectionRefusedByDestinationHost,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send connection refused: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendUnsupported(clientMessage socks5ClientMessage) error {
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     commandNotSupported,
		AddressType:    clientMessage.AddressType,
		AddressPayload: clientMessage.AddressPayload,
		Port:           clientMessage.Port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send unsupported: %w", err)
	}
	return nil
}

func (s *singleConnectionServer) sendProtocolError() error {
	addrType, ip, port := tcpLocalAddrInfo(s.tcpClientConn)
	serverAnswer := socks5ServerAnswer{
		SocksVersion:   socks5,
		AnswerCode:     protocolError,
		AddressType:    addrType,
		AddressPayload: ip,
		Port:           port,
	}
	err := s.messageSource.WriteServerAnswer(serverAnswer)
	if err != nil {
		return fmt.Errorf("send protocol error: %w", err)
	}
	return nil
}

func tcpLocalAddrInfo(conn *net.TCPConn) (addressType, []byte, uint16) {
	var serverIP []byte
	var addrType addressType
	tcpAddr := conn.LocalAddr().(*net.TCPAddr)
	if tcpAddr.IP.To4() == nil {
		serverIP = tcpAddr.IP.To16()
		addrType = ipv6
	} else {
		serverIP = tcpAddr.IP.To4()
		addrType = ipv4
	}
	return addrType, serverIP, uint16(tcpAddr.Port)
}
