package ru.nsu.vbalashov2.onlinesnake.ui.contentpanels

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class RatingPanel : JPanel() {
    private val ratingLabel = JLabel("Rating")
    private val ratingScrollPane = JScrollPane()
    init {
        ratingLabel.horizontalAlignment = JLabel.CENTER
    }
    init {
        this.layout = GridBagLayout()

        val gridBagInsets = Insets(2, 2, 2, 2)

        val gbcRatingLabel = GridBagConstraints()
        gbcRatingLabel.gridx = 0
        gbcRatingLabel.gridy = 0
        gbcRatingLabel.gridwidth = 1
        gbcRatingLabel.gridheight = 1
        gbcRatingLabel.fill = GridBagConstraints.BOTH
        gbcRatingLabel.anchor = GridBagConstraints.NORTHWEST
        gbcRatingLabel.weightx = 100.0
        gbcRatingLabel.weighty = 5.0
        gbcRatingLabel.insets = gridBagInsets
        this.add(ratingLabel, gbcRatingLabel)

        val gbcRatingScrollPane = GridBagConstraints()
        gbcRatingScrollPane.gridx = 0
        gbcRatingScrollPane.gridy = 1
        gbcRatingScrollPane.gridwidth = 1
        gbcRatingScrollPane.gridheight = 1
        gbcRatingScrollPane.fill = GridBagConstraints.BOTH
        gbcRatingScrollPane.anchor = GridBagConstraints.NORTHWEST
        gbcRatingScrollPane.weightx = 100.0
        gbcRatingScrollPane.weighty = 95.0
        gbcRatingScrollPane.insets = gridBagInsets
        this.add(ratingScrollPane, gbcRatingScrollPane)
    }
}