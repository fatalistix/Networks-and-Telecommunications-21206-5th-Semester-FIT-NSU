package reciever

type Reciever interface {
	Recieve(buffer []byte) (int, error)
	Close() error
}
