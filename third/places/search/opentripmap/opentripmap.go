package opentripmap

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
	"strconv"
)

type OpenTripMap struct {
	urlPrefix string
	apiKey    string
}

func NewOpenTripMap(apiKey string) *OpenTripMap {
	return &OpenTripMap{
		urlPrefix: "https://api.opentripmap.com/0.1",
		apiKey:    apiKey,
	}
}

func (s *OpenTripMap) GetPlacesInRadius(lat float64, lon float64, radius int) ([]*NearbyPlace, error) {
	var (
		request                 *http.Request
		err                     error
		query                   url.Values
		response                *http.Response
		responseBody            []byte
		getNearbyPlacesResponse []*NearbyPlace
	)

	request, err = http.NewRequest("GET", s.urlPrefix+"/en"+"/places/radius", nil)
	if err != nil {
		return nil, fmt.Errorf("get places in radius: %w", err)
	}

	query = request.URL.Query()
	query.Add("radius", strconv.Itoa(radius))
	query.Add("lon", fmt.Sprint(lon))
	query.Add("lat", fmt.Sprint(lat))
	query.Add("format", "json")
	query.Add("apikey", s.apiKey)
	request.URL.RawQuery = query.Encode()

	response, err = http.DefaultClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("get places in radius: %w", err)
	}

	if response.StatusCode != http.StatusOK {
		defer response.Body.Close()
		responseBody, err = io.ReadAll(response.Body)
		fmt.Println(string(responseBody))
		return nil, fmt.Errorf("get places in radius: unexpected response status code: %v", response.StatusCode)
	}

	defer response.Body.Close()
	responseBody, err = io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("get places in radius: %w", err)
	}

	err = json.Unmarshal(responseBody, &getNearbyPlacesResponse)
	if err != nil {
		return nil, fmt.Errorf("get places in radius: %w", err)
	}

	return getNearbyPlacesResponse, nil
}

type NearbyPlace struct {
	Name     string `json:"name"`
	Osm      string `json:"osm"`
	Xid      string `json:"xid"`
	Wikidata string `json:"wikidata"`
	Kind     string `json:"kind"`
	Point    struct {
		Lon float64 `json:"lon"`
		Lat float64 `json:"lat"`
	} `json:"point"`
}

func (s *OpenTripMap) GetPlaceDescription(xid string) (*GetPlaceDescriptionResponse, error) {
	var (
		request                     *http.Request
		err                         error
		query                       url.Values
		response                    *http.Response
		responseBody                []byte
		getPlaceDescriptionResponse = GetPlaceDescriptionResponse{}
	)

	request, err = http.NewRequest("GET", s.urlPrefix+"/en"+"/places/xid/"+xid, nil)
	if err != nil {
		return nil, fmt.Errorf("get place description: %w", err)
	}

	query = request.URL.Query()
	query.Add("apikey", s.apiKey)
	request.URL.RawQuery = query.Encode()

	response, err = http.DefaultClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("get place description: %w", err)
	}

	if response.StatusCode != http.StatusOK {
		return nil, fmt.Errorf("get place description: unexpected response status code: %v", response.StatusCode)
	}

	defer response.Body.Close()
	responseBody, err = io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("get place description: %w", err)
	}

	err = json.Unmarshal(responseBody, &getPlaceDescriptionResponse)
	if err != nil {
		return nil, fmt.Errorf("get place description: %w", err)
	}

	return &getPlaceDescriptionResponse, nil
}

type GetPlaceDescriptionResponse struct {
	Kinds   string `json:"kinds"`
	Sources struct {
		Geometry   string   `json:"geometry"`
		Attributes []string `json:"attributes"`
	} `json:"sources"`
	Bbox struct {
		LatMax float64 `json:"lat_max"`
		LatMin float64 `json:"lat_min"`
		LonMax float64 `json:"lon_max"`
		LonMin float64 `json:"lon_min"`
	} `json:"bbox"`
	Point struct {
		Lon float64 `json:"lon"`
		Lat float64 `json:"lat"`
	} `json:"point"`
	Osm       string `json:"osm"`
	Otm       string `json:"otm"`
	Xid       string `json:"xid"`
	Name      string `json:"name"`
	Wikipedia string `json:"wikipedia"`
	Image     string `json:"image"`
	Wikidata  string `json:"wikidata"`
	Rate      string `json:"rate"`
	Info      struct {
		Descr     string `json:"descr"`
		Image     string `json:"image"`
		ImgWidth  int    `json:"img_width"`
		Src       string `json:"src"`
		SrcId     int    `json:"src_id"`
		ImgHeight int    `json:"img_height"`
	} `json:"info"`
}
