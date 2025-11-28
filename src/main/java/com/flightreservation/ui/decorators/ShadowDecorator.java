package com.flightreservation.ui.decorators;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.Border;

public class ShadowDecorator extends ButtonDecorator {

    public ShadowDecorator(JButton button) {
        super(button);
        Border lineBorder = BorderFactory.createLineBorder(new Color(0, 0, 0, 30), 1);
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        setBorderPainted(true);
    }

    public ShadowDecorator(JButton button, Color shadowColor, int thickness) {
        super(button);
        Border lineBorder = BorderFactory.createLineBorder(shadowColor, thickness);
        Border emptyBorder = BorderFactory.createEmptyBorder(2, 2, 2, 2);
        setBorder(BorderFactory.createCompoundBorder(lineBorder, emptyBorder));
        setBorderPainted(true);
    }
}
