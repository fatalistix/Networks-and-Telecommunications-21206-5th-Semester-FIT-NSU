package controller

import (
	"fmt"
	"net"

	"fyne.io/fyne/v2"

	"networks/first/model"
	"networks/first/model/ip4"
	"networks/first/view"
)

const numOfErrors int = 5

type Controller struct {
	startView        *fyne.Container
	listViewHandler  *view.ListWindowViewHandler
	settingsView     *fyne.Container
	errorViewHandler *view.ErrorViewHandler
	window           fyne.Window
	ip4Server        *ip4.IP4B6FServer
	bufferSize       int
	buffer           []byte
}

func NewController(window fyne.Window) *Controller {
	controller := Controller{}
	controller.window = window
	controller.bufferSize = 1024
	controller.buffer = make([]byte, controller.bufferSize)
	return &controller
}

func (s *Controller) handleError(err error) {
	s.errorViewHandler.UpdateErrorDescription(err)
	s.window.SetContent(s.errorViewHandler.Container())
}

func (s *Controller) ip4Pressed(ip, iden string) {
	s.window.SetContent(s.listViewHandler.Container())
	s.listViewHandler.UpdateTitle(ip, model.B6FPort, iden)
	var err error
	s.ip4Server, err = ip4.NewIP4B6FServer(ip, model.B6FPort, iden)
	if err != nil {
		s.handleError(err)
	}

	err = s.ip4Server.Start(
		s.buffer,
		model.B6FSendingTimeoutMs,
		numOfErrors,
		func(addr *net.UDPAddr, packet model.B6FPacket) {
			switch packet.MessageType() {
			case model.B6F_MT_Report:
				{
					s.listViewHandler.Add(addr.IP.String(), addr.Port, packet.Id())
				}
			case model.B6F_MT_Leave:
				{
					s.listViewHandler.Remove(addr.IP.String(), addr.Port)
				}
			}
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
		s.ip4Pressed(ip, iden)
	}, func(ip, iden string) {
		s.ip16Pressed(ip, iden)
	}, func() {
		s.window.SetContent(s.settingsView)
	})

	s.listViewHandler = view.NewListWindowView("MOCK", "MOCK", model.B6FPort, func() {
		s.window.SetContent(s.startView)
		s.ip4Server.Close()
	})

	s.errorViewHandler = view.NewErrorViewHandler(func() {
		s.window.SetContent(s.startView)
	})

	s.window.SetContent(s.startView)
}
