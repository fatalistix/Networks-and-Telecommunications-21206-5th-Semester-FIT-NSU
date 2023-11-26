package search

import (
	"places/search/graphhopper"
	"places/search/opentripmap"
	"places/search/openweather"
	"sync"
)

type Placer struct {
	graphHopper graphhopper.GraphHopper
	openWeather openweather.OpenWeather
	openTripMap opentripmap.OpenTripMap
}

func NewPlacer(graphHopper *graphhopper.GraphHopper, openWeather *openweather.OpenWeather, openTripMap *opentripmap.OpenTripMap) *Placer {
	return &Placer{
		graphHopper: *graphHopper,
		openWeather: *openWeather,
		openTripMap: *openTripMap,
	}
}

func (s *Placer) FindByName(name string, limit int) (*graphhopper.GetGeocodingResponse, error) {
	return s.graphHopper.GetGeocoding(name, limit)
}

func (s *Placer) FindByNameAsBytes(name string, limit int32) ([]byte, error) {
	return s.graphHopper.GetGeocodingAsBytes(name, limit)
}

func (s *Placer) GetLocationInfo(lat, lon float64, radius int) (*LocationInfoResult, error) {
	var weather *openweather.GetWeatherResponse
	var weatherErr error
	weatherFinished := make(chan bool)
	go func() {
		weather, weatherErr = s.openWeather.GetWeather(lat, lon)
		weatherFinished <- true
	}()

	var placesInRadius []*opentripmap.NearbyPlace
	var radiusErr error
	placesInRadius, radiusErr = s.openTripMap.GetPlacesInRadius(lat, lon, radius)
	if radiusErr != nil {
		<-weatherFinished
		return nil, radiusErr
	}

	radiusPlacesWithInfo := make([]RadiusInfo, 0, len(placesInRadius))
	mutex := sync.Mutex{}
	var wg sync.WaitGroup
	wg.Add(len(placesInRadius))
	for _, v := range placesInRadius {
		v := v
		go func() {
			defer wg.Done()
			description, err := s.openTripMap.GetPlaceDescription(v.Xid)
			if err != nil {
				return
			}
			mutex.Lock()
			radiusPlacesWithInfo = append(radiusPlacesWithInfo, RadiusInfo{Info: description, Place: v})
			mutex.Unlock()
		}()
	}
	<-weatherFinished
	wg.Wait()
	if weatherErr != nil {
		return nil, weatherErr
	}

	return &LocationInfoResult{Weather: weather, Radius: radiusPlacesWithInfo}, nil
}

type LocationInfoResult struct {
	Weather *openweather.GetWeatherResponse `json:"weather"`
	Radius  []RadiusInfo                    `json:"radius"`
}

type RadiusInfo struct {
	Place *opentripmap.NearbyPlace                 `json:"place"`
	Info  *opentripmap.GetPlaceDescriptionResponse `json:"info"`
}
