
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

/** Une pastille de la palette de couleurs. */
public class BoutonCouleur extends JComponent {

    /** Écouteur qui prévient la fenêtre lorsqu'une couleur est choisie. */
    public interface Ecouteur {
        void couleurChoisie(Color c, boolean clicDroit);
    }

    private Color couleur; // null = emplacement libre
    private boolean survol;

    public BoutonCouleur(Color c, final Ecouteur ecouteur) {
        couleur = c;

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (couleur != null && ecouteur != null) {
                    ecouteur.couleurChoisie(
                        couleur,
                        SwingUtilities.isRightMouseButton(e)
                    );
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                survol = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                survol = false;
                repaint();
            }
        });
    }

    public Color getCouleur() {
        return couleur;
    }

    public void setCouleur(Color c) {
        couleur = c;
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(24, 24);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        Ellipse2D rond = new Ellipse2D.Double(
            2, 2, getWidth() - 5, getHeight() - 5
        );

        if (couleur != null) {
            g2.setColor(couleur);
            g2.fill(rond);
        }

        g2.setColor(
            survol ? new Color(0x2B7CD3) : new Color(0xB4B4B4)
        );
        g2.draw(rond);

        g2.dispose();
    }
}
