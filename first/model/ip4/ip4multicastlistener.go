package ip4

import (
	"net"
	"strconv"
	"strings"

	"golang.org/x/net/ipv4"

	"networks/first/model"
)

var _ model.MulticastListener = (*IP4MulticastListener)(nil)

type IP4MulticastListener struct {
	localMulticastValidator model.LocalMulticastValidator
	packConn                *ipv4.PacketConn
	// conn                    *net.UDPConn
}

func NewIP4MulticastListener() *IP4MulticastListener {
	listener := IP4MulticastListener{}
	listener.localMulticastValidator = MakeIP4LocalMulticastValidator()
	return &listener
}

func (s *IP4MulticastListener) Bind(ip4Group string, port int) error {
	// err := s.localMulticastValidator.Validate(ip4Group)
	// if err != nil {
	// 	return err
	// }
	//
	// udp4Addr, err := net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
	// if err != nil {
	// 	return err
	// }
	//
	// s.conn, err = net.ListenMulticastUDP("udp4", nil, udp4Addr)
	// if err != nil {
	// 	return err
	// }

	udp4Addr, err := net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
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
	// return s.conn.ReadFromUDP(buffer)
	n, _, addr, err := s.packConn.ReadFrom(buffer)
	return n, addr, err
}

func (s *IP4MulticastListener) Close() error {
	return s.packConn.Close()
}
