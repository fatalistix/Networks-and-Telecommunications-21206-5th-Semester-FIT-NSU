package socks5proxy

import (
	"errors"
	"fmt"
	"io"
	"net"
	"strconv"
	"sync"
)

type Statser interface {
	Stats() (StatsResult, StatsResult, error)
}

type StatsCloser interface {
	Statser
	io.Closer
}

type Socks5Proxy struct {
	listenTCPAddr     *net.TCPAddr
	tcpListener       *net.TCPListener
	statsClosers      map[StatsCloser]bool
	statsClosersMutex sync.Mutex
}

func NewSocks5Proxy() *Socks5Proxy {
	return &Socks5Proxy{
		statsClosers: make(map[StatsCloser]bool),
	}
}

func (s *Socks5Proxy) Open(port uint16) error {
	var err error

	s.listenTCPAddr, err = net.ResolveTCPAddr("tcp", ":"+strconv.Itoa(int(port)))
	if err != nil {
		return fmt.Errorf("open: %w", err)
	}

	s.tcpListener, err = net.ListenTCP("tcp", s.listenTCPAddr)
	if err != nil {
		return fmt.Errorf("open: %w", err)
	}

	return nil
}

func (s *Socks5Proxy) Serve() error {
	defer s.tcpListener.Close()

	for {
		tcpConn, err := s.tcpListener.AcceptTCP()
		if err != nil {
			if errors.Is(err, net.ErrClosed) {
				return nil
			}
			return fmt.Errorf("serve: %w", err)
		}

		go func() {
			server := newSingleConnectionServer(tcpConn)
			s.statsClosersMutex.Lock()
			s.statsClosers[server] = true
			s.statsClosersMutex.Unlock()
			err = server.Serve()
			s.statsClosersMutex.Lock()
			delete(s.statsClosers, server)
			s.statsClosersMutex.Unlock()
			if err != nil {
				fmt.Println("serving coroutine:", err.Error())
			}
		}()
	}
}

type StatsResultPair struct {
	Client StatsResult
	Remote StatsResult
}

func (s *Socks5Proxy) Stats() []StatsResultPair {
	s.statsClosersMutex.Lock()
	result := make([]StatsResultPair, 0, len(s.statsClosers))
	for k := range s.statsClosers {
		clientRes, remoteRes, err := k.Stats()
		if err == nil {
			result = append(result, StatsResultPair{
				Client: clientRes,
				Remote: remoteRes,
			})
		}
	}
	s.statsClosersMutex.Unlock()
	return result
}

func (s *Socks5Proxy) Close() error {
	err := s.tcpListener.Close()
	if err != nil {
		return fmt.Errorf("close: error closing tcp listener: %w", err)
	}
	s.statsClosersMutex.Lock()
	for k := range s.statsClosers {
		_ = k.Close()
	}
	s.statsClosersMutex.Unlock()
	return nil
}
