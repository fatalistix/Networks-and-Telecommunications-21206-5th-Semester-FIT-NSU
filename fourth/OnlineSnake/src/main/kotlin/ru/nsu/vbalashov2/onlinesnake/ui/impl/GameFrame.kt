package ru.nsu.vbalashov2.onlinesnake.ui.impl

import ru.nsu.vbalashov2.onlinesnake.ui.*
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameInfo
import ru.nsu.vbalashov2.onlinesnake.ui.dto.KeyPoint
import java.awt.Dimension
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities

class GameFrame : JFrame(), GameUI {
    private val mainFrameName = "OnlineSnake"
    private val gameUIPanel = GameUIPanel()
    private val applicationCloseListenersList: MutableList<ApplicationCloseListener> = mutableListOf()

    init {
        this.title = mainFrameName
        this.contentPane.add(gameUIPanel)
        this.defaultCloseOperation = EXIT_ON_CLOSE
        this.pack()
        this.setLocationRelativeTo(null)
        this.size = Dimension(200, 200)
    }

    init {
        addWindowListener(object: WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                super.windowClosing(e)
                applicationCloseListenersList.forEach { it.onClose() }
            }
        })
    }

    override fun start() {
        this.isVisible = true
    }

    override fun updateField(
        snakesKeyPointsList: List<List<KeyPoint>>,
        foodList: List<KeyPoint>,
        width: Int,
        height: Int
    ) = SwingUtilities.invokeLater { gameUIPanel.updateField(snakesKeyPointsList, foodList, width, height) }

    override fun addNewGameListener(listener: NewGameListener): Int =
        gameUIPanel.addNewGameListener(listener)

    override fun addExitListener(listener: ExitListener): Int =
        gameUIPanel.addExitListener(listener)

    override fun addWidthValidationRule(validationRule: WidthValidationRule): Int =
        gameUIPanel.addWidthValidationRule(validationRule)

    override fun addHeightValidationRule(validationRule: HeightValidationRule): Int =
        gameUIPanel.addHeightValidationRule(validationRule)

    override fun addFoodStaticValidationRule(validationRule: FoodStaticValidationRule): Int =
        gameUIPanel.addFoodStaticValidationRule(validationRule)

    override fun addStateDelayMsValidationRule(validationRule: StateDelayMsValidationRule): Int =
        gameUIPanel.addStateDelayMsValidationRule(validationRule)

    override fun addApplicationCloseListener(listener: ApplicationCloseListener): Int {
        this.applicationCloseListenersList += listener
        return this.applicationCloseListenersList.size - 1
    }

    override fun addAvailableGame(availableGameInfo: AvailableGameInfo, selectedListener: AvailableGameSelectedListener): Int {
        return this.gameUIPanel.addAvailableGame(availableGameInfo, selectedListener)
    }

    override fun removeAvailableGame(index: Int) {
        this.gameUIPanel.removeAvailableGame(index)
    }

    override fun updateAvailableGame(availableGameInfo: AvailableGameInfo, index: Int) {
        this.gameUIPanel.updateAvailableGame(availableGameInfo, index)
    }
}