package ru.nsu.vbalashov2.onlinesnake.ui

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JPanel

class ExitPanel : JPanel() {
    private val exitButton = JButton("Exit")

    init {
        this.layout = GridBagLayout()

        val gbcExitButton = GridBagConstraints()
        gbcExitButton.gridx = 0
        gbcExitButton.gridy = 0
        gbcExitButton.gridwidth = 1
        gbcExitButton.gridheight = 1
        gbcExitButton.fill = GridBagConstraints.BOTH
        gbcExitButton.anchor = GridBagConstraints.NORTHWEST
        gbcExitButton.weightx = 100.0
        gbcExitButton.weighty = 100.0
        this.add(exitButton, gbcExitButton)
    }
}