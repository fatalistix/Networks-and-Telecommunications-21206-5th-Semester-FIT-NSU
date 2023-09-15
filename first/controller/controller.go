package controller

import (
	"fmt"

	"fyne.io/fyne/v2"

	"networks/first/view"
)

type Controller struct {
	startWindow *fyne.Container
	listWindow  *fyne.Container
	window      fyne.Window
}

func NewController(window fyne.Window) *Controller {
	controller := Controller{}
	controller.window = window
	return &controller
}

func (s *Controller) Init() {
	s.startWindow = view.NewStartWindowView(func(ip string) {
		fmt.Println(ip)
	}, func(ip string) {
		fmt.Println(ip)
	})
	s.window.SetContent(s.startWindow)
}
