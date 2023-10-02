package ip16

import (
	"net"
	"networks/first/model"
	"strconv"

	"golang.org/x/net/ipv6"
)

var _ model.MulticastListener = (*IP16MulticastListener)(nil)

type IP16MulticastListener struct {
	localMulticastValidator model.LocalMulticastValidator
	packConn                *ipv6.PacketConn
}

func NewIP16MulticastListener() *IP16MulticastListener {
	listener := IP16MulticastListener{}
	listener.localMulticastValidator = MakeIP16LocalMulticastValidator()
	return &listener
}

func (s *IP16MulticastListener) Bind(ip16Group string, port int) error {
	udp16Addr, err := net.ResolveUDPAddr("udp6", "["+ip16Group+"]:"+strconv.Itoa(port))
	if err != nil {
		return err
	}

	nifaces, err := net.Interfaces()
	if err != nil {
		return err
	}

	for _, nif := range nifaces {
		if nif.Flags&net.FlagLoopback != 0 {
			continue
		}

		if nif.Flags&net.FlagMulticast == 0 {
			continue
		}

		if nif.Flags&net.FlagUp == 0 {
			continue
		}

		if nif.Flags&net.FlagRunning == 0 {
			continue
		}

		conn, err := net.ListenUDP("udp6", udp16Addr)
		if err != nil {
			continue
		}

		pc := ipv6.NewPacketConn(conn)

		err = pc.JoinGroup(&nif, udp16Addr)
		if err != nil {
			conn.Close()
			continue
		}

		s.packConn = pc
		break
	}

	return nil
}

func (s *IP16MulticastListener) Listen(buffer []byte) (int, net.Addr, error) {
	n, _, addr, err := s.packConn.ReadFrom(buffer)
	return n, addr, err
}

func (s *IP16MulticastListener) Close() error {
	return s.packConn.Close()
}
