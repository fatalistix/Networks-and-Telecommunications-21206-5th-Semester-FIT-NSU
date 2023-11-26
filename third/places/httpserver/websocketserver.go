package httpserver

import (
	"encoding/json"
	"fmt"
	"github.com/gorilla/websocket"
	"places/search"
)

type websocketServer struct {
	conn   *websocket.Conn
	placer *search.Placer
}

func newWebsocketServer(conn *websocket.Conn, placer *search.Placer) *websocketServer {
	return &websocketServer{
		conn:   conn,
		placer: placer,
	}
}

func (s *websocketServer) serve() error {
	defer s.conn.Close()

	var err error
	var message messageWithType
	var data []byte
	var requestedPlace placeRequest
	var selectedLocation locationSelectRequest

	for {
		_, data, err = s.conn.ReadMessage()
		if err != nil {
			return fmt.Errorf("serve: %w", err)
		}

		err = json.Unmarshal(data, &message)
		if err != nil {
			fmt.Printf("serve: %v\n", err)
			r := response{
				Code: -1,
				Info: err.Error(),
			}
			err = s.conn.WriteJSON(r)
			if err != nil {

				return fmt.Errorf("serve: %w", err)
			}
			continue
		}

		if message.Id == 0 { // Case placeDTO
			err = json.Unmarshal([]byte(message.Info), &requestedPlace)
			if err != nil {
				fmt.Printf("serve: %v\n", err)
				r := response{
					Code: -1,
					Info: err.Error(),
				}
				err = s.conn.WriteJSON(r)
				if err != nil {
					return fmt.Errorf("serve: %w", err)
				}
				continue
			}

			locations, err := s.placer.FindByName(requestedPlace.Place, requestedPlace.Limit)
			if err != nil {
				fmt.Printf("serve: %v\n", err)
				r := response{
					Code: -1,
					Info: err.Error(),
				}
				err = s.conn.WriteJSON(r)
				if err != nil {
					return fmt.Errorf("serve: %w", err)
				}
				continue
			}

			r := response{
				Code: 0,
				Info: locations,
			}

			err = s.conn.WriteJSON(r)
			if err != nil {
				return fmt.Errorf("serve: %w", err)
			}
		} else if message.Id == 1 { // Case info
			fmt.Println("MESSAGE ID 1")
			err = json.Unmarshal([]byte(message.Info), &selectedLocation)
			if err != nil {
				fmt.Printf("serve: %v\n", err)
				r := response{
					Code: -1,
					Info: err.Error(),
				}
				err = s.conn.WriteJSON(r)
				if err != nil {
					return fmt.Errorf("serve: %w", err)
				}
				continue
			}

			locationInfo, err := s.placer.GetLocationInfo(
				selectedLocation.Lat,
				selectedLocation.Lng,
				selectedLocation.Radius,
			)
			if err != nil {
				fmt.Printf("serve: %v\n", err)
				r := response{
					Code: -1,
					Info: err.Error(),
				}
				err = s.conn.WriteJSON(r)
				if err != nil {
					return fmt.Errorf("serve: %w", err)
				}
				continue
			}

			r := response{
				Code: 1,
				Info: locationInfo,
			}

			err = s.conn.WriteJSON(r)
			fmt.Println("SERVED 1")
			if err != nil {
				return fmt.Errorf("serve: %w", err)
			}
		} else {
			fmt.Printf("serve: %v\n", err)
			r := response{
				Code: -1,
				Info: err.Error(),
			}
			err = s.conn.WriteJSON(r)
			if err != nil {
				return fmt.Errorf("serve: %w", err)
			}
			continue
		}
	}
}

type placeRequest struct {
	Place string `json:"place"`
	Limit int    `json:"limit"`
}

type locationSelectRequest struct {
	Lat    float64 `json:"lat"`
	Lng    float64 `json:"lng"`
	Radius int     `json:"radius"`
}

type messageWithType struct {
	Id   int    `json:"id"`
	Info string `json:"info"`
}

type response struct {
	Code int `json:"code"`
	Info any `json:"info"`
}
