package view

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/data/binding"
	"fyne.io/fyne/v2/widget"
)

// func newStartWindowViewThirdRowColumnGrid() *fyne.Container {
// firstEntry := widget.NewEntryWithData(binding.BindString())
// return container.NewGridWithColumns(15)
// }

func newStartWindowViewFirstRowColumnGrid() *fyne.Container {
	firstStr := "224"
	firstEntry := widget.NewEntry()
	firstEntry.Bind(binding.BindString(&firstStr))
	firstEntry.TextStyle = fyne.TextStyle{}
	return container.NewGridWithColumns(7, firstEntry)
}

func NewStartWindowView() *fyne.Container {

	watch4Button := widget.NewButton("Watch IPv4", func() {})
	watch6Button := widget.NewButton("Watch IPv6", func() {})

	firstRowContainer := newStartWindowViewFirstRowColumnGrid()
	secondRowContainer := container.NewStack()
	thirdRowContainer := container.NewGridWithColumns(7)
	forthRowContainer := container.NewGridWithColumns(2, watch4Button, watch6Button)
	mainGrid := container.NewGridWithRows(4, firstRowContainer, secondRowContainer, thirdRowContainer, forthRowContainer)
	return mainGrid
}
