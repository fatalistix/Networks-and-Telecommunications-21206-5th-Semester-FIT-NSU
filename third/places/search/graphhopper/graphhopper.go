package graphhopper

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strconv"
)

type GraphHopper struct {
	apiKey     string
	urlPrefix  string
	geocodeUri string
}

func NewGraphHopper(apiKey string) *GraphHopper {
	return &GraphHopper{
		apiKey:     apiKey,
		urlPrefix:  "https://graphhopper.com/api/1",
		geocodeUri: "/geocode",
	}
}

func (s *GraphHopper) GetGeocoding(location string, limit int) (*GetGeocodingResponse, error) {
	var (
		request              *http.Request
		err                  error
		query                url.Values
		response             *http.Response
		responseBody         []byte
		getGeocodingResponse = GetGeocodingResponse{}
	)

	request, err = http.NewRequest("GET", s.urlPrefix+s.geocodeUri, nil)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	query = request.URL.Query()
	query.Add("q", location)
	query.Add("locale", "en")
	query.Add("limit", strconv.Itoa(limit))
	query.Add("reverse", "false")
	query.Add("debug", "false")
	query.Add("provider", "default")
	query.Add("key", s.apiKey)
	request.URL.RawQuery = query.Encode()

	response, err = http.DefaultClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	if response.StatusCode != http.StatusOK {
		//defer response.Body.Close()
		//responseBody, err = io.ReadAll(response.Body)
		//fmt.Println(string(responseBody))
		return nil, fmt.Errorf("get geocoding: request finished with unexpected code: %v", response.StatusCode)
	}

	defer response.Body.Close()
	responseBody, err = io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	err = json.Unmarshal(responseBody, &getGeocodingResponse)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	return &getGeocodingResponse, nil
}

func (s *GraphHopper) GetGeocodingAsBytes(location string, limit int32) ([]byte, error) {
	var (
		request      *http.Request
		err          error
		query        url.Values
		response     *http.Response
		responseBody []byte
	)

	request, err = http.NewRequest("GET", s.urlPrefix+s.geocodeUri, nil)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	query = request.URL.Query()
	query.Add("q", location)
	query.Add("locale", "en")
	query.Add("limit", strconv.Itoa(int(limit)))
	query.Add("reverse", "false")
	query.Add("debug", "false")
	query.Add("provider", "default")
	query.Add("key", s.apiKey)
	request.URL.RawQuery = query.Encode()

	response, err = http.DefaultClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	if response.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("get geocoding: request finished with unexpected code: %v", response.StatusCode)
	}

	defer response.Body.Close()
	responseBody, err = io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("get geocoding: %w", err)
	}

	return responseBody, nil
}

type GetGeocodingResponse struct {
	Hits []struct {
		OsmId    int    `json:"osm_id"`
		OsmType  string `json:"osm_type"`
		Country  string `json:"country"`
		OsmKey   string `json:"osm_key"`
		City     string `json:"city"`
		OsmValue string `json:"osm_value"`
		Postcode string `json:"postcode"`
		Name     string `json:"name"`
		Point    struct {
			Lng float64 `json:"lng"`
			Lat float64 `json:"lat"`
		} `json:"point"`
	} `json:"hits"`
	Took int `json:"took"`
}
