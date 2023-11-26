const domainName = "nsu-places.ddns.net"
const webSocketListenURI = "/ws"
export const ws = new WebSocket("wss://" + domainName + webSocketListenURI)

// const domainName = "localhost:8069"
// const webSocketListenURI = "/ws"
// export const ws = new WebSocket("ws://" + domainName + webSocketListenURI)

export type ServerResponse = {
    code: number,
    info: any
}