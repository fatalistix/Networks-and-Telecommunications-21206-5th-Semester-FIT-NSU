package controller

import (
	"fmt"
	"net"
	"strconv"
	"time"

	"fyne.io/fyne/v2"

	"networks/first/model"
	"networks/first/model/ip4"
	"networks/first/view"
)

const numOfErrors int = 5

type addrWithId struct {
	Addr   string
	Packet model.B6FPacket
}

type Controller struct {
	startView        *fyne.Container
	listViewHandler  *view.ListWindowViewHandler
	settingsView     *fyne.Container
	errorViewHandler *view.ErrorViewHandler
	window           fyne.Window
	ip4Server        *ip4.IP4B6FServer
	bufferSize       int
	buffer           []byte
	updaterClose     chan bool
	updaterNewPacket chan addrWithId
}

func NewController(window fyne.Window) *Controller {
	controller := Controller{}
	controller.window = window
	controller.bufferSize = 1024
	controller.buffer = make([]byte, controller.bufferSize)
	controller.updaterClose = make(chan bool)
	controller.updaterNewPacket = make(chan addrWithId)
	return &controller
}

func (s *Controller) handleError(err error) {
	s.errorViewHandler.UpdateErrorDescription(err)
	s.window.SetContent(s.errorViewHandler.Container())
}

func (s *Controller) createUpdater() {
	go func() {
		ticker := time.NewTicker(1 * time.Second)
		lastPacketTime := make(map[string]time.Time)
		defer ticker.Stop()
		for {
			select {
			case <-s.updaterClose:
				{
					s.listViewHandler.ClearList()
					return
				}
			case data := <-s.updaterNewPacket:
				{
					switch data.Packet.MessageType() {
					case model.B6F_MT_Report:
						{
							lastPacketTime[data.Addr] = time.Now()
							s.listViewHandler.Add(data.Addr, data.Packet.Id())
						}
					case model.B6F_MT_Leave:
						{
							delete(lastPacketTime, data.Addr)
							s.listViewHandler.Remove(data.Addr)
						}
					}
				}
			case <-ticker.C:
				{
					currentTime := time.Now()
					for k, v := range lastPacketTime {
						if currentTime.Sub(v) >= model.B6FDeletingTimeourMs*time.Millisecond {
							delete(lastPacketTime, k)
							s.listViewHandler.Remove(k)
						}
					}
				}
			}
		}
	}()
}

func (s *Controller) ip4Pressed(ip, iden string) {
	s.window.SetContent(s.listViewHandler.Container())
	s.listViewHandler.UpdateTitle(ip+":"+strconv.Itoa(model.B6FPort), iden)
	var err error
	s.ip4Server, err = ip4.NewIP4B6FServer(ip, model.B6FPort, iden)
	if err != nil {
		s.handleError(err)
	}

	err = s.ip4Server.Start(
		s.buffer,
		model.B6FSendingTimeoutMs,
		numOfErrors,
		func(addr net.Addr, packet model.B6FPacket) {
			s.updaterNewPacket <- addrWithId{Addr: addr.String(), Packet: packet}
		},
	)

	if err != nil {
		s.handleError(err)
	}
}

func (s *Controller) ip16Pressed(ip, iden string) {
	fmt.Println("MOCK: ", ip, " # ", iden)
}

func (s *Controller) Init() {
	s.settingsView = view.NewSettingsView(s.window, func() {
		s.window.SetContent(s.startView)
	})

	s.startView = view.NewStartWindowView(func(ip, iden string) {
		s.createUpdater()
		s.ip4Pressed(ip, iden)
	}, func(ip, iden string) {
		// s.createUpdater()
		s.ip16Pressed(ip, iden)
	}, func() {
		s.window.SetContent(s.settingsView)
	})

	s.listViewHandler = view.NewListWindowView("MOCK", "MOCK", model.B6FPort, func() {
		s.window.SetContent(s.startView)
		s.ip4Server.Close()
		s.updaterClose <- true
		s.listViewHandler.ClearList()
	})

	s.errorViewHandler = view.NewErrorViewHandler(func() {
		s.window.SetContent(s.startView)
	})

	s.window.SetContent(s.startView)
}
