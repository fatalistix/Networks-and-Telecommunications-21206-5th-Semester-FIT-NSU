package ip4

import (
	"net"
	"strconv"

	"networks/first/model"
)

type IP4MulticastListener struct {
	localMulticastValidator model.LocalMulticastValidator
	conn                    *net.UDPConn
}

func NewIP4MulticastListener() *IP4MulticastListener {
	listener := IP4MulticastListener{}
	listener.localMulticastValidator = MakeIP4LocalMulticastValidator()
	return &listener
}

func (s *IP4MulticastListener) Bind(ip4Group string, port int) error {
	err := s.localMulticastValidator.Validate(ip4Group)
	if err != nil {
		return err
	}

	udp4Addr, err := net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
	if err != nil {
		return err
	}

	s.conn, err = net.ListenMulticastUDP("udp4", nil, udp4Addr)
	if err != nil {
		return err
	}

	return nil
}

func (s *IP4MulticastListener) Listen(buffer []byte) (int, *net.UDPAddr, error) {
	return s.conn.ReadFromUDP(buffer)
}

func (s *IP4MulticastListener) Close() error {
	return s.conn.Close()
}
