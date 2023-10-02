package view

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/cmd/fyne_settings/settings"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/widget"
)

func NewSettingsView(w fyne.Window, onBackButtonPressed func()) *fyne.Container {
	return container.NewBorder(
		settings.NewSettings().LoadAppearanceScreen(w),
		widget.NewButton("Back", onBackButtonPressed),
		nil,
		nil,
	)
}
