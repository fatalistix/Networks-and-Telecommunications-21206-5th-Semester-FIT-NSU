package view

import (
	"fyne.io/fyne/v2"
	"fyne.io/fyne/v2/container"
	"fyne.io/fyne/v2/data/binding"
	"fyne.io/fyne/v2/layout"
	"fyne.io/fyne/v2/widget"

	"networks/first/model"
	"networks/first/model/ip16"
	"networks/first/model/ip4"
)

func newStartWindowViewFirstRowColumnGrid(
	entryValidator fyne.StringValidator,
	onValidationSuccess func(string),
	onValidationError func(),
) *fyne.Container {
	staticStrings := []string{"224", ".", "0", ".", "0", "."}
	cont := container.NewGridWithColumns(7)

	for _, str := range staticStrings {
		label := widget.NewLabelWithStyle(
			str,
			fyne.TextAlignCenter,
			fyne.TextStyle{Bold: true},
		)
		cont.Add(container.NewCenter(label))
	}

	changableStr := ""
	entry := widget.NewEntryWithData(binding.BindString(&changableStr))
	entry.MultiLine = false
	entry.Validator = entryValidator
	entry.OnChanged = func(s string) {
		err := entry.Validate()
		if err != nil {
			onValidationError()
		} else {
			onValidationSuccess(ip4.IP4LocalMulticastPrefix + s)
		}
	}

	cont.Add(container.NewCenter(container.NewBorder(entry, nil, nil, nil)))

	return cont
}

func newStartWindowViewSecondRowCenter() *fyne.Container {
	orLabel := widget.NewLabelWithStyle(
		"OR",
		fyne.TextAlignCenter,
		fyne.TextStyle{Bold: true},
	)
	return container.NewCenter(orLabel)
}

func newStartWindowViewThirdRowColumnGrid(
	firstEntryValidator, secondEntryValidator fyne.StringValidator,
	onValidationSuccess func(string),
	onValidationError func(),
) *fyne.Container {
	cont := container.NewGridWithColumns(15)
	staticStrings := []string{"FF02", ":", "0", ":", "0", ":", "0", ":", "0", ":", "1", ":"}

	for _, str := range staticStrings {
		label := widget.NewLabelWithStyle(
			str,
			fyne.TextAlignCenter,
			fyne.TextStyle{Bold: true},
		)
		cont.Add(container.NewCenter(label))
	}

	firstStr := ""
	secondStr := ""

	firstEntry := widget.NewEntryWithData(binding.BindString(&firstStr))
	firstEntry.MultiLine = false
	firstEntry.Validator = firstEntryValidator

	secondEntry := widget.NewEntryWithData(binding.BindString(&secondStr))
	secondEntry.MultiLine = false
	secondEntry.Validator = secondEntryValidator

	firstEntry.OnChanged = func(s string) {
		if firstEntry.Validate() == nil && secondEntry.Validate() == nil {
			onValidationSuccess(ip16.IP16LocalMulticastPrefix + s + ":" + secondEntry.Text)
		} else {
			onValidationError()
		}
	}

	secondEntry.OnChanged = func(s string) {
		if firstEntry.Validate() == nil && secondEntry.Validate() == nil {
			onValidationSuccess(ip16.IP16LocalMulticastPrefix + firstEntry.Text + ":" + s)
		} else {
			onValidationError()
		}
	}

	label := widget.NewLabelWithStyle(
		":",
		fyne.TextAlignCenter,
		fyne.TextStyle{Bold: true, Monospace: true},
	)

	cont.Add(container.NewCenter(firstEntry))
	cont.Add(container.NewCenter(label))
	cont.Add(container.NewCenter(secondEntry))

	return cont
}

func NewStartWindowView(
	onIPv4ButtonPressed, onIPv6ButtonPressed func(ip string),
	onSettingsButtonPressed func(),
) *fyne.Container {
	ip4str := ""
	ip16str := ""

	var ipv4mcv model.LocalMulticastValidator = ip4.MakeIP4LocalMulticastValidator()
	var ipv6mcv model.LocalMulticastValidator = ip16.MakeIP16LocalMulticastValidator()

	watch4Button := widget.NewButton("Watch IPv4", func() {
		onIPv4ButtonPressed(ip4str)
	})
	watch4Button.Importance = widget.HighImportance
	watch4Button.Disable()

	watch6Button := widget.NewButton("Watch IPv6", func() {
		onIPv6ButtonPressed(ip16str)
	})
	watch6Button.Importance = widget.HighImportance
	watch6Button.Disable()

	firstRowContainer := newStartWindowViewFirstRowColumnGrid(func(s string) error {
		return ipv4mcv.Validate(ip4.IP4LocalMulticastPrefix + s)
	}, func(s string) {
		ip4str = s
		watch4Button.Enable()
	}, func() {
		watch4Button.Disable()
	})
	secondRowContainer := newStartWindowViewSecondRowCenter()
	thirdRowContainer := newStartWindowViewThirdRowColumnGrid(func(s string) error {
		return ipv6mcv.Validate(ip16.IP16LocalMulticastPrefix + s + ":00ff")
	}, func(s string) error {
		return ipv6mcv.Validate(ip16.IP16LocalMulticastPrefix + "ff01:" + s)
	}, func(s string) {
		ip16str = s
		watch6Button.Enable()
	}, func() {
		watch6Button.Disable()
	})

	settingsButton := widget.NewButton("Settings", onSettingsButtonPressed)

	forthRowContainer := container.NewGridWithColumns(
		4,
		settingsButton,
		layout.NewSpacer(),
		watch4Button,
		watch6Button,
	)
	mainGrid := container.NewGridWithRows(
		4,
		firstRowContainer,
		secondRowContainer,
		thirdRowContainer,
		forthRowContainer,
	)
	return mainGrid
}
