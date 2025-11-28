package com.flightreservation.ui.decorators;

import javax.swing.JButton;

/**
 * factory for creating decorated buttons with common combinations
 * combines Decorator with Factory
 */
public class ButtonDecoratorFactory {

    public static JButton withHoverEffect(JButton button) {
        return new HoverEffectDecorator(button);
    }

    public static JButton withTooltip(JButton button, String tooltip) {
        return new TooltipDecorator(button, tooltip);
    }

    public static JButton withShadow(JButton button) {
        return new ShadowDecorator(button);
    }

    public static JButton withRoundedBorder(JButton button, int radius) {
        return new RoundedBorderDecorator(button, radius);
    }

    public static JButton createEnhancedActionButton(JButton button, String tooltip) {
        JButton decorated = new HoverEffectDecorator(button);
        decorated = new TooltipDecorator(decorated, tooltip);
        decorated = new RoundedBorderDecorator(decorated, 10);
        return decorated;
    }

    public static JButton createEnhancedNavigationButton(JButton button, String tooltip) {
        JButton decorated = new HoverEffectDecorator(button);
        decorated = new TooltipDecorator(decorated, tooltip);
        return decorated;
    }

    public static JButton decorate(JButton button, DecorationType... types) {
        JButton decorated = button;
        for (DecorationType type : types) {
            decorated = type.apply(decorated);
        }
        return decorated;
    }

    public enum DecorationType {
        HOVER {
            @Override
            JButton apply(JButton button) {
                return new HoverEffectDecorator(button);
            }
        },
        SHADOW {
            @Override
            JButton apply(JButton button) {
                return new ShadowDecorator(button);
            }
        },
        ROUNDED {
            @Override
            JButton apply(JButton button) {
                return new RoundedBorderDecorator(button, 10);
            }
        };

        abstract JButton apply(JButton button);
    }
}
