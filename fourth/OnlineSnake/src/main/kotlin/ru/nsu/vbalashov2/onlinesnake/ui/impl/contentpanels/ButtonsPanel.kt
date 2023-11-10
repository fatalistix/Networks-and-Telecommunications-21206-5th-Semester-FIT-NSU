package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class ButtonsPanel : JPanel() {
    private val exitButton = JButton("Exit")
    private val newGameButton = JButton("New")

    init {
        this.layout = GridBagLayout()
    }

    init {
        val gbcExitButton = GridBagConstraints()
        gbcExitButton.gridx = 0
        gbcExitButton.gridy = 0
        gbcExitButton.gridwidth = 1
        gbcExitButton.gridheight = 1
        gbcExitButton.fill = GridBagConstraints.BOTH
        gbcExitButton.anchor = GridBagConstraints.NORTHWEST
        gbcExitButton.weightx = 50.0
        gbcExitButton.weighty = 100.0
        this.add(exitButton, gbcExitButton)
    }

    init {
        val gbcNewGameButton = GridBagConstraints()
        gbcNewGameButton.gridx = 1
        gbcNewGameButton.gridy = 0
        gbcNewGameButton.gridwidth = 1
        gbcNewGameButton.gridheight = 1
        gbcNewGameButton.fill = GridBagConstraints.BOTH
        gbcNewGameButton.anchor = GridBagConstraints.NORTHWEST
        gbcNewGameButton.weightx = 500.0
        gbcNewGameButton.weighty = 100.0
        this.add(newGameButton, gbcNewGameButton)
    }
}