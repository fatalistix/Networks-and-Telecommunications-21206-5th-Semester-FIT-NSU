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
	ipToUserId map[string]string
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
	handler.ipToUserId = make(map[string]string)

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

func (s *ListWindowViewHandler) Add(addr, id string) {
	s.ipToUserId[addr] = id
	s.refreshList()
}

func (s *ListWindowViewHandler) Remove(addr string) {
	_, ok := s.ipToUserId[addr]
	if ok {
		delete(s.ipToUserId, addr)
		s.refreshList()
	}
}

func (s *ListWindowViewHandler) UpdateTitle(addr, id string) {
	s.titleLabel.SetText(addr + "#" + id)
}

func (s *ListWindowViewHandler) ClearList() {
	s.users = make([]string, 0)
	s.ipToUserId = make(map[string]string)
	s.list.Refresh()
}

func (s *ListWindowViewHandler) refreshList() {
	s.users = make([]string, 0, len(s.ipToUserId))
	for k, v := range s.ipToUserId {
		s.users = append(s.users, k+"#"+v)
	}
	s.list.Refresh()
}
