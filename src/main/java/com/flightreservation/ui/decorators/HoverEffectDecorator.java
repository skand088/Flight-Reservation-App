package com.flightreservation.ui.decorators;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

public class HoverEffectDecorator extends ButtonDecorator {
    private Color originalColor;
    private Color hoverColor;

    public HoverEffectDecorator(JButton button) {
        super(button);
        this.originalColor = button.getBackground();
        this.hoverColor = brightenColor(originalColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalColor);
            }
        });
    }

    public HoverEffectDecorator(JButton button, Color hoverColor) {
        super(button);
        this.originalColor = button.getBackground();
        this.hoverColor = hoverColor;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(originalColor);
            }
        });
    }

    private Color brightenColor(Color color) {
        int r = Math.min(255, (int) (color.getRed() * 1.2));
        int g = Math.min(255, (int) (color.getGreen() * 1.2));
        int b = Math.min(255, (int) (color.getBlue() * 1.2));
        return new Color(r, g, b);
    }
}
