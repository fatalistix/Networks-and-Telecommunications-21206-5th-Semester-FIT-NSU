package ip16

import (
	"errors"
	"net"
	"strconv"
	"strings"

	"golang.org/x/net/ipv6"

	"networks/first/model"
)

var _ model.Multicaster = (*IP16Multicaster)(nil)

type IP16Multicaster struct {
	localMulticastValidator model.LocalMulticastValidator
	udp16Addr               *net.UDPAddr
	packConn                *ipv6.PacketConn
}

func NewIP16Multicaster() *IP16Multicaster {
	mcaster := IP16Multicaster{}
	mcaster.localMulticastValidator = MakeIP16LocalMulticastValidator()
	return &mcaster
}

func (s *IP16Multicaster) Connect(ip16Group string, port int) error {
	err := s.localMulticastValidator.Validate(ip16Group)
	if err != nil {
		return err
	}

	s.udp16Addr, err = net.ResolveUDPAddr("udp6", "["+ip16Group+"]:"+strconv.Itoa(port))
	if err != nil {
		return err
	}

	nifaces, err := net.Interfaces()
	if err != nil {
		return err
	}

	for _, nif := range nifaces {
		if strings.HasPrefix(nif.Name, "lo") {
			continue
		}

		conn, err := net.ListenPacket("udp6", ":0")
		if err != nil {
			continue
		}

		pc := ipv6.NewPacketConn(conn)

		err = pc.JoinGroup(&nif, s.udp16Addr)
		if err != nil {
			conn.Close()
			continue
		}

		s.packConn = pc
		break
	}

	return nil
}

func (s *IP16Multicaster) Multicast(buffer []byte) error {
	if buffer == nil {
		return errors.New("buffer is nil")
	}

	targetBytes := len(buffer)
	n := -1
	var err error
	for targetBytes != n {
		n, err = s.packConn.WriteTo(buffer, nil, s.udp16Addr)
		if n == 0 && targetBytes != 0 {
			return errors.New("connection closed")
		}
		if err != nil {
			return err
		}
	}
	return nil
}

func (s *IP16Multicaster) Close() error {
	return s.packConn.Close()
}

func (s *IP16Multicaster) GetLocalAddress() string {
	return s.packConn.LocalAddr().String()
}
