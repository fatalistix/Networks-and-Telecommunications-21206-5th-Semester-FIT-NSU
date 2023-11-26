package openweather

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"net/url"
)

type OpenWeather struct {
	apiKey string
	url    string
}

func NewOpenWeather(apiKey string) *OpenWeather {
	return &OpenWeather{
		apiKey: apiKey,
		url:    "https://api.openweathermap.org/data/2.5/weather",
	}
}

func (s *OpenWeather) GetWeather(lat float64, lon float64) (*GetWeatherResponse, error) {
	var (
		request            *http.Request
		err                error
		query              url.Values
		response           *http.Response
		responseBody       []byte
		getWeatherResponse = GetWeatherResponse{}
	)

	request, err = http.NewRequest("GET", s.url, nil)
	if err != nil {
		return nil, fmt.Errorf("get weather: %w", err)
	}

	query = request.URL.Query()
	query.Add("lat", fmt.Sprintf("%f", lat))
	query.Add("lon", fmt.Sprintf("%f", lon))
	query.Add("appid", s.apiKey)
	query.Add("units", "metric")
	query.Add("lang", "en")
	request.URL.RawQuery = query.Encode()

	response, err = http.DefaultClient.Do(request)
	if err != nil {
		return nil, fmt.Errorf("get weather: %w", err)
	}

	defer response.Body.Close()
	responseBody, err = io.ReadAll(response.Body)
	if err != nil {
		return nil, fmt.Errorf("get weather: %w", err)
	}

	err = json.Unmarshal(responseBody, &getWeatherResponse)
	if err != nil {
		return nil, fmt.Errorf("get weather: %w", err)
	}

	return &getWeatherResponse, nil
}

type GetWeatherResponse struct {
	Coord struct {
		Lon float64 `json:"lon"`
		Lat float64 `json:"lat"`
	} `json:"coord"`
	Weather []struct {
		Id          int    `json:"id"`
		Main        string `json:"main"`
		Description string `json:"description"`
		Icon        string `json:"icon"`
	} `json:"weather"`
	Base string `json:"base"`
	Main struct {
		Temp      float64 `json:"temp"`
		FeelsLike float64 `json:"feels_like"`
		TempMin   float64 `json:"temp_min"`
		TempMax   float64 `json:"temp_max"`
		Pressure  int     `json:"pressure"`
		Humidity  int     `json:"humidity"`
		SeaLevel  int     `json:"sea_level"`
		GrndLevel int     `json:"grnd_level"`
	} `json:"main"`
	Visibility int `json:"visibility"`
	Wind       struct {
		Speed float64 `json:"speed"`
		Deg   int     `json:"deg"`
		Gust  float64 `json:"gust"`
	} `json:"wind"`
	Rain struct {
		Optional map[string]float64
	} `json:"rain"`
	Snow struct {
		Optional map[string]float64
	} `json:"snow"`
	Clouds struct {
		All int `json:"all"`
	} `json:"clouds"`
	Dt  int `json:"dt"`
	Sys struct {
		Type    int    `json:"type"`
		Id      int    `json:"id"`
		Message string `json:"message"`
		Country string `json:"country"`
		Sunrise int    `json:"sunrise"`
		Sunset  int    `json:"sunset"`
	} `json:"sys"`
	Timezone int    `json:"timezone"`
	Id       int    `json:"id"`
	Name     string `json:"name"`
	Cod      int    `json:"cod"`
}
