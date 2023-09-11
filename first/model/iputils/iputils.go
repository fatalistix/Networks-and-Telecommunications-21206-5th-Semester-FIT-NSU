package iputils

import (
	"errors"
	"net"
)

func IsIPv4(ip string) bool {
	return net.ParseIP(ip).To4() != nil
}

func IsIPv6(ip string) bool {
	return net.ParseIP(ip).To16() != nil
}

func DeletePort(group string) (string, error) {
	for i := len(group) - 1; i >= 0; i-- {
		if group[i] == ':' {
			return group[:i], nil
		}
	}
	return "", errors.New("expected correct IP address with port in the end")
}
