package ru.nsu.vbalashov2.onlinesnake.ui.impl.contentpanels

import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class GameConfigPanel : JPanel() {
    private val widthLabel = JLabel("Width:")
    private val heightLabel = JLabel("Height:")
    private val foodStaticLabel = JLabel("Food:")
    private val stateDelayMsLabel = JLabel("ms:")

    private val widthTextField = JTextField()
    private val heightTextField = JTextField()
    private val foodStaticTextField = JTextField()
    private val stateDelayMsTextField = JTextField()

    init {
        this.layout = GridLayout(2, 4)
    }

    init {
        this.add(widthLabel)
        this.add(heightLabel)
        this.add(foodStaticLabel)
        this.add(stateDelayMsLabel)
        this.add(widthTextField)
        this.add(heightTextField)
        this.add(foodStaticTextField)
        this.add(stateDelayMsTextField)
    }

//    init {
//        this.layout = GridBagLayout()
//    }
//
//    //-------
//    // LABELS
//    //-------
//    // WIDTH LABEL
//    init {
//        val gbcWidthLabel = GridBagConstraints()
//        gbcWidthLabel.gridx = 0
//        gbcWidthLabel.gridy = 0
//        gbcWidthLabel.gridwidth = 1
//        gbcWidthLabel.gridheight = 1
//        gbcWidthLabel.fill = GridBagConstraints.BOTH
//        gbcWidthLabel.anchor = GridBagConstraints.NORTHWEST
//        gbcWidthLabel.weightx = 25.0
//        gbcWidthLabel.weighty = 50.0
//        this.add(widthLabel, gbcWidthLabel)
//    }
//
//    // HEIGHT LABEL
//    init {
//        val gbcHeightLabel = GridBagConstraints()
//        gbcHeightLabel.gridx = 1
//        gbcHeightLabel.gridy = 0
//        gbcHeightLabel.gridwidth = 1
//        gbcHeightLabel.gridheight = 1
//        gbcHeightLabel.fill = GridBagConstraints.BOTH
//        gbcHeightLabel.anchor = GridBagConstraints.NORTHWEST
//        gbcHeightLabel.weightx = 25.0
//        gbcHeightLabel.weighty = 50.0
//        this.add(heightLabel, gbcHeightLabel)
//    }
//
//    // FOOD STATIC LABEL
//    init {
//        val gbcFoodStaticLabel = GridBagConstraints()
//        gbcFoodStaticLabel.gridx = 2
//        gbcFoodStaticLabel.gridy = 0
//        gbcFoodStaticLabel.gridwidth = 1
//        gbcFoodStaticLabel.gridheight = 1
//        gbcFoodStaticLabel.fill = GridBagConstraints.BOTH
//        gbcFoodStaticLabel.anchor = GridBagConstraints.NORTHWEST
//        gbcFoodStaticLabel.weightx = 25.0
//        gbcFoodStaticLabel.weighty = 50.0
//        this.add(foodStaticLabel, gbcFoodStaticLabel)
//    }
//
//    // STATE DELAY MS LABEL
//    init {
//        val gbcStateDelayMsLabel = GridBagConstraints()
//        gbcStateDelayMsLabel.gridx = 3
//        gbcStateDelayMsLabel.gridy = 0
//        gbcStateDelayMsLabel.gridwidth = 1
//        gbcStateDelayMsLabel.gridheight = 1
//        gbcStateDelayMsLabel.fill = GridBagConstraints.BOTH
//        gbcStateDelayMsLabel.anchor = GridBagConstraints.NORTHWEST
//        gbcStateDelayMsLabel.weightx = 25.0
//        gbcStateDelayMsLabel.weighty = 50.0
//        this.add(stateDelayMsLabel, gbcStateDelayMsLabel)
//    }
//
//    //------------
//    // TEXT FIELDS
//    //------------
//    // WIDTH TEXT FIELD
//    init {
//        val gbcWidthTextField = GridBagConstraints()
//        gbcWidthTextField.gridx = 0
//        gbcWidthTextField.gridy = 1
//        gbcWidthTextField.gridwidth = 1
//        gbcWidthTextField.gridheight = 1
//        gbcWidthTextField.fill = GridBagConstraints.BOTH
//        gbcWidthTextField.anchor = GridBagConstraints.NORTHWEST
//        gbcWidthTextField.weightx = 25.0
//        gbcWidthTextField.weighty = 50.0
//        this.add(widthTextField, gbcWidthTextField)
//    }
//
//    // HEIGHT TEXT FIELD
//    init {
//        val gbcHeightTextField = GridBagConstraints()
//        gbcHeightTextField.gridx = 1
//        gbcHeightTextField.gridy = 1
//        gbcHeightTextField.gridwidth = 1
//        gbcHeightTextField.gridheight = 1
//        gbcHeightTextField.fill = GridBagConstraints.BOTH
//        gbcHeightTextField.anchor = GridBagConstraints.NORTHWEST
//        gbcHeightTextField.weightx = 25.0
//        gbcHeightTextField.weighty = 50.0
//        this.add(heightTextField, gbcHeightTextField)
//    }
//
//    // FOOD STATIC TEXT FIELD
//    init {
//        val gbcFoodStaticTextField = GridBagConstraints()
//        gbcFoodStaticTextField.gridx = 2
//        gbcFoodStaticTextField.gridy = 1
//        gbcFoodStaticTextField.gridwidth = 1
//        gbcFoodStaticTextField.gridheight = 1
//        gbcFoodStaticTextField.fill = GridBagConstraints.BOTH
//        gbcFoodStaticTextField.anchor = GridBagConstraints.NORTHWEST
//        gbcFoodStaticTextField.weightx = 25.0
//        gbcFoodStaticTextField.weighty = 50.0
//        this.add(foodStaticTextField, gbcFoodStaticTextField)
//    }
//
//    // STATE DELAY MS TEXT FIELD
//    init {
//        val gbcStateDelayMsTextField = GridBagConstraints()
//        gbcStateDelayMsTextField.gridx = 3
//        gbcStateDelayMsTextField.gridy = 1
//        gbcStateDelayMsTextField.gridwidth = 1
//        gbcStateDelayMsTextField.gridheight = 1
//        gbcStateDelayMsTextField.fill = GridBagConstraints.BOTH
//        gbcStateDelayMsTextField.anchor = GridBagConstraints.NORTHWEST
//        gbcStateDelayMsTextField.weightx = 25.0
//        gbcStateDelayMsTextField.weighty = 50.0
//        this.add(stateDelayMsTextField, gbcStateDelayMsTextField)
//    }
}