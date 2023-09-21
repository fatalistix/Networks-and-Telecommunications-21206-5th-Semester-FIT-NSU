package ip4

import (
	"errors"
	"net"
	"strconv"

	"networks/first/model"
)

type IP4Multicaster struct {
	conn                    net.Conn
	localMulticastValidator model.LocalMulticastValidator
}

func NewIP4Multicaster() *IP4Multicaster {
	mcaster := IP4Multicaster{}
	mcaster.localMulticastValidator = MakeIP4LocalMulticastValidator()
	return &mcaster
}

func (s *IP4Multicaster) Connect(ip4Group string, port int) error {
	err := s.localMulticastValidator.Validate(ip4Group)
	if err != nil {
		return err
	}

	udp4Addr, err := net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
	if err != nil {
		return err
	}

	s.conn, err = net.DialUDP("udp4", nil, udp4Addr)
	if err != nil {
		return err
	}

	return nil
}

func (s *IP4Multicaster) Multicast(buffer []byte) error {
	if buffer == nil {
		return errors.New("buffer is nil")
	}

	targetBytes := len(buffer)
	n := -1
	var err error
	for targetBytes != n {
		n, err = s.conn.Write(buffer)
		if n == 0 {
			return errors.New("connection closed")
		}
		if err != nil {
			return err
		}
	}
	return nil
}

func (s *IP4Multicaster) Close() error {
	return s.conn.Close()
}

func (s *IP4Multicaster) GetLocalAddress() string {
	return s.conn.LocalAddr().String()
}
