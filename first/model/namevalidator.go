package model

import (
	"errors"
	"math"
)

type NameValidator struct {
}

func (v NameValidator) Validate(str string) error {
	if len(str) <= math.MaxInt16 {
		return nil
	}
	return errors.New("str cannot be bigger, then 32000")
}

func MakeNameValidator() NameValidator {
	return NameValidator{}
}
