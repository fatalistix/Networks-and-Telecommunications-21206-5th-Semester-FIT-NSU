package model

type Multicaster interface {
	Connect(group string, port int) error
	Multicast(buffer []byte) error
	Close() error
	GetLocalAddress() string
}
