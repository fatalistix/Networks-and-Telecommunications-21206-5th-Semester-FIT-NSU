package main

import (
	"fmt"
	"golang.org/x/crypto/acme/autocert"
	"log"
	"net/http"
	"places/filescanner"
	"places/httpserver"
	"places/search"
	"places/search/graphhopper"
	"places/search/opentripmap"
	"places/search/openweather"
)

func getSiteFiles() ([]string, error) {
	css, err := filescanner.ScanForSuffix("./site", ".css")
	if err != nil {
		return nil, fmt.Errorf("get site files: %w", err)
	}
	js, err := filescanner.ScanForSuffix("./site", ".js")
	if err != nil {
		return nil, fmt.Errorf("get site files: %w", err)
	}
	png, err := filescanner.ScanForSuffix("./site", ".png")
	if err != nil {
		return nil, fmt.Errorf("get site files: %w", err)
	}
	return append(append(css, js...), png...), nil
}

func main() {
	g := graphhopper.NewGraphHopper("269bd77f-9cef-4491-9db7-7736da916f00")
	t := opentripmap.NewOpenTripMap("5ae2e3f221c38a28845f05b60988d8d67eb0469edbcdfcf2bc1b8ff2")
	w := openweather.NewOpenWeather("fa325a4c8f9c6731c20ae49d32c9e075")
	placer := search.NewPlacer(g, w, t)
	domainName := "nsu-places.ddns.net"
	addons, err := getSiteFiles()
	if err != nil {
		log.Fatal(err)
	}

	httpServer, err := httpserver.NewPlacesHttpServer("./site", "./site/main.html", addons, placer)
	if err != nil {
		log.Fatal(err)
	}

	http.HandleFunc("/", httpServer.GetOnRoot)
	http.HandleFunc("/ws", httpServer.WebSocket)

	m := &autocert.Manager{
		Cache:      autocert.DirCache("secret-dir"),
		Prompt:     autocert.AcceptTOS,
		HostPolicy: autocert.HostWhitelist(domainName),
	}
	s := &http.Server{
		Addr:      ":6969",
		TLSConfig: m.TLSConfig(),
	}
	log.Fatal(s.ListenAndServeTLS("", ""))

	//log.Fatal(http.Serve(autocert.NewListener(domainName), nil))
	//log.Fatal(http.ListenAndServe(":8069", nil))
}
