package ru.nsu.vbalashov2.onlinesnake.ui

import ru.nsu.vbalashov2.onlinesnake.ui.contentpanels.*
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel

class GameUIPanel : JPanel() {
    private val gameFieldPanel = GameFieldPanel()
    private val ratingPanel = RatingPanel()
    private val availableGamesPanel = AvailableGamesPanel()
    private val currentGameInfoPanel = CurrentGameInfoPanel()
    private val exitPanel = ExitPanel()
    private val newGamePanel = NewGamePanel()

    init {
        this.layout = GridBagLayout()

        val gridBagInsets = Insets(2, 2, 2, 2)

        val gbcGameFieldPanel = GridBagConstraints()
        gbcGameFieldPanel.gridx = 0
        gbcGameFieldPanel.gridy = 0
        gbcGameFieldPanel.gridwidth = 1
        gbcGameFieldPanel.gridheight = 3
        gbcGameFieldPanel.fill = GridBagConstraints.BOTH
        gbcGameFieldPanel.anchor = GridBagConstraints.NORTHWEST
        gbcGameFieldPanel.weightx = 70.0
        gbcGameFieldPanel.weighty = 90.0
        gbcGameFieldPanel.insets = gridBagInsets
        this.add(gameFieldPanel, gbcGameFieldPanel)

        val gbcRatingPanel = GridBagConstraints()
        gbcRatingPanel.gridx = 1
        gbcRatingPanel.gridy = 0
        gbcRatingPanel.gridwidth = 2
        gbcRatingPanel.gridheight = 1
        gbcRatingPanel.fill = GridBagConstraints.BOTH
        gbcRatingPanel.anchor = GridBagConstraints.NORTHWEST
        gbcRatingPanel.weightx = 30.0
        gbcRatingPanel.weighty = 43.0
        gbcRatingPanel.insets = gridBagInsets
        this.add(ratingPanel, gbcRatingPanel)

        val gbcExitPanel = GridBagConstraints()
        gbcExitPanel.gridx = 1
        gbcExitPanel.gridy = 1
        gbcExitPanel.gridwidth = 1
        gbcExitPanel.gridheight = 1
        gbcExitPanel.fill = GridBagConstraints.BOTH
        gbcExitPanel.anchor = GridBagConstraints.NORTHWEST
        gbcExitPanel.weightx = 15.0
        gbcExitPanel.weighty = 2.0
        gbcExitPanel.insets = gridBagInsets
        this.add(exitPanel, gbcExitPanel)

        val gbcNewGamePanel = GridBagConstraints()
        gbcNewGamePanel.gridx = 2
        gbcNewGamePanel.gridy = 1
        gbcNewGamePanel.gridwidth = 1
        gbcNewGamePanel.gridheight = 1
        gbcNewGamePanel.fill = GridBagConstraints.BOTH
        gbcNewGamePanel.anchor = GridBagConstraints.NORTHWEST
        gbcNewGamePanel.weightx = 15.0
        gbcNewGamePanel.weighty = 2.0
        gbcNewGamePanel.insets = gridBagInsets
        this.add(newGamePanel, gbcNewGamePanel)

        val gbcAvailableGamesPanel = GridBagConstraints()
        gbcAvailableGamesPanel.gridx = 1
        gbcAvailableGamesPanel.gridy = 2
        gbcAvailableGamesPanel.gridwidth = 2
        gbcAvailableGamesPanel.gridheight = 1
        gbcAvailableGamesPanel.fill = GridBagConstraints.BOTH
        gbcAvailableGamesPanel.anchor = GridBagConstraints.NORTHWEST
        gbcAvailableGamesPanel.weightx = 30.0
        gbcAvailableGamesPanel.weighty = 45.0
        gbcAvailableGamesPanel.insets = gridBagInsets
        this.add(availableGamesPanel, gbcAvailableGamesPanel)

        val gbcCurrentGameInfoPanel = GridBagConstraints()
        gbcCurrentGameInfoPanel.gridx = 0
        gbcCurrentGameInfoPanel.gridy = 3
        gbcCurrentGameInfoPanel.gridwidth = 3
        gbcCurrentGameInfoPanel.gridheight = 1
        gbcCurrentGameInfoPanel.fill = GridBagConstraints.BOTH
        gbcCurrentGameInfoPanel.anchor = GridBagConstraints.NORTHWEST
        gbcCurrentGameInfoPanel.weightx = 100.0
        gbcCurrentGameInfoPanel.weighty = 10.0
        gbcCurrentGameInfoPanel.insets = gridBagInsets
        this.add(currentGameInfoPanel, gbcCurrentGameInfoPanel)
    }

    init {
        gameFieldPanel.background = Color.BLACK
        ratingPanel.background = Color.BLUE
        exitPanel.background = Color.ORANGE
        newGamePanel.background = Color.YELLOW
        availableGamesPanel.background = Color.RED
        currentGameInfoPanel.background = Color.GREEN
    }

    fun updateGameField(newField: IntArray, newFieldWidth: Int, newFieldHeight: Int) {
        this.gameFieldPanel.updateField(
            newField = newField,
            newFieldWidth = newFieldWidth,
            newFieldHeight = newFieldHeight
        )
    }

//    init {
//        gamePanel.preferredSize = Dimension(200, 200)
//        leftPanel.background = Color.CYAN
//        rightPanel.background = Color.RED
//        leftPanel.layout = GridLayout(1, 1)
//
//        val gameField = gamePanel.field
//        gameField[55] = 3
//        gameField[56] = -1
//        gamePanel.updateField(gameField)
//    }
}



//    init {
//        val vBox = Box(BoxLayout.PAGE_AXIS)
//        vBox.add(Box.createVerticalGlue())
//        vBox.add(gamePanel)
////        vBox.add(Box.createVerticalGlue())
//        val hBox = Box(BoxLayout.LINE_AXIS)
////        hBox.add(Box.createHorizontalGlue())
//        hBox.add(vBox)
////        hBox.add(Box.createHorizontalGlue())
//        leftPanel.add(hBox, CENTER_ALIGNMENT)
//    }