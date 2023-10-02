package model

import "net"

type MulticastListener interface {
	Bind(group string, port int) error
	Listen(buffer []byte) (int, net.Addr, error)
	Close() error
}
