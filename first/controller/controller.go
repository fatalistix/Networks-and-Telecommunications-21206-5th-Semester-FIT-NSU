package controller

import "fyne.io/fyne/v2"

type Controller struct {
	window fyne.Window
}

func NewController(window fyne.Window) Controller {
	controller := Controller{}
	controller.window = window
	return controller
}
