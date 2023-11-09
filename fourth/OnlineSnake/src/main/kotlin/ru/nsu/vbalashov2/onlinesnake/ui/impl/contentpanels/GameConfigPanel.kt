package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JLabel
import javax.swing.JPanel

class GameConfigPanel : JPanel() {
    private val widthLabel = JLabel("Width:")
    init {
        this.layout = GridBagLayout()
    }

    init {
        val gbcWidthLabel = GridBagConstraints()
        gbcWidthLabel.gridx = 0
        gbcWidthLabel.gridy = 0
        gbcWidthLabel.gridwidth = 1
        gbcWidthLabel.gridheight = 1
        gbcWidthLabel.fill = GridBagConstraints.BOTH
        gbcWidthLabel.anchor = GridBagConstraints.NORTHWEST
        gbcWidthLabel.weightx = 25.0
        gbcWidthLabel.weighty = 50.0
        this.add(widthLabel, gbcWidthLabel)
    }
}