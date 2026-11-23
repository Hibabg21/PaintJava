
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/** Couleur principale (clic gauche) et secondaire (clic droit). */
public class ApercuCouleurs extends JComponent {

    private Color primaire = Color.BLACK;
    private Color secondaire = Color.WHITE;

    public void setPrimaire(Color c) {
        primaire = c;
        repaint();
    }

    public void setSecondaire(Color c) {
        secondaire = c;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(54, 82);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // Couleur secondaire
        g2.setColor(secondaire);
        g2.fill(new Ellipse2D.Double(10, 48, 32, 32));

        g2.setColor(new Color(0xA8A8A8));
        g2.draw(new Ellipse2D.Double(10, 48, 32, 32));

        // Couleur principale
        g2.setColor(primaire);
        g2.fill(new Ellipse2D.Double(4, 6, 40, 40));

        // Cadre bleu autour de la couleur principale
        g2.setColor(new Color(0x2B7CD3));
        g2.setStroke(new BasicStroke(2f));
        g2.draw(new Ellipse2D.Double(1.5, 3.5, 45, 45));

        g2.dispose();
    }
}
