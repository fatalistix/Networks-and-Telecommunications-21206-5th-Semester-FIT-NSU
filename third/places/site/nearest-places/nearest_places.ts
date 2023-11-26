import {ServerResponse} from "../common/backend_web_socket";

export {

}

import('../common/backend_web_socket.js').then(({ ws }) => {
    import("../maps/mapbox.js").then(({ addMarker }) => {
        ws.addEventListener('message', (event) => {
            const response = JSON.parse(event.data) as ServerResponse
            if (response.code === 1) {
                serveCode1(response)
            } else {
                console.log('nearest places: ignoring...')
            }
        })

        function serveCode1(response: ServerResponse) {
            console.log(response.info)
            const nearestPlacesWrapper = response.info as NearestPlacesWrapper
            const nearestPlaces = nearestPlacesWrapper.radius
            nearestPlacesUl.innerHTML = ''
            for (let i = 0; i < nearestPlaces.length; i++) {
                const currentPlace = nearestPlaces[i]
                const li = document.createElement('li')
                li.append(
                    "name: " + currentPlace.place.name + " " +
                    "kind: " + currentPlace.place.kind
                )
                li.addEventListener('click', (event) => {
                    placeDescriptionNameLabel.innerText = "Name: " + currentPlace.info.name
                    placeDescriptionDescriptionLabel.innerHTML = "Description: " + currentPlace.info.info.descr
                    placeDescriptionKindsLabel.innerText = "Kinds: " + currentPlace.info.kinds

                    placeImageDiv.innerHTML = ""
                    if (currentPlace.info.image != '') {
                        const img = document.createElement('img')
                        img.src = currentPlace.info.image
                        img.sizes = currentPlace.info.info.img_height.toString() + "x" + currentPlace.info.info.img_width.toString()
                        console.log(currentPlace.info.image)
                        placeImageDiv.append(img)
                    }
                })
                addMarker(currentPlace.place.point.lon, currentPlace.place.point.lat, 'red')
                nearestPlacesUl.appendChild(li)
            }
        }
    })
})

const placeDescriptionNameLabel         = document.getElementById('place-description-name-label')
const placeDescriptionDescriptionLabel  = document.getElementById('place-description-description-label')
const placeDescriptionKindsLabel        = document.getElementById('place-description-kinds-label')
const placeImageDiv                     = document.getElementById('place-image-div')

const nearestPlacesUl = document.getElementById('nearest-places-ul')
nearestPlacesUl.onmousedown = function() { return false }
nearestPlacesUl.addEventListener('click', selectElement )

type NearestPlacesWrapper = {
    weather: any,
    radius: [
        {
            place: {
                name: string,
                osm: string,
                xid: string,
                wikidata: string,
                kind: string,
                point: {
                    lon: number,
                    lat: number
                }
            },
            info: {
                "kinds": string
                "sources": {
                    "geometry": string
                    "attributes": [string]
                },
                "bbox": {
                    "lat_max": number,
                    "lat_min": number,
                    "lon_max": number,
                    "lon_min": number
                },
                "point": {
                    "lon": number,
                    "lat": number
                },
                "osm": string,
                "otm": string,
                "xid": string,
                "name": string,
                "wikipedia": string,
                "image": string,
                "wikidata": string,
                "rate": string,
                "info": {
                    "descr": string,
                    "image": string,
                    "img_width": number,
                    "src": string,
                    "src_id": number,
                    "img_height": number
                }
            }
        }
    ]
}

function selectElement(e: any) {
    if ('LI' !== e.target.tagName) return;
    if (e.ctrlKey || e.metaKey) {
        e.target.classList.toggle('selected');
    } else {
        let selected = nearestPlacesUl.querySelectorAll('.selected');
        selected.forEach(li => li.classList.remove('selected'));
        e.target.classList.add('selected');
    }
}