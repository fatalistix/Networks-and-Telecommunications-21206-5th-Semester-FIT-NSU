package main

import (
	"fmt"
	"log"
)

func main() {
	var port int16
	_, err := fmt.Scanln(&port)
	if err != nil {
		log.Fatal("Error occurred during reading port from stdin: ", err.Error())
	}

}
