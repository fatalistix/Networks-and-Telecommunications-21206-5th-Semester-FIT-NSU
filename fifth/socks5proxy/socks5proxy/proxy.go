package socks5proxy

import (
	"fmt"
	"io"
	"net"
	"strconv"
)

type Socks5Proxy struct {
	listenTCPAddr *net.TCPAddr
	tcpListener   *net.TCPListener
	closers       map[io.Closer]bool
	closed        bool
}

func NewSocks5Proxy() *Socks5Proxy {
	return &Socks5Proxy{
		closers: make(map[io.Closer]bool),
		closed:  false,
	}
}

func (s *Socks5Proxy) Open(port uint16) error {
	var err error

	s.listenTCPAddr, err = net.ResolveTCPAddr("tcp", ":"+strconv.Itoa(int(port)))
	if err != nil {
		return fmt.Errorf("open: failed to resolve TCP address: %w", err)
	}

	s.tcpListener, err = net.ListenTCP("tcp", s.listenTCPAddr)
	if err != nil {
		return fmt.Errorf("open: failed to start listening on TCP: %w", err)
	}

	return nil
}

func (s *Socks5Proxy) Serve() error {
	defer s.tcpListener.Close()

	for {
		tcpConn, err := s.tcpListener.AcceptTCP()
		if err != nil {
			if s.closed {
				return nil
			}
			return fmt.Errorf("serve: failed to listen for TCP connection: %w", err)
		}

		go func() {
			server := newSingleConnectionServer(tcpConn)
			s.closers[server] = true
			err = server.Serve()
			delete(s.closers, server)
			if err != nil {
				fmt.Println(err.Error())
			}
		}()
	}
}

func (s *Socks5Proxy) Close() error {
	s.closed = true
	err := s.tcpListener.Close()
	if err != nil {
		return fmt.Errorf("close: error closing tcp listener: %w", err)
	}
	for k := range s.closers {
		_ = k.Close()
	}
	return nil
}
