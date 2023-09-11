package multicaster

type Multicaster interface {
	Multicast(buffer []byte) error
	Close() error
}
