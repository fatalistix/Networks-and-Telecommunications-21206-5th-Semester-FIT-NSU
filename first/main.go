package main

import (
	"fyne.io/fyne/v2/app"
	"fyne.io/fyne/v2/widget"
)

func main() {
	application := app.New()
	window := application.NewWindow("Hello, World")

	window.SetContent(widget.NewLabel("Hello, World!!!!!!!"))
	window.ShowAndRun()
}

// func main() {
// 	ur, err := reciever.Connect("235.0.0.1:6969")
// 	fmt.Println(ur, err)
// 	buffer := make([]byte, 5)
// 	ur.Recieve(buffer)
// 	fmt.Println(buffer)
// }

// addr, _ := net.ResolveUDPAddr("udp", "localhost:8887")
// conn, err := net.ListenUDP("udp", addr)
//
// if err != nil {
// 	fmt.Println(err)
// 	return
// }
// fmt.Println(addr)
// fmt.Println(conn)
//
// defer conn.Close()
//
// buffer := make([]byte, 256)
// n, raddr, _ := conn.ReadFromUDP(buffer)
//
// fmt.Println(n)
// fmt.Println(raddr)
// fmt.Println(strings.Trim(string(buffer), " "))
