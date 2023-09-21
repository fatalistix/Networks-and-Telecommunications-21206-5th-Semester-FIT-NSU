package ip4

import (
	"errors"
	"strconv"

	"networks/first/model"
)

var _ model.LocalMulticastValidator = IP4LocalMulticastValidator{}

type IP4LocalMulticastValidator struct {
}

func MakeIP4LocalMulticastValidator() IP4LocalMulticastValidator {
	return IP4LocalMulticastValidator{}
}

func (s IP4LocalMulticastValidator) Validate(ip string) error {
	for i := range IP4LocalMulticastPrefix {
		if ip[i] != IP4LocalMulticastPrefix[i] {
			return errors.New("invalid first 3 bytes or format, expected 224.0.0.*")
		}
	}

	lastNum, err := strconv.Atoi(ip[len(IP4LocalMulticastPrefix):])
	if err != nil {
		return err
	}

	if 0 <= lastNum && lastNum <= 255 {
		return nil
	} else {
		return errors.New("invalid fourth byte, expected 0 <= byte <= 255, but got " + strconv.Itoa(lastNum))
	}
}
