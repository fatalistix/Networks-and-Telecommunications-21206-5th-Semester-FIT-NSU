package ip4

import (
	"net"
	"networks/first/model"
	"strconv"

	"golang.org/x/net/ipv4"
)

var _ model.MulticastListener = (*IP4MulticastListener)(nil)

type IP4MulticastListener struct {
	localMulticastValidator model.LocalMulticastValidator
	packConn                *ipv4.PacketConn
}

func NewIP4MulticastListener() *IP4MulticastListener {
	listener := IP4MulticastListener{}
	listener.localMulticastValidator = MakeIP4LocalMulticastValidator()
	return &listener
}

func (s *IP4MulticastListener) Bind(ip4Group string, port int) error {
	udp4Addr, err := net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
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

		conn, err := net.ListenUDP("udp4", udp4Addr)
		if err != nil {
			continue
		}

		pc := ipv4.NewPacketConn(conn)

		err = pc.JoinGroup(&nif, udp4Addr)
		if err != nil {
			conn.Close()
			continue
		}

		s.packConn = pc
		break
	}

	return nil
}

func (s *IP4MulticastListener) Listen(
	buffer []byte,
) (int, net.Addr, error) {
	n, _, addr, err := s.packConn.ReadFrom(buffer)
	return n, addr, err
}

func (s *IP4MulticastListener) Close() error {
	return s.packConn.Close()
}
