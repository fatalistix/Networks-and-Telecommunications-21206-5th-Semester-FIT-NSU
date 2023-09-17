package controller

import (
	"fmt"
	"net"

	"fyne.io/fyne/v2"

	"networks/first/model"
	"networks/first/model/ip4"
	"networks/first/view"
)

const port int = 6969

type Controller struct {
	startView        *fyne.Container
	listViewHandler  *view.ListWindowViewHandler
	settingsView     *fyne.Container
	errorViewHandler *view.ErrorViewHandler
	window           fyne.Window

	ip4Server *ip4.IP4B6FServer
}

func NewController(window fyne.Window) *Controller {
	controller := Controller{}
	controller.window = window
	return &controller
}

func (s *Controller) Init() {
	s.settingsView = view.NewSettingsView(s.window, func() {
		s.window.SetContent(s.startView)
	})

	s.startView = view.NewStartWindowView(func(ip, iden string) {
		s.window.SetContent(s.listViewHandler.Container())
		s.listViewHandler.UpdateTitle(ip, port, iden)
		var err error
		s.ip4Server, err = ip4.NewIP4B6FServer(ip, port, iden)
		if err != nil {
			s.window.SetContent(s.errorViewHandler.Container())
		}

		buffer := make([]byte, 1024)
		err = s.ip4Server.Start(buffer, 500, 500, func(addr *net.UDPAddr, packet model.B6FPacket) {
			switch packet.MessageType() {
			case model.Report:
				{
					s.listViewHandler.Add(addr.IP.String(), addr.Port, packet.Id())
				}
			case model.Leave:
				{
					s.listViewHandler.Remove(addr.IP.String(), addr.Port)
				}
			}
		})
		if err != nil {
			s.window.SetContent(s.errorViewHandler.Container())
		}
	}, func(ip, id string) {
		fmt.Println(ip)
	}, func() {
		s.window.SetContent(s.settingsView)
	})

	s.listViewHandler = view.NewListWindowView("MOCK", "MOCK", port, func() {
		s.window.SetContent(s.startView)
		s.ip4Server.Close()
	})

	s.errorViewHandler = view.NewErrorViewHandler(func() {
		s.window.SetContent(s.startView)
	})

	s.window.SetContent(s.startView)
}
