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
	list       *widget.List
	titleLabel *widget.Label
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
	topLabel.Importance = widget.HighImportance
	handler.titleLabel = topLabel

	list := widget.NewList(func() int {
		return len(handler.users)
	}, func() fyne.CanvasObject {
		return widget.NewLabel(strings.Repeat("1", 50))
	}, func(lii widget.ListItemID, co fyne.CanvasObject) {
		co.(*widget.Label).SetText(handler.users[lii])
	})
	handler.list = list
	backButton := widget.NewButton("Back", onBackButtonPressed)
	backButton.Importance = widget.HighImportance

	handler.cont = container.NewBorder(container.NewCenter(topLabel), backButton, nil, nil, list)
	return &handler
}

func (s *ListWindowViewHandler) Container() *fyne.Container {
	return s.cont
}

func (s *ListWindowViewHandler) Add(ip string, port int, id string) {
	ipWithPort := ip + ":" + strconv.Itoa(port)
	_, ok := s.ipToUserId[ipWithPort]
	if ok {
		return
	}
	s.ipToUserId[ipWithPort] = len(s.users)
	s.users = append(s.users, ipWithPort+"#"+id)
	s.list.Refresh()
}

func (s *ListWindowViewHandler) Remove(ip string, port int) {
	ipWithPort := ip + ":" + strconv.Itoa(port)
	index, ok := s.ipToUserId[ipWithPort]
	if ok {
		s.users = append(s.users[:index], s.users[index+1:]...)
		delete(s.ipToUserId, ipWithPort)
		s.list.Refresh()
	}
}

func (s *ListWindowViewHandler) UpdateTitle(ip string, port int, id string) {
	s.titleLabel.SetText(id + "#" + ip + "#" + strconv.Itoa(port))
}
