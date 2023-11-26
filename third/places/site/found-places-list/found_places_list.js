import('../common/backend_web_socket.js').then(({ ws }) => {
    import('../maps/mapbox.js').then(({ setCenter, addMarker, removeAllMarkers }) => {
        ws.addEventListener('message', (event) => {
            console.log("HERERER");
            const response = JSON.parse(event.data);
            if (response.code === 0) {
                serveCode0(response);
            }
            else {
                console.log("in found_places_list: ignoring...");
            }
        });
        function serveCode0(response) {
            const graphHopperPlaces = response.info;
            console.log(graphHopperPlaces.hits);
            console.log(graphHopperPlaces);
            const hits = graphHopperPlaces.hits;
            console.log("CHILDREN: " + interestingPlacesUl.children.length);
            // for (let i = 0; i < interestingPlacesUl.children.length; i++) {
            //     interestingPlacesUl.children[i].remove()
            // }
            interestingPlacesUl.innerHTML = '';
            console.log("CHILDREN: " + interestingPlacesUl.children.length);
            for (let i = 0; i < hits.length; i++) {
                const currentPlace = hits[i];
                const li = document.createElement('li');
                li.append("name: " + currentPlace.name + "\n" +
                    "country: " + currentPlace.country + "\n" +
                    "city: " + currentPlace.city + "\n" +
                    "postcode: " + currentPlace.postcode);
                li.addEventListener('click', (event) => {
                    const point = {
                        lat: currentPlace.point.lat,
                        lng: currentPlace.point.lng,
                        radius: 1000
                    };
                    const message = {
                        id: 1,
                        info: JSON.stringify(point)
                    };
                    ws.send(JSON.stringify(message));
                    setCenter(currentPlace.point.lng, currentPlace.point.lat);
                    removeAllMarkers();
                    addMarker(currentPlace.point.lng, currentPlace.point.lat, 'black');
                });
                interestingPlacesUl.appendChild(li);
            }
        }
    });
});
const interestingPlacesUl = document.getElementById("interesting-places-ul");
interestingPlacesUl.onmousedown = function () { return false; };
interestingPlacesUl.addEventListener('click', selectElement);
function selectElement(e) {
    if ('LI' !== e.target.tagName)
        return;
    if (e.ctrlKey || e.metaKey) {
        e.target.classList.toggle('selected');
    }
    else {
        let selected = interestingPlacesUl.querySelectorAll('.selected');
        selected.forEach(li => li.classList.remove('selected'));
        e.target.classList.add('selected');
    }
}
export {};
//# sourceMappingURL=found_places_list.js.map