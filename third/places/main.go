package main

import (
	"fmt"
	"log"
	"net/http"
	"places/filescanner"
	"places/httpserver"
	"places/search"
	"places/search/graphhopper"
	"places/search/opentripmap"
	"places/search/openweather"

	"golang.org/x/crypto/acme/autocert"
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
	g := graphhopper.NewGraphHopper("your-key")
	t := opentripmap.NewOpenTripMap("your-key")
	w := openweather.NewOpenWeather("your-key")
	placer := search.NewPlacer(g, w, t)
	domainName := "your-dns"
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
}
