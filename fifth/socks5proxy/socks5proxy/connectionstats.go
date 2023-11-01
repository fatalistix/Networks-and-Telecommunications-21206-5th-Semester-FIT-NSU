package socks5proxy

import (
	"net"
	"slices"
	"sync/atomic"
	"time"
)

type connectionStats struct {
	readBytes           uint64
	wroteBytes          uint64
	readSinceLastStats  atomic.Uint64
	wroteSinceLastStats atomic.Uint64
	startTime           time.Time
	lastStatsTime       time.Time
	ip                  net.IP
	port                uint16
}

func newConnectionStats(ip net.IP, port uint16) *connectionStats {
	stats := connectionStats{
		readBytes:     0,
		wroteBytes:    0,
		startTime:     time.Now(),
		lastStatsTime: time.Now(),
		ip:            ip,
		port:          port,
	}
	stats.readSinceLastStats.Store(0)
	stats.wroteSinceLastStats.Store(0)
	return &stats
}

func (s *connectionStats) Stats() StatsResult {
	result := StatsResult{
		ReadBytes:           s.readBytes,
		WroteBytes:          s.wroteBytes,
		ReadSinceLastStats:  s.readSinceLastStats.Swap(0),
		WroteSinceLastStats: s.wroteSinceLastStats.Swap(0),
		StartTime:           s.startTime,
		LastStatsTime:       s.lastStatsTime,
		IP:                  slices.Clone(s.ip),
		Port:                s.port,
	}
	s.lastStatsTime = time.Now()
	return result
}

func (s *connectionStats) AddReadBytes(delta uint64) {
	s.readBytes += delta
	s.readSinceLastStats.Add(delta)
}

func (s *connectionStats) AddWroteBytes(delta uint64) {
	s.wroteBytes += delta
	s.wroteSinceLastStats.Add(delta)
}
