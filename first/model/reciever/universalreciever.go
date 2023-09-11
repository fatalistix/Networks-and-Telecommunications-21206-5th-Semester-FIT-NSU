package reciever

import (
	"errors"
	"net"

	"networks/first/model/iputils"
)

type UniversalReciever struct {
	conn net.Conn
}

func (s UniversalReciever) Recieve(buffer []byte) (int, error) {
	n, err := s.conn.Read(buffer)
	if err != nil {
		return 0, err
	}
	return n, nil
}

func (s UniversalReciever) Close() error {
	return s.conn.Close()
}

func Connect(group string) (UniversalReciever, error) {
	ipstr, err := iputils.DeletePort(group)
	if err != nil {
		return UniversalReciever{}, err
	}

	if iputils.IsIPv4(ipstr) {
		ip4 := net.ParseIP(ipstr)
		if !ip4.IsMulticast() {
			return UniversalReciever{}, errors.New("got non-multicast IPv4")
		}

		um := UniversalReciever{}
		um.conn, err = net.Dial("udp4", group)
		if err != nil {
			return um, err
		}
		return um, nil
	} else if iputils.IsIPv6(ipstr) {
		ip16 := net.ParseIP(ipstr)
		if !ip16.IsMulticast() {
			return UniversalReciever{}, errors.New("got non-multicast IPv6")
		}

		um := UniversalReciever{}
		um.conn, err = net.Dial("udp6", group)
		if err != nil {
			return um, err
		}
		return um, nil
	} else {
		return UniversalReciever{}, errors.New("invalid ip address")
	}

}
