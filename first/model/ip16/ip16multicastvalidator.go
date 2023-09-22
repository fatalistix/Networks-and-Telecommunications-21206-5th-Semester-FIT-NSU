package ip16

import (
	"errors"
	"strconv"
	"strings"

	"networks/first/model"
)

var _ model.LocalMulticastValidator = IP16LocalMulticastValidator{}

type IP16LocalMulticastValidator struct {
}

func MakeIP16LocalMulticastValidator() IP16LocalMulticastValidator {
	return IP16LocalMulticastValidator{}
}

func (s IP16LocalMulticastValidator) Validate(ip string) error {
	upCaseIP := strings.ToUpper(ip)
	for i := range IP16LocalMulticastPrefix {
		if IP16LocalMulticastPrefix[i] != upCaseIP[i] {
			return errors.New(
				"invalid first 12 bytes or format, excpected (ignore case) ff02:0:0:0:0:1",
			)
		}
	}

	colonPos := 0

	for i := len(IP16LocalMulticastPrefix); i < len(ip); i++ {
		if ip[i] == ':' {
			colonPos = i
			break
		}
	}

	if colonPos == 0 {
		return errors.New(
			"got invalid IPv6 multicast address: expected 7 separators, but found only 6",
		)
	}

	penultimateNum, err := strconv.ParseInt(ip[len(IP16LocalMulticastPrefix):colonPos], 16, 0)
	if err != nil {
		return err
	}

	if penultimateNum < 0xff00 || 0xffff < penultimateNum {
		return errors.New(
			"invalid seventh 2 bytes of IPv6 local multicast address: expected 0xFF00 <= value <= 0xFFFF, but got " + strconv.Itoa(
				int(penultimateNum),
			),
		)
	}

	lastNum, err := strconv.ParseInt(ip[colonPos+1:], 16, 0)
	if err != nil {
		return err
	}

	if lastNum < 0x0000 || 0xffff < lastNum {
		return errors.New(
			"invalid eighth 2 bytes of IPv6 local multicast address: expected 0x0000 <= value <= 0xFFFF, but got " + strconv.Itoa(
				int(lastNum),
			),
		)
	}

	return nil
}
