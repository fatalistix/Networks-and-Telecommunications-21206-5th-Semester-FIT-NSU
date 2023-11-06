package ru.nsu.vbalashov2.onlinesnake.ui.contentpanels

import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel

class CurrentGameInfoPanel : JPanel() {
    private val ownerLabel = JLabel("Owner: ")
    private val sizeLabel = JLabel("Field size: ")
    private val foodLabel = JLabel("Food: ")
    init {
        this.layout = GridLayout(1, 3)
        this.add(ownerLabel)
        this.add(sizeLabel)
        this.add(foodLabel)
    }
}