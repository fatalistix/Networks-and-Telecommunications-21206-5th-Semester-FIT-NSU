package socks5proxy

import (
	"fmt"
	"io"
	"slices"
)

type byteSliceMessageSource struct {
	reader io.Reader
	writer io.Writer
	buffer []byte
}

func newByteSliceMessageSource(reader io.Reader, writer io.Writer) *byteSliceMessageSource {
	return &byteSliceMessageSource{
		reader: reader,
		writer: writer,
		buffer: make([]byte, 6+1+256),
	}
}

func (s *byteSliceMessageSource) ReadGreetingMessage() (socks5GreetingMessage, error) {
	var (
		readBytes        int
		totallyReadBytes = 0
		err              error
	)

	readBytes, err = s.reader.Read(s.buffer)
	if err != nil {
		return socks5GreetingMessage{}, fmt.Errorf("get greeting message: %w", err)
	}
	totallyReadBytes += readBytes

	for !greetingMessageEnoughBytes(s.buffer[:totallyReadBytes]) {
		readBytes, err = s.reader.Read(s.buffer[totallyReadBytes:])
		if err != nil {
			return socks5GreetingMessage{}, fmt.Errorf("get greeting message: %w", err)
		}
		totallyReadBytes += readBytes
	}

	if !correctGreetingMessage(s.buffer[:totallyReadBytes]) {
		return socks5GreetingMessage{}, fmt.Errorf("get greeting message: %w", errProtocol)
	}

	return makeMessageFromBytes(s.buffer), nil
}

func (s *byteSliceMessageSource) WriteGreetingAnswer(answer socks5GreetingAnswer) error {
	var (
		totallyWroteBytes = 0
		wroteBytes        int
		err               error
	)
	s.buffer[0] = byte(answer.SocksVersion)
	s.buffer[1] = answer.AuthMethod

	for totallyWroteBytes != 2 {
		wroteBytes, err = s.writer.Write(s.buffer[totallyWroteBytes:2])
		if err != nil {
			return fmt.Errorf("write greeting answer: %w", err)
		}
		totallyWroteBytes += wroteBytes
	}
	return nil
}

func (s *byteSliceMessageSource) ReadClientMessage() (socks5ClientMessage, error) {
	var (
		totallyReadBytes = 0
		readBytes        int
		err              error
	)

	readBytes, err = s.reader.Read(s.buffer)
	if err != nil {
		return socks5ClientMessage{}, fmt.Errorf("read client message: %w", err)
	}
	totallyReadBytes += readBytes

	for {
		flag, err := clientMessageEnoughBytes(s.buffer[:totallyReadBytes])
		if err != nil {
			return socks5ClientMessage{}, fmt.Errorf("read client message: %w", err)
		}
		if flag {
			break
		}
		readBytes, err = s.reader.Read(s.buffer)
		if err != nil {
			return socks5ClientMessage{}, fmt.Errorf("read client message: %w", err)
		}
		totallyReadBytes += readBytes
	}

	if !correctClientMessage(s.buffer[:totallyReadBytes]) {
		return socks5ClientMessage{}, fmt.Errorf("read client message: %w", errProtocol)
	}

	return makeClientMessageFromBytes(s.buffer[:totallyReadBytes]), nil
}

func (s *byteSliceMessageSource) WriteServerAnswer(answer socks5ServerAnswer) error {
	var (
		totallyWroteBytes = 0
		wroteBytes        int
		messageSize       int
		err               error
	)

	s.buffer[0] = answer.SocksVersion.GetByte()
	s.buffer[1] = answer.AnswerCode.GetByte()
	s.buffer[2] = 0
	s.buffer[3] = answer.AddressType.GetByte()

	switch answer.AddressType {
	case ipv4:
		{
			copy(s.buffer[4:4+4], answer.AddressPayload)
			s.buffer[8] = byte(answer.Port / 256)
			s.buffer[9] = byte(answer.Port & 256)
			messageSize = 6 + 4
		}
	case domainName:
		{
			s.buffer[4] = byte(len(answer.AddressPayload))
			copy(s.buffer[5:5+len(answer.AddressPayload)], answer.AddressPayload)
			s.buffer[5+len(answer.AddressPayload)] = byte(answer.Port / 256)
			s.buffer[5+len(answer.AddressPayload)+1] = byte(answer.Port & 256)
			messageSize = 6 + 1 + len(answer.AddressPayload)
		}
	case ipv6:
		{
			copy(s.buffer[4:4+16], answer.AddressPayload)
			s.buffer[4+16] = byte(answer.Port / 256)
			s.buffer[4+16+1] = byte(answer.Port & 256)
			messageSize = 6 + 16
		}
	}

	for totallyWroteBytes != messageSize {
		wroteBytes, err = s.writer.Write(s.buffer[totallyWroteBytes:messageSize])
		if err != nil {
			return fmt.Errorf("write server answer: %w", err)
		}
		totallyWroteBytes += wroteBytes
	}
	return nil
}

func greetingMessageEnoughBytes(info []byte) bool {
	if len(info) < 2 {
		return false
	}
	return len(info) >= int(2+info[1])
}

func correctGreetingMessage(info []byte) bool {
	if info[0] != 5 {
		return false
	}
	return len(info) == int(2+info[1])
}

func makeMessageFromBytes(info []byte) socks5GreetingMessage {
	return socks5GreetingMessage{
		SocksVersion:     socks5,
		NumOfAuthMethods: info[1],
		AuthMethods:      info[2:],
	}
}

func clientMessageEnoughBytes(info []byte) (bool, error) {
	if len(info) < 4 {
		return false, nil
	}

	switch info[3] {
	case 1:
		return len(info) >= 6+4, nil
	case 3:
		return len(info) >= 6+1+int(info[4]), nil
	case 4:
		return len(info) >= 6+16, nil
	default:
		return false, fmt.Errorf("client message enough bytes: invalid message format: expected 0x01, 0x03 or 0x04 at 4-th byte, but got %v: %w", info[3], errProtocol)
	}
}

func correctClientMessage(info []byte) bool {
	if info[0] != 5 {
		return false
	}
	if info[1] != 1 && info[1] != 2 && info[1] != 3 {
		return false
	}
	if info[2] != 0 {
		return false
	}
	switch info[3] {
	case 1:
		return len(info) == 6+4
	case 3:
		return len(info) == 6+1+int(info[4])
	case 4:
		return len(info) == 6+16
	default:
		return false
	}
}

func makeClientMessageFromBytes(info []byte) socks5ClientMessage {
	switch info[3] {
	case 1:
		return socks5ClientMessage{
			SocksVersion:   socks5,
			MessageCode:    messageCode(info[1]),
			AddressType:    addressType(info[3]),
			AddressPayload: slices.Clone(info[4 : 4+4]),
			Port:           uint16(info[8])*256 + uint16(info[9]),
		}
	case 3:
		return socks5ClientMessage{
			SocksVersion:   socks5,
			MessageCode:    messageCode(info[1]),
			AddressType:    addressType(info[3]),
			AddressPayload: slices.Clone(info[5 : 5+info[4]]),
			Port:           uint16(info[5+info[4]])*256 + uint16(info[5+info[4]+1]),
		}
	case 4:
		return socks5ClientMessage{
			SocksVersion:   socks5,
			MessageCode:    messageCode(info[1]),
			AddressType:    addressType(info[3]),
			AddressPayload: slices.Clone(info[4 : 4+16]),
			Port:           uint16(info[4+16])*256 + uint16(info[4+16+1]),
		}
	default:
		panic("make client message from bytes: unexpected address type")
	}
}
