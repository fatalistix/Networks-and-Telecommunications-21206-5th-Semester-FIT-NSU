package ru.nsu.vbalashov2.onlinesnake.ui

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ScrollPaneLayout
import javax.swing.table.TableColumn

//    private val columnNames = arrayOf("Owner", "#", "Size", "Food", "Enter")
//    private val availableGamesTable = JTable(arrayOf(arrayOf("1", "2", "3", "4", "5")), columnNames)
//
//    init {
//        this.layout = GridBagLayout()
//
//        val gbcAvailableGamesTable = GridBagConstraints()
//        gbcAvailableGamesTable.gridx = 0
//        gbcAvailableGamesTable.gridy = 0
//        gbcAvailableGamesTable.gridwidth = 1
//        gbcAvailableGamesTable.gridheight = 1
//        gbcAvailableGamesTable.fill = GridBagConstraints.BOTH
//        gbcAvailableGamesTable.anchor = GridBagConstraints.NORTHWEST
//        gbcAvailableGamesTable.weightx = 100.0
//        gbcAvailableGamesTable.weighty = 100.0
//        this.add(availableGamesTable, gbcAvailableGamesTable)
//    }

class AvailableGamesPanel : JPanel() {
    private val availableScrollPane = JScrollPane()

    init {
        this.layout = GridBagLayout()

        val gbcAvailableScrollPane = GridBagConstraints()
        gbcAvailableScrollPane.gridx = 0
        gbcAvailableScrollPane.gridy = 0
        gbcAvailableScrollPane.gridwidth = 1
        gbcAvailableScrollPane.gridheight = 1
        gbcAvailableScrollPane.fill = GridBagConstraints.BOTH
        gbcAvailableScrollPane.anchor = GridBagConstraints.NORTHWEST
        gbcAvailableScrollPane.weightx = 100.0
        gbcAvailableScrollPane.weighty = 100.0
        this.add(availableScrollPane, gbcAvailableScrollPane)
    }

    init {
        val content = JPanel()
        content.layout = GridLayout(0, 1)
        content.add(JButton("HELLO"))
        content.add(JButton("HOLA"))
        content.add(JButton("HOLb"))
        content.add(JButton("HOLc"))
        availableScrollPane.setViewportView(content)
        availableScrollPane.horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        availableScrollPane.verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS
    }
}