import('../common/backend_web_socket.js').then(({ ws }) => {
    import("../maps/mapbox.js").then(({ addMarker }) => {
        ws.addEventListener('message', (event) => {
            const response = JSON.parse(event.data);
            if (response.code === 1) {
                serveCode1(response);
            }
            else {
                console.log('nearest places: ignoring...');
            }
        });
        function serveCode1(response) {
            console.log(response.info);
            const nearestPlacesWrapper = response.info;
            const nearestPlaces = nearestPlacesWrapper.radius;
            nearestPlacesUl.innerHTML = '';
            for (let i = 0; i < nearestPlaces.length; i++) {
                const currentPlace = nearestPlaces[i];
                const li = document.createElement('li');
                li.append("name: " + currentPlace.place.name + " " +
                    "kind: " + currentPlace.place.kind);
                li.addEventListener('click', (event) => {
                    placeDescriptionNameLabel.innerText = "Name: " + currentPlace.info.name;
                    placeDescriptionDescriptionLabel.innerHTML = "Description: " + currentPlace.info.info.descr;
                    placeDescriptionKindsLabel.innerText = "Kinds: " + currentPlace.info.kinds;
                    placeImageDiv.innerHTML = "";
                    if (currentPlace.info.image != '') {
                        const img = document.createElement('img');
                        img.src = currentPlace.info.image;
                        img.sizes = currentPlace.info.info.img_height.toString() + "x" + currentPlace.info.info.img_width.toString();
                        console.log(currentPlace.info.image);
                        placeImageDiv.append(img);
                    }
                });
                addMarker(currentPlace.place.point.lon, currentPlace.place.point.lat, 'red');
                nearestPlacesUl.appendChild(li);
            }
        }
    });
});
const placeDescriptionNameLabel = document.getElementById('place-description-name-label');
const placeDescriptionDescriptionLabel = document.getElementById('place-description-description-label');
const placeDescriptionKindsLabel = document.getElementById('place-description-kinds-label');
const placeImageDiv = document.getElementById('place-image-div');
const nearestPlacesUl = document.getElementById('nearest-places-ul');
nearestPlacesUl.onmousedown = function () { return false; };
nearestPlacesUl.addEventListener('click', selectElement);
function selectElement(e) {
    if ('LI' !== e.target.tagName)
        return;
    if (e.ctrlKey || e.metaKey) {
        e.target.classList.toggle('selected');
    }
    else {
        let selected = nearestPlacesUl.querySelectorAll('.selected');
        selected.forEach(li => li.classList.remove('selected'));
        e.target.classList.add('selected');
    }
}
export {};
//# sourceMappingURL=nearest_places.js.map