package dataheaven;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import javax.swing.border.Border;

public class GlowBorder implements Border {
    private Color glowColor = Color.GRAY;
    private int glowThickness = 1;
    private boolean isRound = true;
    public GlowBorder(Color c){
        this.glowColor = c;
    }
    public GlowBorder(Color c, int glowThickness, boolean isRound){
        this.glowColor = c;
        this.glowThickness = glowThickness;
        this.isRound = isRound;
    }
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(glowColor);
        g2.setStroke(new BasicStroke(glowThickness));
        if(this.isRound){
            g2.drawRoundRect(x, y, width - 1, height - 1, 10, 10);
        } else {
            g2.drawRect(x, y, width - 1, height - 1);
        }
    }
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(glowThickness, glowThickness, glowThickness, glowThickness);
    }
    @Override
    public boolean isBorderOpaque() {
        return true;
    }
}
