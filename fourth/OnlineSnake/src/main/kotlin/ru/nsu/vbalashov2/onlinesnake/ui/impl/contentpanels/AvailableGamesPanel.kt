package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import ru.nsu.vbalashov2.onlinesnake.ui.AvailableGameSelectedListener
import ru.nsu.vbalashov2.onlinesnake.ui.dto.AvailableGameInfo
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import java.awt.Insets
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane

class AvailableGamesPanel : JPanel() {
    private val availableScrollPane = JScrollPane()
    private val content = JPanel()
    private val availableGameElementsList: MutableList<AvailableGameScrollElement> = mutableListOf()

    init {
        this.layout = GridLayout(1, 1)
    }

    init {
        this.add(availableScrollPane)
    }

    init {
        content.layout = GridLayout(0, 1)
        availableScrollPane.setViewportView(content)
        availableScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        availableScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
    }

    fun addAvailableGame(
        availableGameInfo: AvailableGameInfo,
        availableGameSelectedListener: AvailableGameSelectedListener
    ) : Int {
        availableGameElementsList += AvailableGameScrollElement(availableGameInfo, availableGameSelectedListener)
        return availableGameElementsList.size - 1
    }
}

