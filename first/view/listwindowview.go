package view

import (
	"strconv"
	"strings"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

type ListWindowViewHandler struct {
	cont       *fyne.Container
	users      []string
	ipToUserId map[string]int
}

func NewListWindowView(
	myIp, myName string,
	port int,
	onBackButtonPressed func(),
) *ListWindowViewHandler {
	handler := ListWindowViewHandler{}
	handler.users = make([]string, 0)
	handler.ipToUserId = make(map[string]int)

	topLabel := widget.NewLabel(myName + "#" + myIp + ":" + strconv.Itoa(port))
	list := widget.NewList(func() int {
		return len(handler.users)
	}, func() fyne.CanvasObject {
		return widget.NewLabel(strings.Repeat("1", 50))
	}, func(lii widget.ListItemID, co fyne.CanvasObject) {
		co.(*widget.Label).SetText(handler.users[lii])
	})
	backButton := widget.NewButton("Back", onBackButtonPressed)
	backButton.Importance = widget.HighImportance

	handler.cont = container.NewBorder(topLabel, backButton, nil, nil, list)
	return &handler
}

func (s *ListWindowViewHandler) Container() *fyne.Container {
	return s.cont
}
