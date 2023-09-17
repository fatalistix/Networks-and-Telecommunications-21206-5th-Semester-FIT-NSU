package model

type LocalMulticastValidator interface {
	Validate(ip string) error
}
