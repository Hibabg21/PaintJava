import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * le commande de dessin est tout ce que l'utilisateur dessine est décrit par un objet Forme.
 * il est Serializable : on peut donc l'envoyer par socket (dessin collaboratif).
 */
public class Forme implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String[] FORMES = {
        "ligne", "ovale", "rectangle", "rectArrondi", "triangle", "triangleRect", "losange",
        "pentagone", "hexagone", "flecheDroite", "flecheGauche", "flecheHaut", "flecheBas",
        "etoile4", "etoile5", "etoile6", "croix"};

    public String type;                      // les   formes  de dessinn
    public Color couleur = Color.BLACK, couleur2 = Color.WHITE;
    public int taille = 3;
    public float opacite = 1f;
    public boolean contour = true, remplissage = false;
    public String texte;
    public List<Point> points = new ArrayList<Point>();
    public long graine = System.nanoTime();  

    public Forme(String type) { this.type = type; }

    public boolean estGeometrique() { return Arrays.asList(FORMES).contains(type); }

    // dessin
    public void appliquer(BufferedImage img) {
        if (points.isEmpty()) return;
        if (type.equals("remplir")) { remplir(img); return; }

        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            type.equals("crayon") ? RenderingHints.VALUE_ANTIALIAS_OFF : RenderingHints.VALUE_ANTIALIAS_ON);
        float alpha = type.equals("marqueur") ? opacite * 0.5f : opacite;
        alpha = Math.max(0f, Math.min(1f, alpha));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        Point a = points.get(0), b = points.get(points.size() - 1);

        if (type.equals("crayon"))        trait(g, couleur, Math.max(1, taille / 2), BasicStroke.CAP_SQUARE);
        else if (type.equals("pinceau"))  trait(g, couleur, taille, BasicStroke.CAP_ROUND);
        else if (type.equals("marqueur")) trait(g, couleur, taille * 2, BasicStroke.CAP_SQUARE);
        else if (type.equals("gomme")) {
            g.setComposite(AlphaComposite.Clear);
            trait(g, Color.BLACK, taille * 3, BasicStroke.CAP_SQUARE);
        } else if (type.equals("aerographe")) {
            g.setColor(couleur);
            Random r = new Random(graine);
            for (Point q : points) {
                for (int k = 0; k < taille * 2 + 8; k++) {
                    double ang = r.nextDouble() * 2 * Math.PI;
                    double d = Math.sqrt(r.nextDouble()) * (taille * 2 + 4);
                    g.fillRect((int) (q.x + Math.cos(ang) * d), (int) (q.y + Math.sin(ang) * d), 1, 1);
                }
            }
        } else if (type.equals("texte")  && texte != null) {
            
            g.setColor(couleur);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(14, taille * 4)));
            int y = a.y + g.getFontMetrics().getAscent();
            for (String ligne : texte.split("\n")) { g.drawString(ligne, a.x, y); y += g.getFontMetrics().getHeight(); }
        } else if (estGeometrique()) {
            g.setStroke(new BasicStroke(taille, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            if (type.equals("ligne")) {
                g.setColor(couleur);
                g.draw(new Line2D.Double(a, b));
            } else if (a.x != b.x || a.y != b.y) {
                Shape s = geometrie(type, Math.min(a.x, b.x), Math.min(a.y, b.y), Math.abs(a.x - b.x), Math.abs(a.y - b.y));
                if (remplissage) { g.setColor(couleur2); g.fill(s); }
                if (contour)     { g.setColor(couleur);  g.draw(s); }
            }
        }
        g.dispose();
    }

    /**  on relie tous les points par un Path2D. */
    private void trait(Graphics2D g, Color c, int largeur, int cap) {
        g.setColor(c);
        g.setStroke(new BasicStroke(largeur, cap, BasicStroke.JOIN_ROUND));
        Point p0 = points.get(0);
        if (points.size() < 2) {                                   // un petit carré
            g.fillRect(p0.x - largeur / 2, p0.y - largeur / 2, largeur, largeur);
            return;
        }
        Path2D.Double chemin = new Path2D.Double();
        chemin.moveTo(p0.x, p0.y);
        for (Point p : points) chemin.lineTo(p.x, p.y);
        g.draw(chemin);
    }

    /**  remplissage par diffusion */
    private void remplir(BufferedImage img) {
        Point p = points.get(0);
        int w = img.getWidth(), h = img.getHeight();
        if (p.x < 0 || p.y < 0 || p.x >= w || p.y >= h) return;
        int[] px = img.getRGB(0, 0, w, h, null, 0, w);
        int cible = px[p.y * w + p.x], neuf = couleur.getRGB();
        if (cible == neuf) return;
        boolean[] vu = new boolean[w * h];
        int[] pile = new int[w * h];
        int sp = 0;
        pile[sp++] = p.y * w + p.x;
        vu[p.y * w + p.x] = true;
        while (sp > 0) {
            int i = pile[--sp];
            px[i] = neuf;
            int[] voisins = {i - 1, i + 1, i - w, i + w};
            boolean[] existe = {i % w > 0, i % w < w - 1, i >= w, i < w * (h - 1)};
            for (int k = 0; k < 4; k++) {
                int v = voisins[k];
                if (existe[k] && !vu[v] && proche(px[v], cible)) { vu[v] = true; pile[sp++] = v; }
            }
        }
        img.setRGB(0, 0, w, h, px, 0, w);
    }

    private boolean proche(int a, int b) {
    int da = Math.abs(((a >>> 24) & 255) - ((b >>> 24) & 255));
    int dr = Math.abs(((a >>> 16) & 255) - ((b >>> 16) & 255));
    int dg = Math.abs(((a >>> 8) & 255) - ((b >>> 8) & 255));
    int db = Math.abs((a & 255) - (b & 255));

    return da + dr + dg + db <= 60;}


    //  géométrie
    public static Shape geometrie(String t, double x, double y, double w, double h) {
        if (t.equals("ovale"))        return new Ellipse2D.Double(x, y, w, h);
        if (t.equals("rectangle"))    return new Rectangle2D.Double(x, y, w, h);
        if (t.equals("rectArrondi"))  return new RoundRectangle2D.Double(x, y, w, h, Math.min(w, h) / 3, Math.min(w, h) / 3);
        if (t.equals("triangle"))     return poly(x, y, w, h, .5, 0, 1, 1, 0, 1);
        if (t.equals("triangleRect")) return poly(x, y, w, h, 0, 0, 1, 1, 0, 1);
        if (t.equals("losange"))      return poly(x, y, w, h, .5, 0, 1, .5, .5, 1, 0, .5);
        if (t.equals("pentagone"))    return poly(x, y, w, h, .5, 0, 1, .38, .81, 1, .19, 1, 0, .38);
        if (t.equals("hexagone"))     return poly(x, y, w, h, .25, 0, .75, 0, 1, .5, .75, 1, .25, 1, 0, .5);
        if (t.equals("flecheDroite")) return poly(x, y, w, h, 0, .3, .6, .3, .6, 0, 1, .5, .6, 1, .6, .7, 0, .7);
        if (t.equals("flecheGauche")) return poly(x, y, w, h, 1, .3, .4, .3, .4, 0, 0, .5, .4, 1, .4, .7, 1, .7);
        if (t.equals("flecheHaut"))   return poly(x, y, w, h, .3, 1, .3, .4, 0, .4, .5, 0, 1, .4, .7, .4, .7, 1);
        if (t.equals("flecheBas"))    return poly(x, y, w, h, .3, 0, .3, .6, 0, .6, .5, 1, 1, .6, .7, .6, .7, 0);
        if (t.equals("etoile4"))      return etoile(x, y, w, h, 4, .4);
        if (t.equals("etoile5"))      return etoile(x, y, w, h, 5, .4);
        if (t.equals("etoile6"))      return etoile(x, y, w, h, 6, .55);
        if (t.equals("croix"))        return poly(x, y, w, h, .35, 0, .65, 0, .65, .35, 1, .35, 1, .65, .65, .65,
                                                  .65, 1, .35, 1, .35, .65, 0, .65, 0, .35, .35, .35);
        return new Rectangle2D.Double(x, y, w, h);
    }

    /** Polygone */
    private static Path2D poly(double x, double y, double w, double h, double... p) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0; i < p.length; i += 2) {
            if (i == 0) path.moveTo(x + p[i] * w, y + p[i + 1] * h);
            else        path.lineTo(x + p[i] * w, y + p[i + 1] * h);
        }
        path.closePath();
        return path;
    }

    private static Path2D etoile(double x, double y, double w, double h, int branches, double interieur) {
        Path2D.Double path = new Path2D.Double();
        for (int i = 0; i < branches * 2; i++) {
            double ang = -Math.PI / 2 + i * Math.PI / branches;
            double k = (i % 2 == 0) ? 1 : interieur;
            double px = x + w / 2 + Math.cos(ang) * w / 2 * k, py = y + h / 2 + Math.sin(ang) * h / 2 * k;
            if (i == 0) path.moveTo(px, py); else path.lineTo(px, py);
        }
        path.closePath();
        return path;
    }
}
