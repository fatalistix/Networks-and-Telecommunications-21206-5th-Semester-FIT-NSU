package socks5proxy

//type socksVersion byte
//
//const (
//	socks5 socksVersion = iota + 5
//)

type socks5GreetingMessage struct {
	SocksVersion     byte
	NumOfAuthMethods byte
	AuthMethods      []byte
}

type socks5GreetingAnswer struct {
	SocksVersion byte
	AuthMethod   byte
}

//type messageCode byte
//
//const (
//	_ messageCode = iota
//	establishTCP
//	bindPort
//	associateUDPPort
//)

//type addressType byte
//
//const (
//	_ addressType = iota
//	ipv4
//	_
//	domainName
//	ipv6
//)

type socks5ClientMessage struct {
	SocksVersion   byte
	MessageCode    byte
	AddressType    byte
	AddressPayload []byte
	Port           uint16
}

//type answerCode byte
//
//const (
//	requestGranted answerCode = iota
//)

type socks5ServerAnswer struct {
	SocksVersion   byte
	AnswerCode     byte
	AddressType    byte
	AddressPayload []byte
	Port           uint16
}
