
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/** Bouton plat du ruban : cadre bleu quand l'outil est actif. */
public class BoutonRuban extends JButton {

    private boolean actif, survol;

    public BoutonRuban(Icon icone, String info, int marge) {
        super(icone);

        setToolTipText(info);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);
        setMargin(new Insets(marge, marge, marge, marge));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
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

    public void setActif(boolean a) {
        actif = a;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        if (actif) {
            // Fond bleu clair et cadre bleu quand l'outil est actif
            g2.setColor(new Color(0xE3F0FC));
            g2.fillRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1, 8, 8
            );

            g2.setColor(new Color(0x2B7CD3));
            g2.drawRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1, 8, 8
            );

        } else if (survol) {
            // Fond gris quand la souris survole le bouton
            g2.setColor(new Color(0xECECEC));
            g2.fillRoundRect(
                0, 0, getWidth() - 1, getHeight() - 1, 8, 8
            );
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
