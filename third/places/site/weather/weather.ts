import {ServerResponse} from "../common/backend_web_socket";

export {

}

import('../common/backend_web_socket.js').then(({ ws }) => {
    ws.addEventListener('message', (event) => {
        const response = JSON.parse(event.data) as ServerResponse
        if (response.code === 1) {
            serveResponse1(response)
        } else {
            console.log("weather: ignoring...")
        }
    })

    function serveResponse1(response: ServerResponse) {
        console.log(response)
        const weatherWrapper = response.info as WeatherResponseWrapper
        const weather = weatherWrapper.weather
        console.log(weather)
        weatherWeatherMainLabel.innerText = "Weather: " + weather.weather[0].main
        weatherWeatherDescriptionLabel.innerText = "Description: " + weather.weather[0].description
        weatherMainTempLabel.innerText = "Current temp: " + weather.main.temp.toString() + "°C"
        weatherMainFeelsLikeLabel.innerText = "Feels like: " + weather.main.feels_like.toString() + "°C"
        weatherMainPressureLabel.innerText = "Atmosphere pressure: " + weather.main.pressure.toString() + "hPa"
        weatherMainHumidityLabel.innerText = "Humidity: " + weather.main.humidity.toString() + "%"
        weatherMainTempMinLabel.innerText = "Min temp: " + weather.main.temp_min.toString() + "°C"
        weatherMainTempMaxLabel.innerText = "Max temp: " + weather.main.temp_max.toString() + "°C"
        weatherMainSeaLevelLabel.innerText = "Atmosphere pressure on sea level: " + weather.main.sea_level.toString() + "hPa"
        weatherMainGrndLevelLabel.innerText = "Atmosphere pressure on ground level: " + weather.main.grnd_level.toString() + "hPa"
        weatherVisibilityLabel.innerText = "Visibility meters: " + weather.visibility.toString() + "m"
        weatherWindSpeedLabel.innerText = "Wind speed: " + weather.wind.speed.toString() + "m/s"
        weatherWindDegLabel.innerText = "Wind direction: " + weather.wind.deg.toString() + "°"
        weatherWindGustLabel.innerText = "Wind gust: " + weather.wind.gust.toString() + "m/s"
        weatherCloudsLabel.innerText = "Clouds: " + weather.clouds.all.toString() + "%"
        if (weather.rain["1h"]) {
            weatherRain1hLabel.innerText = "Rain per 1 hour: " + weather.rain["1h"].toString() + "mm"
        } else {
            weatherRain1hLabel.innerText = "Rain per 1 hour: " + "no info"
        }
        if (weather.rain["3h"]) {
            weatherRain3hLabel.innerText = "Rain per 3 hours: " + weather.rain["3h"].toString() + "mm"
        } else {
            weatherRain3hLabel.innerText = "Rain per 3 hours: " + "no info"
        }
        if (weather.snow["1h"]) {
            weatherSnow1hLabel.innerText = "Snow per 1 hour: " + weather.snow["1h"].toString() + "mm"
        } else {
            weatherSnow1hLabel.innerText = "Snow per 1 hour: " + "no info"
        }
        if (weather.snow["3h"]) {
            weatherSnow3hLabel.innerText = "Snow per 3 hours: " + weather.snow["3h"].toString() + "mm"
        } else {
            weatherSnow3hLabel.innerText = "Snow per 3 hours: " + "no info"
        }
        weatherTimeDtLabel.innerText = "Current time: " + new Date(weather.dt * 1000).toUTCString()
        const sunrise = new Date(weather.sys.sunrise * 1000)
        weatherTimeSunriseLabel.innerText = "Sunrise at: " + sunrise.getUTCHours() + ":" + sunrise.getUTCMinutes()
        const sunset = new Date(weather.sys.sunset * 1000)
        weatherTimeSunsetLabel.innerText = "Sunset at: " + sunset.getUTCHours() + ":" + sunset.getUTCMinutes()
        const timezone = weather.timezone / 3600
        weatherTimeTimezoneLabel.innerText = "Timezone: UTC" + (timezone >= 0 ? "+" : "-") + timezone.toString()
        weatherLocationCityLabel.innerText = weather.name
        weatherLocationCountryLabel.innerText = weather.sys.country

        const weatherImage = document.createElement('img')
        weatherImage.src = imagePrefix + weather.weather[0].icon + "@2x.png"
        weatherImage.sizes="100x100"
        weatherImageDiv.innerHTML = ''
        weatherImageDiv.appendChild(weatherImage)
    }
})

const imagePrefix = "https://openweathermap.org/img/wn/"

const weatherWeatherMainLabel        = document.getElementById('weather-weather-main-label')
const weatherWeatherDescriptionLabel = document.getElementById('weather-weather-description-label')
const weatherMainTempLabel           = document.getElementById('weather-main-temp-label')
const weatherMainFeelsLikeLabel      = document.getElementById('weather-main-feels-like-label')
const weatherMainPressureLabel       = document.getElementById('weather-main-pressure-label')
const weatherMainHumidityLabel       = document.getElementById('weather-main-humidity-label')
const weatherMainTempMinLabel        = document.getElementById('weather-main-temp-min-label')
const weatherMainTempMaxLabel        = document.getElementById('weather-main-temp-max-label')
const weatherMainSeaLevelLabel       = document.getElementById('weather-main-sea-level-label')
const weatherMainGrndLevelLabel      = document.getElementById('weather-main-grnd-level-label')
const weatherVisibilityLabel         = document.getElementById('weather-visibility-label')
const weatherWindSpeedLabel          = document.getElementById('weather-wind-speed-label')
const weatherWindDegLabel            = document.getElementById('weather-wind-deg-label')
const weatherWindGustLabel           = document.getElementById('weather-wind-gust-label')
const weatherCloudsLabel             = document.getElementById('weather-clouds-label')
const weatherRain1hLabel             = document.getElementById('weather-rain-1h-label')
const weatherRain3hLabel             = document.getElementById('weather-rain-3h-label')
const weatherSnow1hLabel             = document.getElementById('weather-snow-1h-label')
const weatherSnow3hLabel             = document.getElementById('weather-snow-3h-label')
const weatherTimeDtLabel             = document.getElementById('weather-time-dt-label')
const weatherTimeSunriseLabel        = document.getElementById('weather-time-sunrise-label')
const weatherTimeSunsetLabel         = document.getElementById('weather-time-sunset-label')
const weatherTimeTimezoneLabel       = document.getElementById('weather-time-timezone-label')
const weatherLocationCountryLabel    = document.getElementById('weather-location-country-label')
const weatherLocationCityLabel       = document.getElementById('weather-location-city-label')
const weatherImageDiv                = document.getElementById('weather-image-div')

type WeatherResponseWrapper = {
    weather: WeatherResponse,
    radius: any
}

type WeatherResponse = {
    "coord": {
        "lon": number,
        "lat": number
    },
    "weather": [
        {
            "id": number,
            "main": string,
            "description": string,
            "icon": string
        }
    ],
    "base": string,
    "main": {
        "temp": number,
        "feels_like": number,
        "temp_min": number,
        "temp_max": number,
        "pressure": number,
        "humidity": number,
        "sea_level": number,
        "grnd_level": number
    },
    "visibility": number,
    "wind": {
        "speed": number,
        "deg": number,
        "gust": number
    },
    "rain": {
        "1h"?: number
        "3h"?: number
    },
    "snow": {
        "1h"?: number,
        "3h"?: number,
    }
    "clouds": {
        "all": number
    },
    "dt": number,
    "sys": {
        "type": number,
        "id": number,
        "country": string,
        "sunrise": number,
        "sunset": number
    },
    "timezone": number,
    "id": number,
    "name": string,
    "cod": number
}
