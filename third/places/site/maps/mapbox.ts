export {

}

// @ts-ignore
mapboxgl.accessToken = 'pk.eyJ1IjoiamFyZWRmaWdvIiwiYSI6ImNscGNocmc3dTB2NTEya3ByZmdmYzliYm4ifQ.72vXlJ0RVOwjkPnVtWvoAw'
// @ts-ignore
const map = new mapboxgl.Map({
    container: 'map',
    style: 'mapbox://sytles/mapbox/streets-v12',
    center: [12.550343, 55.665957],
    zoom: 8
})

type Point = {
    lat: number,
    lng: number
}

// @ts-ignore
const markers = new Set<mapboxgl.Marker>()

export function setCenter(lng: number, lat: number) {
    map.setCenter([lng, lat])
}

export function addMarker(lng: number, lat: number, color: string) {
    // @ts-ignore
    markers.add(new mapboxgl.Marker({ color: color}).setLngLat([lng, lat]).addTo(map))
}

export function removeAllMarkers() {
    markers.forEach((marker) => {
        marker.remove()
    })
    markers.clear()
}

// @ts-ignore
// const marker1 = new mapboxgl.Marker({ color: 'red'}).setLngLat([83.092100, 54.847330]).addTo(map)
// marker1.remove()
// @ts-ignore
// const marker2 = new mapboxgl.Marker({ color: 'black'})
//     .setLngLat([12.554729, 55.70651]).addTo(map)