package ip4

import (
	"errors"
	"log"
	"net"
	"os"
	"time"

	"networks/first/model"
)

var logger *log.Logger = log.New(
	os.Stdin,
	"ip4server: ",
	log.Ldate|log.Ltime|log.Lshortfile|log.Lmicroseconds,
)

type IP4B6FServer struct {
	multicaster     *IP4Multicaster
	listener        *IP4MulticastListener
	id              string
	quitMulticaster chan bool
	quitListener    chan bool
}

func NewIP4B6FServer(
	ip string,
	port int,
	id string,
) (*IP4B6FServer, error) {
	server := IP4B6FServer{}
	server.listener = NewIP4MulticastListener()
	logger.Println("binding to multicast listener ip + port...")
	err := server.listener.Bind(ip, port)
	if err != nil {
		logger.Println("error binding: ", err)
		return nil, err
	}

	server.multicaster = NewIP4Multicaster()
	logger.Println("connecting to multicast's listener...")
	err = server.multicaster.Connect(ip, port)
	if err != nil {
		logger.Println("error connecting: ", err)
		return nil, err
	}

	server.id = id
	server.quitMulticaster = make(chan bool)
	server.quitListener = make(chan bool)
	logger.Println("server initialized without errors")
	return &server, nil
}

func (s *IP4B6FServer) Start(
	buffer []byte,
	timeoutMs int,
	maxErrors int,
	onPackageRecieved func(addr net.Addr, packet model.B6FPacket),
) error {
	if timeoutMs <= 0 {
		logger.Println("invalid argument timeoutMs = ", timeoutMs, ", epxpected >= 0")
		return errors.New("invalid timeout: cannot be less then zero")
	}
	if maxErrors <= 0 {
		logger.Println("invalid argument maxErrors = ", maxErrors, ", epxpected >= 0")
		return errors.New("invalid maxErrors: cannot be less then zero")
	}

	logger.Println("creating packet...")
	sendingData, err := model.MakeB6FPacketReport(s.id)
	if err != nil {
		logger.Println("error creating packet: ", err)
		return err
	}

	sendingDataBytes := sendingData.ToBytes()

	go func() {
		logger.Println("sending goroutine: started")
		ticker := time.NewTicker(time.Duration(timeoutMs) * time.Millisecond)
		logger.Println("sending goroutine: ticker initialized")
		errCounter := 0
		defer func() {
			ticker.Stop()
			logger.Println("sending goroutine: ticker stoped")
		}()
		for {
			select {
			case <-s.quitMulticaster:
				{
					logger.Println("sending goroutine: terminated")
					packet := model.MakeB6FPacketLeave()
					_ = s.multicaster.Multicast(packet.ToBytes())
					logger.Println("sending goroutine: sent leave packet")
					// ticker.Stop()
					// logger.Println("sending goroutine: ticker stoped")
					logger.Println("sending goroutine: return")
					s.quitMulticaster <- true
					return
				}
			case <-ticker.C:
				{
					if errCounter >= maxErrors {
						logger.Println("sending goroutine: too many errors")
						packet := model.MakeB6FPacketLeave()
						_ = s.multicaster.Multicast(packet.ToBytes())
						logger.Println("sending goroutine: sent leave packet")
						// ticker.Stop()
						// logger.Println("sending goroutine: ticker stoped")
						logger.Println("sending goroutine: return")
						<-s.quitMulticaster
						s.quitMulticaster <- true
						return
					}

					logger.Println("sending goroutine: sending packet...")
					err = s.multicaster.Multicast(sendingDataBytes)

					if err != nil {
						logger.Println("sending goroutine: got error: ", err)
						errCounter++
						continue
					}

					errCounter = 0
					logger.Println("sending goroutine: errors counter reset")
				}
			}
		}
	}()

	go func() {
		logger.Println("listening goroutine: started")
		errCounter := 0
		for {
			select {
			case <-s.quitListener:
				{
					logger.Println("listening goroutine: terminated")
					logger.Println("listening goroutine: return")
					s.quitListener <- true
					return
				}
			default:
				{
					if errCounter >= maxErrors {
						s.quitListener <- true
						logger.Println("sending goroutine: too many errors")
						logger.Println("sending goroutine: continue")
						<-s.quitListener
						s.quitListener <- true
						continue
					}

					logger.Println("listening goroutine: listening...")
					n, _, addr, err := s.listener.Listen(buffer)
					logger.Println("listening goroutine: recieved a package")
					if n == 0 {
						logger.Println("listening goroutine: recieved a zero length package")
						logger.Println("listening goroutine: continue")
						errCounter++
						continue
					}

					if err != nil {
						logger.Println("listening goroutine: got error: ", err)
						errCounter++
						continue
					}

					// mCasterIpAddr := s.multicaster.GetLocalAddress()
					// fmt.Println(mCasterIpAddr, " ", addr.String())
					// if mCasterIpAddr == addr.String() {
					// logger.Println("listening goroutine: got myself ==> skip")
					// continue
					// }

					logger.Println("listening goroutine: parsing packet...")
					packet, err := model.MakeB6FPacketFromBytes(buffer)
					if err != nil {
						logger.Println("listening goroutine: got error: ", err)
						logger.Println("listening goroutine: skipping")
						// errCounter++
						continue
					}

					logger.Println(
						"listening goroutine: packet parsed successfully: message type = ",
						packet.MessageType(),
					)
					errCounter = 0
					onPackageRecieved(addr, packet)
				}

			}
		}
	}()

	return nil
}

func (s *IP4B6FServer) Close() error {
	s.quitMulticaster <- true
	<-s.quitMulticaster
	s.quitListener <- true
	<-s.quitListener
	logger.Println("closing server...")
	err1 := s.multicaster.Close()
	logger.Println("multicaster closed")
	err2 := s.listener.Close()
	logger.Println("listener closed")
	logger.Println("errors: \"", err1, "\" and \"", err2, "\"")
	if err1 != nil {
		return err1
	}
	return err2
}
