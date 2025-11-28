package com.flightreservation.ui.decorators;

import javax.swing.JButton;

public class TooltipDecorator extends ButtonDecorator {

    public TooltipDecorator(JButton button, String tooltipText) {
        super(button);
        setToolTipText(tooltipText);
    }
}
