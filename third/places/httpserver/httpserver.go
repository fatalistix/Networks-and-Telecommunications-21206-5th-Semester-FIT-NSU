package httpserver

import (
	"fmt"
	"github.com/gorilla/websocket"
	"net/http"
	"os"
	"places/search"
	"strings"
)

type PlacesHttpServer struct {
	mainHtml []byte
	addons   map[string][]byte
	placer   *search.Placer
	upgrader websocket.Upgrader
}

func NewPlacesHttpServer(rootProjectPath, pathToHTML string, addonFilesPaths []string, placer *search.Placer) (*PlacesHttpServer, error) {
	mainHtml, err := os.ReadFile(pathToHTML)
	if err != nil {
		return nil, fmt.Errorf("new places http server: %w", err)
	}
	addons := make(map[string][]byte)
	addons["/"] = mainHtml
	for _, v := range addonFilesPaths {
		file, err := os.ReadFile(v)
		if err != nil {
			return nil, fmt.Errorf("new places http server: %w", err)
		}
		newV := strings.Replace(v, rootProjectPath, "", 1)
		addons[newV] = file
	}
	return &PlacesHttpServer{
		mainHtml: mainHtml,
		addons:   addons,
		placer:   placer,
		upgrader: websocket.Upgrader{},
	}, nil
}

func (s *PlacesHttpServer) GetOnRoot(w http.ResponseWriter, r *http.Request) {
	path := r.URL.Path
	fmt.Println(path)
	v, contains := s.addons[path]
	if !contains {
		w.WriteHeader(http.StatusNotFound)
		return
	}

	if strings.HasSuffix(path, ".css") {
		w.Header().Set("Content-Type", "text/css")
		w.Header().Set("X-Content-Type-Options", "nosniff")
		w.Header().Set("Transfer-Encoding", "UTF-8")
	}
	if strings.HasSuffix(path, ".js") {
		w.Header().Set("Content-Type", "text/javascript")
		w.Header().Set("X-Content-Type-Options", "nosniff")
		w.Header().Set("Transfer-Encoding", "UTF-8")
	}
	if strings.HasSuffix(path, ".png") {
		w.Header().Set("Content-Type", "image/png")
	}
	w.WriteHeader(http.StatusOK)
	_, err := w.Write(v)
	if err != nil {
		w.WriteHeader(http.StatusInternalServerError)
	}

	//if strings.Contains(r.Header["Accept"][0], "text/html") {
	//	_, err := w.Write(s.mainHtml)
	//	if err != nil {
	//		w.WriteHeader(http.StatusInternalServerError)
	//	}
	//} else {
	//	for k, v := range s.addons {
	//		_, contains := store.Get(k)
	//		if contains {
	//			continue
	//		}
	//		if strings.HasSuffix(k, ".js") {
	//			w.Header().Add("Content-Type", "application/javascript")
	//		}
	//		_, err := w.Write(v)
	//		if err != nil {
	//			w.WriteHeader(http.StatusInternalServerError)
	//			_ = session.Destroy(context.Background(), w, r)
	//			return
	//		}
	//		store.Set(k, true)
	//		err = store.Save()
	//		if err != nil {
	//			_ = session.Destroy(context.Background(), w, r)
	//			return
	//		}
	//		break
	//	}
	//}
}

func (s *PlacesHttpServer) WebSocket(w http.ResponseWriter, r *http.Request) {
	conn, err := s.upgrader.Upgrade(w, r, nil)
	if err != nil {
		return
	}
	go func() {
		server := newWebsocketServer(conn, s.placer)
		err := server.serve()
		if err != nil {
			_, _ = fmt.Fprintln(os.Stderr, fmt.Errorf("web socket request handler: %w", err).Error())
		}
	}()
}
