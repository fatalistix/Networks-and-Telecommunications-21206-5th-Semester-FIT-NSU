package socks5proxy

import (
	"net"
	"time"
)

type StatsResult struct {
	ReadBytes           uint64
	WroteBytes          uint64
	ReadSinceLastStats  uint64
	WroteSinceLastStats uint64
	StartTime           time.Time
	LastStatsTime       time.Time
	IP                  net.IP
	Port                uint16
}
