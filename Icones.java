import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

/** toutes les icônes sont dessinées en  2D  */
public class Icones {

    /** prepare une image transparente  on dessine aprs sur une grille 24 x 24. */
    private static Graphics2D prepare(BufferedImage img, int s) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.scale(s / 24.0, s / 24.0);
        g.setColor(new Color(0x2B2B2B));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        return g;
    }
    private static void verifierTaille(int s) {
    if (s <= 0) {
        throw new IllegalArgumentException(
            "La taille doit être positive."
        );
    }
    }

    public static ImageIcon forme(String type, int s) {
        if (type == null) {
            throw new IllegalArgumentException("Le type ne doit pas être null.");
        }
        verifierTaille(s);
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = prepare(img, s);
        if ("ligne".equals(type)) g.draw(new Line2D.Double(3, 21, 21, 3));
        else g.draw(Forme.geometrie(type, 3, 3, 18, 18));
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon roueCouleurs(int s) {
        verifierTaille(s);
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = prepare(img, s);
        for (int a = 0; a < 360; a += 4) {
            g.setColor(Color.getHSBColor(a / 360f, 0.85f, 1f));
            g.fill(new Arc2D.Double(2, 2, 20, 20, a, 5, Arc2D.PIE));
        }
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(8.5, 8.5, 7, 7));
        g.dispose();
        return new ImageIcon(img);
    }

    public static ImageIcon get(String nom, int s) {
        verifierTaille(s);
        if (nom == null) {
        throw new IllegalArgumentException("Nom invalide");}
        BufferedImage img = new BufferedImage(s, s, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = prepare(img, s);
        Path2D.Double p = new Path2D.Double();

        if ("select".equals(nom)) {
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{3f, 2.5f}, 0f));
            g.draw(new Rectangle2D.Double(3.5, 3.5, 17, 17));
        } else if (nom.equals("crop")) {
            p.moveTo(7, 2); p.lineTo(7, 17); p.lineTo(22, 17);
            p.moveTo(2, 7); p.lineTo(17, 7); p.lineTo(17, 22);
            g.draw(p);
        } else if (nom.equals("resize")) {
            g.draw(new Rectangle2D.Double(3, 3, 18, 18));
            g.draw(new Rectangle2D.Double(3, 11, 10, 10));
            g.draw(new Line2D.Double(10, 14, 19, 5));
        } else if (nom.equals("rotate")) {
            g.draw(new Arc2D.Double(4, 4, 16, 16, 60, 270, Arc2D.OPEN));
            p.moveTo(16, 3); p.lineTo(21, 7); p.lineTo(14.5, 8.5); p.closePath();
            g.fill(p);
        } else if (nom.equals("flip")) {
            g.draw(new Line2D.Double(12, 2, 12, 22));
            p.moveTo(9, 6); p.lineTo(3, 18); p.lineTo(9, 18); p.closePath();
            p.moveTo(15, 6); p.lineTo(21, 18); p.lineTo(15, 18); p.closePath();
            g.draw(p);
        } else if (nom.equals("pencil")) {
            p.moveTo(4, 20); p.lineTo(6, 14); p.lineTo(17, 3); p.lineTo(21, 7); p.lineTo(10, 18); p.closePath();
            g.draw(p);
            g.draw(new Line2D.Double(14, 6, 18, 10));
        } else if (nom.equals("fill")) {
            p.moveTo(3, 13); p.lineTo(10, 6); p.lineTo(17, 13); p.lineTo(10, 20); p.closePath();
            g.draw(p);
            g.fill(new Ellipse2D.Double(18, 15, 4, 5));
        } else if (nom.equals("text")) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
            g.drawString("A", 5, 20);
        } else if (nom.equals("eraser")) {
            g.rotate(-Math.PI / 4, 12, 12);
            g.draw(new RoundRectangle2D.Double(3, 8, 18, 9, 3, 3));
            g.draw(new Line2D.Double(10, 8, 10, 17));
        } else if (nom.equals("picker")) {
            g.draw(new Line2D.Double(4, 20, 13, 11));
            g.draw(new Ellipse2D.Double(13, 3, 8, 8));
        } else if (nom.equals("zoom")) {
            g.draw(new Ellipse2D.Double(3.5, 3.5, 13, 13));
            g.draw(new Line2D.Double(15, 15, 21, 21));
            g.draw(new Line2D.Double(7, 10, 13, 10));
            g.draw(new Line2D.Double(10, 7, 10, 13));
        } else if (nom.equals("brush")) {
            g.draw(new RoundRectangle2D.Double(8, 2.5, 8, 9, 3, 3));
            g.draw(new Rectangle2D.Double(7, 11.5, 10, 4));
            for (int i = 0; i < 3; i++) g.draw(new Line2D.Double(9 + i * 3, 16, 9 + i * 3, 21));
        } else if (nom.equals("taille")) {
            for (int i = 0; i < 3; i++) {
                g.setStroke(new BasicStroke(1f + i * 1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.draw(new Line2D.Double(3, 6 + i * 6, 21, 6 + i * 6));
            }
        } else if (nom.equals("opacite")) {
            p.moveTo(12, 3); p.curveTo(12, 3, 5, 11, 5, 15); p.curveTo(5, 19, 8, 21, 12, 21);
            p.curveTo(16, 21, 19, 19, 19, 15); p.curveTo(19, 11, 12, 3, 12, 3); p.closePath();
            g.fill(p);
        } else if (nom.equals("save")) {
            g.draw(new RoundRectangle2D.Double(4, 4, 16, 16, 3, 3));
            g.draw(new Rectangle2D.Double(8, 4, 8, 5));
            g.draw(new Rectangle2D.Double(7, 13, 10, 7));
        } else if (nom.equals("share")) {
            g.draw(new Rectangle2D.Double(5, 11, 14, 10));
            g.draw(new Line2D.Double(12, 15, 12, 3));
            g.draw(new Line2D.Double(8, 7, 12, 3));
            g.draw(new Line2D.Double(16, 7, 12, 3));
        } else if (nom.equals("undo") || nom.equals("redo")) {
            if (nom.equals("redo")) { g.translate(24, 0); g.scale(-1, 1); }   //  undo en miroir
            p.moveTo(6, 10); p.lineTo(15, 10); p.curveTo(21, 10, 21, 19, 14, 19);
            p.moveTo(10, 6); p.lineTo(6, 10); p.lineTo(10, 14);
            g.draw(p);
        }
        g.dispose();
        return new ImageIcon(img);
    }
}
