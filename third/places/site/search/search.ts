export {

}

import('../common/backend_web_socket.js').then(({ ws }) => {
    console.log("SEARCH")
    searchButton.onclick = (event) => {
        const searchPattern = searchField.value
        const placeRequest = {
            place: searchPattern,
            limit: 1000
        }
        const message = {
            id: 0,
            info: JSON.stringify(placeRequest)
        }
        console.log(message)
        ws.send(JSON.stringify(message))
        console.log("CLICKED")
    }
})

const searchField = document.getElementById('place-search') as HTMLInputElement
const searchButton = document.getElementById('search-button')