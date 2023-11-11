package ru.nsu.vbalashov2.onlinesnake.ui.impl

import ru.nsu.vbalashov2.onlinesnake.ui.*
import ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels.*
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JPanel

class GameUIPanel : JPanel(), GameUI {
    private val gameFieldPanel = GameFieldPanel()
    private val ratingPanel = RatingPanel()
    private val buttonsPanel = ButtonsPanel()
    private val gameConfigPanel = GameConfigPanel()
    private val availableGamesPanel = AvailableGamesPanel()
    private val currentGameInfoPanel = CurrentGameInfoPanel()
    private val gridBagInsets = Insets(2, 2, 2, 2)

    init {
        this.layout = GridBagLayout()
    }

    // Game Field Panel configuration
    init {
        val gbcGameFieldPanel = GridBagConstraints()
        gbcGameFieldPanel.gridx = 0
        gbcGameFieldPanel.gridy = 0
        gbcGameFieldPanel.gridwidth = 1
        gbcGameFieldPanel.gridheight = 4
        gbcGameFieldPanel.fill = GridBagConstraints.BOTH
        gbcGameFieldPanel.anchor = GridBagConstraints.NORTHWEST
        gbcGameFieldPanel.weightx = 70.0
        gbcGameFieldPanel.weighty = 90.0
        gbcGameFieldPanel.insets = gridBagInsets
        this.add(gameFieldPanel, gbcGameFieldPanel)
    }

    // Rating Panel configuration
    init {
        val gbcRatingPanel = GridBagConstraints()
        gbcRatingPanel.gridx = 1
        gbcRatingPanel.gridy = 0
        gbcRatingPanel.gridwidth = 1
        gbcRatingPanel.gridheight = 1
        gbcRatingPanel.fill = GridBagConstraints.BOTH
        gbcRatingPanel.anchor = GridBagConstraints.NORTHWEST
        gbcRatingPanel.weightx = 30.0
        gbcRatingPanel.weighty = 43.0
        gbcRatingPanel.insets = gridBagInsets
        this.add(ratingPanel, gbcRatingPanel)
    }

    // Buttons Panel configuration
    init {
        val gbcButtonsPanel = GridBagConstraints()
        gbcButtonsPanel.gridx = 1
        gbcButtonsPanel.gridy = 1
        gbcButtonsPanel.gridwidth = 1
        gbcButtonsPanel.gridheight = 1
        gbcButtonsPanel.fill = GridBagConstraints.BOTH
        gbcButtonsPanel.anchor = GridBagConstraints.NORTHWEST
        gbcButtonsPanel.weightx = 30.0
        gbcButtonsPanel.weighty = 2.0
        gbcButtonsPanel.insets = gridBagInsets
        this.add(buttonsPanel, gbcButtonsPanel)
    }

    init {
        gameConfigPanel.addValidationFailListener(buttonsPanel)
        gameConfigPanel.addValidationSuccessListener(buttonsPanel)
    }

    // Game Config panel
    init {
        val gbcGameConfigPanel = GridBagConstraints()
        gbcGameConfigPanel.gridx = 1
        gbcGameConfigPanel.gridy = 2
        gbcGameConfigPanel.gridwidth = 1
        gbcGameConfigPanel.gridheight = 1
        gbcGameConfigPanel.fill = GridBagConstraints.BOTH
        gbcGameConfigPanel.anchor = GridBagConstraints.NORTHWEST
        gbcGameConfigPanel.weightx = 30.0
        gbcGameConfigPanel.weighty = 5.0
        gbcGameConfigPanel.insets = gridBagInsets
        this.add(gameConfigPanel, gbcGameConfigPanel)
    }

    // Available Games list
    init {
        val gbcAvailableGamesPanel = GridBagConstraints()
        gbcAvailableGamesPanel.gridx = 1
        gbcAvailableGamesPanel.gridy = 3
        gbcAvailableGamesPanel.gridwidth = 1
        gbcAvailableGamesPanel.gridheight = 1
        gbcAvailableGamesPanel.fill = GridBagConstraints.BOTH
        gbcAvailableGamesPanel.anchor = GridBagConstraints.NORTHWEST
        gbcAvailableGamesPanel.weightx = 30.0
        gbcAvailableGamesPanel.weighty = 40.0
        gbcAvailableGamesPanel.insets = gridBagInsets
        this.add(availableGamesPanel, gbcAvailableGamesPanel)
    }

    // Current Game Info panel
    init {
        val gbcCurrentGameInfoPanel = GridBagConstraints()
        gbcCurrentGameInfoPanel.gridx = 0
        gbcCurrentGameInfoPanel.gridy = 4
        gbcCurrentGameInfoPanel.gridwidth = 2
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
        buttonsPanel.background = Color.ORANGE
        availableGamesPanel.background = Color.RED
        currentGameInfoPanel.background = Color.GREEN
    }

    override fun updateField(field: IntArray, width: Int, height: Int) {
        this.gameFieldPanel.updateField(
            newField = field,
            newFieldWidth = width,
            newFieldHeight = height
        )
    }

    override fun addNewGameListener(listener: NewGameListener) : Int {
        return buttonsPanel.addNewGameListener(listener)
    }

    override fun addWidthValidationRule(validationRule: WidthValidationRule): Int {
        TODO("Not yet implemented")
    }

    override fun addHeightValidationRule(validationRule: HeightValidationRule): Int {
        TODO("Not yet implemented")
    }

    override fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule): Int {
        TODO("Not yet implemented")
    }

    override fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule): Int {
        TODO("Not yet implemented")
    }
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