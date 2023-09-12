package view

import (
	"errors"
	"image/color"

	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/canvas"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/data/binding"
	"fyne.io/fyne/v2/widget"
)

// func newStartWindowViewThirdRowColumnGrid() *fyne.Container {
// firstEntry := widget.NewEntryWithData(binding.BindString())
// return container.NewGridWithColumns(15)
// }

func newStartWindowViewFirstRowColumnGrid(validator func(string) error, onValidationSuccess, onValidationError func()) *fyne.Container {
	firstText := canvas.NewText("224", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	firstText.TextSize = 50.
	firstText.Alignment = fyne.TextAlignCenter

	secondText := canvas.NewText(":", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	secondText.TextSize = 50.
	secondText.Alignment = fyne.TextAlignCenter

	thirdText := canvas.NewText("0", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	thirdText.TextSize = 50.
	thirdText.Alignment = fyne.TextAlignCenter

	forthText := canvas.NewText(":", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	forthText.TextSize = 50.
	forthText.Alignment = fyne.TextAlignCenter

	fifthText := canvas.NewText("0", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	fifthText.TextSize = 50.
	fifthText.Alignment = fyne.TextAlignCenter

	sixthText := canvas.NewText(":", color.NRGBA{R: 0, B: 150, G: 150, A: 255})
	sixthText.TextSize = 50.
	sixthText.Alignment = fyne.TextAlignCenter

	s := ""
	seventhEntry := widget.NewEntryWithData(binding.BindString(&s))
	seventhEntry.Validator = fyne.StringValidator(validator)
	seventhEntry.OnChanged = func(s string) {
		err := seventhEntry.Validate()
		if err != nil {
			onValidationError()
		} else {
			onValidationSuccess()
		}
	}

	return container.NewGridWithColumns(7, firstText, secondText, thirdText, forthText, fifthText, sixthText, seventhEntry)
}

func NewStartWindowView() *fyne.Container {

	watch4Button := widget.NewButton("Watch IPv4", func() {})
	watch4Button.Disable()
	watch6Button := widget.NewButton("Watch IPv6", func() {})

	firstRowContainer := newStartWindowViewFirstRowColumnGrid(func(s string) error {
		if s == "123" {
			return nil
		}
		return errors.New("")
	}, func() {
		watch4Button.Enable()
	}, func() {
		watch4Button.Disable()
	})
	secondRowContainer := container.NewStack()
	thirdRowContainer := container.NewGridWithColumns(7)
	forthRowContainer := container.NewGridWithColumns(2, watch4Button, watch6Button)
	mainGrid := container.NewGridWithRows(4, firstRowContainer, secondRowContainer, thirdRowContainer, forthRowContainer)
	return mainGrid
}
