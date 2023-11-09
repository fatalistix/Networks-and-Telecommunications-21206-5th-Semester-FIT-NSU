package main

import (
	"fmt"
	"log"
	"runtime"
	"socks5proxy/socks5proxy"
	"time"
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

	go func() {
		err = proxy.Serve()
		if err != nil {
			log.Fatal("main: error proxy's serve: ", err.Error())
		}
	}()

	//ticker := time.NewTicker(time.Second)
	//mbDivider := float64(1024 * 1024)
	//for range ticker.C {
	//	result := proxy.Stats()
	//	//fmt.Println("<==> UPDATING INFO ", len(result), " <==>")
	//	var sumAverageReadSpeedMB float64
	//	var sumAverageWriteSpeedMB float64
	//	var sumMomentReadSpeedMB float64
	//	var sumMomentWriteSpeedMB float64
	//	for _, v := range result {
	//		now := time.Now()
	//		averageReadSpeedMB := float64(v.ReadBytes) / now.Sub(v.StartTime).Seconds() / mbDivider
	//		averageWriteSpeedMB := float64(v.WroteBytes) / now.Sub(v.StartTime).Seconds() / mbDivider
	//		momentReadSpeedMB := float64(v.ReadSinceLastStats) / now.Sub(v.LastStatsTime).Seconds() / mbDivider
	//		momentWriteSpeedMB := float64(v.WroteSinceLastStats) / now.Sub(v.LastStatsTime).Seconds() / mbDivider
	//		sumAverageReadSpeedMB += averageReadSpeedMB
	//		sumAverageWriteSpeedMB += averageWriteSpeedMB
	//		sumMomentReadSpeedMB += momentReadSpeedMB
	//		sumMomentWriteSpeedMB += momentWriteSpeedMB
	//		//fmt.Println(v.IP, ":", v.Port, " -> average: read - ", averageReadSpeedMB, " MB/s, write - ", averageWriteSpeedMB, " MB/s, moment: read - ", momentReadSpeedMB, " MB/s, write - ", momentWriteSpeedMB, " MB/s")
	//	}
	//	fmt.Println("average: read -", sumAverageReadSpeedMB, "MB/s, write -", sumAverageWriteSpeedMB, "MB/s, moment: read - ", sumMomentReadSpeedMB, " MB/s, write - ", sumMomentWriteSpeedMB, " MB/s")
	//}

	var str string
	_, _ = fmt.Scan(&str)
	err = proxy.Close()
	if err != nil {
		log.Fatal("main: error proxy's close: ", err.Error())
	}
	time.Sleep(15 * time.Second)

	fmt.Println(runtime.NumGoroutine())
}
