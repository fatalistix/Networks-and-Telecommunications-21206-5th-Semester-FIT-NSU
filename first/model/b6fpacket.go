package model

import (
	"bytes"
	"encoding/binary"
	"errors"
	"math"
)

const (
	Report byte = 1
	Leave  byte = 2
)

type B6FPacket struct {
	messageType byte
	id          string
}

func MakeB6FPacketFromBytes(buffer []byte) (B6FPacket, error) {
	if buffer == nil {
		return B6FPacket{}, errors.New("buffer cannot be nil")
	}
	if len(buffer) < 3 {
		return B6FPacket{}, errors.New("buffer too small")
	}
	packet := B6FPacket{}
	packet.messageType = buffer[0]
	switch packet.messageType {
	case Report:
		{
		}
	case Leave:
		{
			return packet, nil
		}
	}
	var length int16
	_ = binary.Read(bytes.NewBuffer(buffer[1:]), binary.BigEndian, &length)

	if int(length) > len(buffer[3:]) {
		length = int16(len(buffer[3:]))
	}

	packet.id = string(buffer[3 : 3+length])

	return packet, nil
}

func MakeB6FPacketReport(id string) (B6FPacket, error) {
	if len(id) > math.MaxInt16 {
		return B6FPacket{}, errors.New("id is too big")
	}

	return B6FPacket{messageType: Report, id: id}, nil
}

func MakeB6FPacketLeave() B6FPacket {
	return B6FPacket{messageType: Leave, id: ""}
}

func (s *B6FPacket) MessageType() byte {
	return s.messageType
}

func (s *B6FPacket) Id() string {
	return s.id
}

func (s *B6FPacket) ToBytes() []byte {
	switch s.messageType {
	case Leave:
		{
			buf := make([]byte, 1)
			buf[0] = Leave
			return buf
		}
	case Report:
		{
			byteSlice := make([]byte, 0)
			buffer := bytes.NewBuffer(byteSlice)
			_ = buffer.WriteByte(s.messageType)
			_ = binary.Write(buffer, binary.BigEndian, int16(len(s.id)))
			_, _ = buffer.WriteString(s.id)
			return buffer.Bytes()

		}
	}
	buf := make([]byte, 1)
	buf[0] = Leave
	return buf
}
