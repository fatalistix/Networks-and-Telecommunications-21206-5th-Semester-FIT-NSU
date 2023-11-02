package socks5proxy

import "errors"

var errProtocol = errors.New("protocol error")

type socksVersion byte
type messageCode byte
type addressType byte
type answerCode byte

func (s socksVersion) GetByte() byte {
	return byte(s)
}

func (s messageCode) GetByte() byte {
	return byte(s)
}

func (s addressType) GetByte() byte {
	return byte(s)
}

func (s answerCode) GetByte() byte {
	return byte(s)
}

const (
	socks5 socksVersion = iota + 5
)

const (
	_ messageCode = iota
	establishTCP
	bindPort
	associateUDPPort
)

const (
	_ addressType = iota
	ipv4
	_
	domainName
	ipv6
)

const (
	requestGranted answerCode = iota
	generalFailure
	notAllowedByRuleset
	networkUnreachable
	hostUnreachable
	connectionRefusedByDestinationHost
	ttlExpired
	commandNotSupported
	protocolError = iota - 1
	addressTypeNotSupported
)

type socks5GreetingMessage struct {
	SocksVersion     socksVersion
	NumOfAuthMethods byte
	AuthMethods      []byte
}

type socks5GreetingAnswer struct {
	SocksVersion socksVersion
	AuthMethod   byte
}

type socks5ClientMessage struct {
	SocksVersion   socksVersion
	MessageCode    messageCode
	AddressType    addressType
	AddressPayload []byte
	Port           uint16
}

type socks5ServerAnswer struct {
	SocksVersion   socksVersion
	AnswerCode     answerCode
	AddressType    addressType
	AddressPayload []byte
	Port           uint16
}
