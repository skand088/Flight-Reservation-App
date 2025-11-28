package com.flightreservation.ui.decorators;

import javax.swing.JButton;

public abstract class ButtonDecorator extends JButton {
    protected JButton decoratedButton;

    public ButtonDecorator(JButton button) {
        this.decoratedButton = button;
        setText(button.getText());
        setFont(button.getFont());
        setBackground(button.getBackground());
        setForeground(button.getForeground());
        setFocusPainted(button.isFocusPainted());
        setOpaque(button.isOpaque());
        setContentAreaFilled(button.isContentAreaFilled());
        setCursor(button.getCursor());
        setBorderPainted(button.isBorderPainted());

        if (button.getPreferredSize() != null) {
            setPreferredSize(button.getPreferredSize());
        }

        for (var listener : button.getActionListeners()) {
            addActionListener(listener);
        }
    }

    public JButton getDecoratedButton() {
        return decoratedButton;
    }
}
