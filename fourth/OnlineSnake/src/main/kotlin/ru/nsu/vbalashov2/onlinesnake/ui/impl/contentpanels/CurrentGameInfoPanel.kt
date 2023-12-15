package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel

class CurrentGameInfoPanel : JPanel() {
    private val ownerPrefix = "Owner: "
    private val sizePrefix = "Field size: "
    private val foodPrefix = "Food on field: "
    private val ownerLabel = JLabel()
    private val sizeLabel = JLabel()
    private val foodLabel = JLabel()
    init {
        this.layout = GridLayout(1, 3)
        this.add(ownerLabel)
        this.add(sizeLabel)
        this.add(foodLabel)
    }

    fun updateCurrentGameInfo(fieldWidth: Int, fieldHeight: Int, foodOnField: Int, ownerName: String) {
        ownerLabel.text = ownerPrefix + ownerName
        sizeLabel.text = sizePrefix + "${fieldWidth}x${fieldHeight}"
        foodLabel.text = foodPrefix + foodOnField
    }

    fun clearCurrentGameInfo() {
        ownerLabel.text = ""
        sizeLabel.text = ""
        foodLabel.text = ""
    }
}