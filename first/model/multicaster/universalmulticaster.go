package multicaster

import (
	"errors"
	"net"

	"networks/first/model/iputils"
)

type UniversalMulticaster struct {
	conn net.Conn
}

func (s UniversalMulticaster) Multicast(buffer []byte) error {
	if buffer == nil {
		return errors.New("buffer is nil")
	}

	targetBytes := len(buffer)
	n := 0
	for targetBytes != n {
		temp, err := s.conn.Write(buffer[n:])
		if err != nil {
			return err
		}
		n += temp
	}

	return nil
}

func (s UniversalMulticaster) Close() error {
	return s.conn.Close()
}

func Connect(group string) (UniversalMulticaster, error) {
	ipstr, err := iputils.DeletePort(group)
	if err != nil {
		return UniversalMulticaster{}, err
	}

	if iputils.IsIPv4(ipstr) {
		ip4 := net.ParseIP(ipstr)
		if !ip4.IsMulticast() {
			return UniversalMulticaster{}, errors.New("got non-multicast IPv4")
		}

		um := UniversalMulticaster{}
		um.conn, err = net.Dial("udp4", group)
		if err != nil {
			return um, err
		}
		return um, nil
	} else if iputils.IsIPv6(ipstr) {
		ip16 := net.ParseIP(ipstr)
		if !ip16.IsMulticast() {
			return UniversalMulticaster{}, errors.New("got non-multicast IPv6")
		}

		um := UniversalMulticaster{}
		um.conn, err = net.Dial("udp6", group)
		if err != nil {
			return um, err
		}
		return um, nil
	} else {
		return UniversalMulticaster{}, errors.New("invalid ip address")
	}
}
