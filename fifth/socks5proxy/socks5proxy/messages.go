package socks5proxy

type socksVersion byte

const (
	socks5 socksVersion = iota + 5
)

func (s socksVersion) GetByte() byte {
	return byte(s)
}

type socks5GreetingMessage struct {
	SocksVersion     socksVersion
	NumOfAuthMethods byte
	AuthMethods      []byte
}

type socks5GreetingAnswer struct {
	SocksVersion socksVersion
	AuthMethod   byte
}

type messageCode byte

const (
	_ messageCode = iota
	establishTCP
	bindPort
	associateUDPPort
)

func (s messageCode) GetByte() byte {
	return byte(s)
}

type addressType byte

const (
	_ addressType = iota
	ipv4
	_
	domainName
	ipv6
)

func (s addressType) GetByte() byte {
	return byte(s)
}

type socks5ClientMessage struct {
	SocksVersion   socksVersion
	MessageCode    messageCode
	AddressType    addressType
	AddressPayload []byte
	Port           uint16
}

type answerCode byte

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

func (s answerCode) GetByte() byte {
	return byte(s)
}

type socks5ServerAnswer struct {
	SocksVersion   socksVersion
	AnswerCode     answerCode
	AddressType    addressType
	AddressPayload []byte
	Port           uint16
}
