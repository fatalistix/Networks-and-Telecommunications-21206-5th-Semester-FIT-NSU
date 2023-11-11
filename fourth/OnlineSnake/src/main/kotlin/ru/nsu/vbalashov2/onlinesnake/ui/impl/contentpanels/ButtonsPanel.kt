package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import ru.nsu.vbalashov2.onlinesnake.ui.ExitListener
import ru.nsu.vbalashov2.onlinesnake.ui.NewGameListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.GameConfig
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JPanel

class ButtonsPanel : JPanel(), ValidationFailListener, ValidationSuccessListener {
    private val exitButton = JButton("Exit")
    private val newGameButton = JButton("New")

    private val newGameListenersList:
            MutableList<NewGameListener> = mutableListOf()
    private val exitListenersList:
            MutableList<ExitListener> = mutableListOf()

    private var verifiedGameConfig: GameConfig = GameConfig(0, 0, 0, 0)

    init {
        this.layout = GridLayout(1, 2)
    }

    init {
        this.add(exitButton)
        this.add(newGameButton)
    }

    init {
        newGameButton.isEnabled = false
    }

    fun addNewGameListener(listener: NewGameListener): Int {
        this.newGameListenersList += listener
        return this.newGameListenersList.size - 1
    }

    fun addExitListener(listener: ExitListener): Int {
        this.exitListenersList += listener
        return this.exitListenersList.size - 1
    }

    override fun validationSuccess(gameConfig: GameConfig) {
        verifiedGameConfig = gameConfig
        newGameButton.isEnabled = true
    }

    override fun validationFail() {
        newGameButton.isEnabled = false
    }


//    init {
//        this.layout = GridBagLayout()
//    }
//
//    init {
//        val gbcExitButton = GridBagConstraints()
//        gbcExitButton.gridx = 0
//        gbcExitButton.gridy = 0
//        gbcExitButton.gridwidth = 1
//        gbcExitButton.gridheight = 1
//        gbcExitButton.fill = GridBagConstraints.BOTH
//        gbcExitButton.anchor = GridBagConstraints.NORTHWEST
//        gbcExitButton.weightx = 50.0
//        gbcExitButton.weighty = 100.0
//        this.add(exitButton, gbcExitButton)
//    }
//
//    init {
//        val gbcNewGameButton = GridBagConstraints()
//        gbcNewGameButton.gridx = 1
//        gbcNewGameButton.gridy = 0
//        gbcNewGameButton.gridwidth = 1
//        gbcNewGameButton.gridheight = 1
//        gbcNewGameButton.fill = GridBagConstraints.BOTH
//        gbcNewGameButton.anchor = GridBagConstraints.NORTHWEST
//        gbcNewGameButton.weightx = 500.0
//        gbcNewGameButton.weighty = 100.0
//        this.add(newGameButton, gbcNewGameButton)
//    }
}