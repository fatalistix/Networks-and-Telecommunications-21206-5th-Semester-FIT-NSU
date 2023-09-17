package controller

import (
	"fmt"

	"fyne.io/fyne/v2"

	"networks/first/view"
)

type Controller struct {
	startView       *fyne.Container
	listViewHandler *view.ListWindowViewHandler
	settingsView    *fyne.Container
	window          fyne.Window
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

	s.startView = view.NewStartWindowView(func(ip string) {
		s.window.SetContent(s.listViewHandler.Container())
		fmt.Println(ip)
	}, func(ip string) {
		fmt.Println(ip)
	}, func() {
		s.window.SetContent(s.settingsView)
	})

	s.listViewHandler = view.NewListWindowView("MOCK", "MOCK", 69, func() {
		s.window.SetContent(s.startView)
	})

	s.window.SetContent(s.startView)
	// s.window.SetContent(view.NewListWindowView())
}
