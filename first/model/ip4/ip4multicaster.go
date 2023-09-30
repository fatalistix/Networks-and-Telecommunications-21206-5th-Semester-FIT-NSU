// Package ip4 for manipulating ipv4 multicasts
package ip4

import (
	"errors"
	"net"
	"networks/first/model"
	"strconv"

	"golang.org/x/net/ipv4"
)

var _ model.Multicaster = (*IP4Multicaster)(nil)

// IP4Multicaster struct helps to multicast messages
type IP4Multicaster struct {
	packConn                *ipv4.PacketConn
	localMulticastValidator model.LocalMulticastValidator
	udp4Addr                *net.UDPAddr
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

	s.udp4Addr, err = net.ResolveUDPAddr("udp4", ip4Group+":"+strconv.Itoa(port))
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

		conn, err := net.DialUDP("udp4", nil, s.udp4Addr)
		if err != nil {
			continue
		}

		pc := ipv4.NewPacketConn(conn)

		err = pc.JoinGroup(&nif, s.udp4Addr)
		if err != nil {
			conn.Close()
			continue
		}

		s.packConn = pc
		break
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
		n, err = s.packConn.WriteTo(buffer, nil, s.udp4Addr)
		if n == 0 && targetBytes != 0 {
			return errors.New("connection closed")
		}
		if err != nil {
			return err
		}
	}
	return nil
}

func (s *IP4Multicaster) Close() error {
	return s.packConn.Close()
}

func (s *IP4Multicaster) GetLocalAddress() string {
	return s.packConn.LocalAddr().String()
}
