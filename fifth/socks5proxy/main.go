package main

import (
	"fmt"
	"log"
	"socks5proxy/socks5proxy"
)

func main() {
	var port uint16
	_, err := fmt.Scanln(&port)
	if err != nil {
		log.Fatal("main: error occurred during reading port from stdin: ", err.Error())
	}

	proxy := socks5proxy.NewSocks5Proxy()
	err = proxy.Open(port)
	if err != nil {
		log.Fatal("main: error opening proxy on port ", port, ": ", err.Error())
	}

	err = proxy.Serve()
	if err != nil {
		log.Fatal("main: error proxy's serve: ", err.Error())
	}

	//conn, _ := net.Dial("tcp", "localhost:6999")
	//time.Sleep(time.Second * 7)
	//
	//fmt.Println(conn.Read(make([]byte, 100)))

	//start := time.Now()
	////conn, err := net.Dial("tcp", "208.102.51.6:58208")
	//conn, err := net.Dial("tcp", "ip-208-102-51-6.static.fuse.net:58208")
	//
	//fmt.Println(time.Now().Sub(start))
	//fmt.Println(conn)
	//if err != nil {
	//	fmt.Println(err.Error())
	//}
	//conn.Close()
	//err = conn.Close()
	//fmt.Println("error after close: ", err.Error())

	//conn, _ := net.Dial("tcp", "localhost:7878")
	//go func() {
	//	buffer := make([]byte, 10)
	//	_, err := conn.Read(buffer)
	//	if err != nil {
	//		fmt.Println(err.Error())
	//		return
	//	}
	//	fmt.Println("returned")
	//}()
	//time.Sleep(time.Second * 2)
	//conn.Close()
	//fmt.Println("CLOSED")
	//time.Sleep(time.Second * 5)
	//
	//networks := []string{"google.com:https", "ya.ru:https", "nsu.ru:https", "nsumedia.ru:https", "vk.com:https"}
	//for _, v := range networks {
	//	res, err := net.Dial("tcp", v)
	//	fmt.Println(res, err)
	//}
	//
	//go func() {
	//	for {
	//		time.Sleep(time.Second)
	//	}
	//}()
	//
	//var x chan bool
	//x <- true
	//fmt.Println("exiting")
	//conn, err := net.Dial("tcp", "localhost:7878")
	//if err != nil {
	//	fmt.Println(err.Error())
	//	return
	//}
	//fmt.Println("before sleep")
	//time.Sleep(time.Second * 10)
	//fmt.Println("woke up")
	//res, err := conn.Write(make([]byte, 20))
	//if err != nil {
	//	fmt.Println(err.Error())
	//}
	//fmt.Println("FINISHED: ", res)
}
