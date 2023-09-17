package view

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

type ErrorViewHandler struct {
	cont            *fyne.Container
	errorDescrLabel *widget.Label
}

func NewErrorViewHandler(onBackButtonPressed func()) *ErrorViewHandler {
	handler := ErrorViewHandler{}
	mainLabel := widget.NewLabel("ERROR!")
	descriptionLabel := widget.NewLabel("")
	handler.errorDescrLabel = descriptionLabel
	backButton := widget.NewButton("Back", onBackButtonPressed)
	backButton.Importance = widget.HighImportance
	handler.cont = container.NewBorder(mainLabel, backButton, nil, nil, descriptionLabel)
	return &handler
}

func (s *ErrorViewHandler) UpdateErrorDescription(err error) {
	s.errorDescrLabel.SetText(err.Error())
}

func (s *ErrorViewHandler) Container() *fyne.Container {
	return s.cont
}
