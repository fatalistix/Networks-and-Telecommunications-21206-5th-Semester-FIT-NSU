package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JPanel

class NewGamePanel : JPanel() {
    private val newGameButton = JButton("New")

    init {
        this.layout = GridBagLayout()

        val gbcNewGameButton = GridBagConstraints()
        gbcNewGameButton.gridx = 0
        gbcNewGameButton.gridy = 0
        gbcNewGameButton.gridwidth = 1
        gbcNewGameButton.gridheight = 1
        gbcNewGameButton.fill = GridBagConstraints.BOTH
        gbcNewGameButton.anchor = GridBagConstraints.NORTHWEST
        gbcNewGameButton.weightx = 100.0
        gbcNewGameButton.weighty = 100.0
        this.add(newGameButton, gbcNewGameButton)
    }
}