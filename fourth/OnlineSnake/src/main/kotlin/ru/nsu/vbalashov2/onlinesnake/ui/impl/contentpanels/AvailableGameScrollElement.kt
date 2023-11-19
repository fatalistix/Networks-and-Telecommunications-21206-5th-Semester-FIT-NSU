package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import ru.nsu.vbalashov2.onlinesnake.ui.AvailableGameSelectedListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameInfo
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class AvailableGameScrollElement(
    availableGameInfo: AvailableGameInfo,
    private val selectedListener: AvailableGameSelectedListener
) : JPanel() {
    private val gridBagInsets = Insets(2, 2, 2, 2)
    private val gameNameLabel = JLabel(availableGameInfo.gameName)
    private val numOfPlayers = JLabel(availableGameInfo.numOfPlayers.toString())
    private val gameFieldInfoLabel = JLabel(if (availableGameInfo.canJoin) "can join to " else "cannot join to " +
            "${availableGameInfo.foodStatic} food on ${availableGameInfo.height}x${availableGameInfo.width} " +
            "with ${availableGameInfo.stateDelayMs}MS")
    private val joinButton = JButton(">>")

    init {
        joinButton.addActionListener {
            selectedListener.onSelected()
        }
    }

    init {
        this.layout = GridBagLayout()
    }

    init {
        val gbcGameNameLabel = GridBagConstraints()
        gbcGameNameLabel.gridx = 0
        gbcGameNameLabel.gridy = 0
        gbcGameNameLabel.gridwidth = 1
        gbcGameNameLabel.gridheight = 1
        gbcGameNameLabel.fill = GridBagConstraints.BOTH
        gbcGameNameLabel.anchor = GridBagConstraints.NORTHWEST
        gbcGameNameLabel.weightx = 30.0
        gbcGameNameLabel.weighty = 100.0
        gbcGameNameLabel.insets = gridBagInsets
        this.add(gameNameLabel, gbcGameNameLabel)
    }

    init {
        val gbcNumOfPlayersLabel = GridBagConstraints()
        gbcNumOfPlayersLabel.gridx = 1
        gbcNumOfPlayersLabel.gridy = 0
        gbcNumOfPlayersLabel.gridwidth = 1
        gbcNumOfPlayersLabel.gridheight = 1
        gbcNumOfPlayersLabel.fill = GridBagConstraints.BOTH
        gbcNumOfPlayersLabel.anchor = GridBagConstraints.NORTHWEST
        gbcNumOfPlayersLabel.weightx = 5.0
        gbcNumOfPlayersLabel.weighty = 100.0
        gbcNumOfPlayersLabel.insets = gridBagInsets
        this.add(numOfPlayers, gbcNumOfPlayersLabel)
    }

    init {
        val gbcGameFieldInfoLabel = GridBagConstraints()
        gbcGameFieldInfoLabel.gridx = 2
        gbcGameFieldInfoLabel.gridy = 0
        gbcGameFieldInfoLabel.gridwidth = 1
        gbcGameFieldInfoLabel.gridheight = 1
        gbcGameFieldInfoLabel.fill = GridBagConstraints.BOTH
        gbcGameFieldInfoLabel.anchor = GridBagConstraints.NORTHWEST
        gbcGameFieldInfoLabel.weightx = 55.0
        gbcGameFieldInfoLabel.weighty = 100.0
        gbcGameFieldInfoLabel.insets = gridBagInsets
        this.add(gameFieldInfoLabel, gbcGameFieldInfoLabel)
    }

    init {
        val gbcJoinButton = GridBagConstraints()
        gbcJoinButton.gridx = 2
        gbcJoinButton.gridy = 0
        gbcJoinButton.gridwidth = 1
        gbcJoinButton.gridheight = 1
        gbcJoinButton.fill = GridBagConstraints.BOTH
        gbcJoinButton.anchor = GridBagConstraints.NORTHWEST
        gbcJoinButton.weightx = 10.0
        gbcJoinButton.weighty = 100.0
        gbcJoinButton.insets = gridBagInsets
        this.add(joinButton, gbcJoinButton)
    }

    fun upadateInfo(availableGameInfo: AvailableGameInfo) {
        gameNameLabel.text = availableGameInfo.gameName
        numOfPlayers.text = availableGameInfo.numOfPlayers.toString()
        gameFieldInfoLabel.text = if (availableGameInfo.canJoin) "can join to " else "cannot join to " +
                "${availableGameInfo.foodStatic} food on ${availableGameInfo.height}x${availableGameInfo.width} " +
                "with ${availableGameInfo.stateDelayMs}MS"
    }
}