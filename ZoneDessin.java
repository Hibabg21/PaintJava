import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

/** La zone de dessin et la gestion de souris. */
public class ZoneDessin extends JPanel {

    /** ecouteur du  la zone prévient la fenêtre when quelque chose change. */
    public interface Ecouteur {
        void positionChangee(Point p);
        void imageModifiee();
        void formeTerminee(Forme f);                       // utilisé par le réseau
        void couleurPipette(Color c, boolean clicDroit);
    }

    public static final int[] ZOOMS = {25, 50, 75, 100, 150, 200, 300, 400, 600, 800};

    private BufferedImage image, avant;
    private Stack<BufferedImage> annulations = new Stack<BufferedImage>();
    private Stack<BufferedImage> retablissements = new Stack<BufferedImage>();
    private Ecouteur ecouteur;
    private String outil = "crayon";                       // tous les   formes
    private Color couleur1 = Color.BLACK, couleur2 = Color.WHITE;
    private int taille = 3, indiceZoom = 3;
    private float opacite = 1f;
    private boolean contour = true, remplissage = false;
    private Forme courante;                                // forme en cours de tracé
    private Rectangle selection;
    private Point debutSelection;
    private String msgTexte = "Texte :";

    public ZoneDessin(BufferedImage initiale) {
        image = initiale;
        setBackground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        MouseAdapter souris = new MouseAdapter() {
            public void mousePressed(MouseEvent e)  { appui(e); }
            public void mouseDragged(MouseEvent e)  { glisse(e); }
            public void mouseReleased(MouseEvent e) { relache(); }
            public void mouseMoved(MouseEvent e)    { if (ecouteur != null) ecouteur.positionChangee(versImage(e)); }
            public void mouseExited(MouseEvent e)   { if (ecouteur != null) ecouteur.positionChangee(null); }
        };
        addMouseListener(souris);
        addMouseMotionListener(souris);
        majTaille();
    }

    //  utilitaires
    public static BufferedImage blanche(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return img;
    }
    public static BufferedImage transparente(int w, int h) {
    return new BufferedImage(
        w,
        h,
        BufferedImage.TYPE_INT_ARGB
    );}



    public static BufferedImage copie(BufferedImage src) {
    BufferedImage c = new BufferedImage(
        src.getWidth(),
        src.getHeight(),
        BufferedImage.TYPE_INT_ARGB
    );

    Graphics2D g = c.createGraphics();
    g.setComposite(AlphaComposite.Src);
    g.drawImage(src, 0, 0, null);
    g.dispose();

    return c;
}

    private double zoom() { return ZOOMS[indiceZoom] / 100.0; }

    /** Position de la souris en pixels de l'image */
    private Point pointLibre(MouseEvent e) {
        return new Point((int) Math.floor(e.getX() / zoom()), (int) Math.floor(e.getY() / zoom()));
    }

    /** nb: null si la souris  hors de la feuille. */
    private Point versImage(MouseEvent e) {
        Point p = pointLibre(e);
        boolean dedans = p.x >= 0 && p.y >= 0 && p.x < image.getWidth() && p.y < image.getHeight();
        return dedans ? p : null;
    }

    private void majTaille() {
        setPreferredSize(new Dimension((int) Math.ceil(image.getWidth() * zoom()), (int) Math.ceil(image.getHeight() * zoom())));
        revalidate();
        repaint();
        if (ecouteur != null) ecouteur.imageModifiee();
    }

    private void sauvegarderPourAnnuler() {
        annulations.push(copie(image));
        if (annulations.size() > 30) annulations.remove(0);
        retablissements.clear();
    }

    private Forme nouvelleForme(boolean droit) {
        Forme f = new Forme(outil);
        f.couleur = droit ? couleur2 : couleur1;
        f.couleur2 = droit ? couleur1 : couleur2;
        f.taille = taille;
        f.opacite = opacite;
        f.contour = contour;
        f.remplissage = remplissage;
        return f;
    }

    private void terminer(Forme f) {
        if (ecouteur != null) ecouteur.formeTerminee(f);
        majTaille();
    }

    //  souris
    private void appui(MouseEvent e) {
        boolean droit = SwingUtilities.isRightMouseButton(e);
        Point p = pointLibre(e);

        if (outil.equals("zoom")) {
            setIndiceZoom(indiceZoom + (droit ? -1 : 1));
        } else if (outil.equals("pipette")) {
            Point q = versImage(e);
            if (q != null && ecouteur != null) ecouteur.couleurPipette(new Color(image.getRGB(q.x, q.y)), droit);
        } else if (outil.equals("selection")) {
            debutSelection = p;
            selection = null;
            majTaille();
        } else if (outil.equals("remplir") || outil.equals("texte")) {
            Forme f = nouvelleForme(droit);
            f.points.add(p);
            if (outil.equals("texte")) {
                f.texte = JOptionPane.showInputDialog(this, msgTexte);
                if (f.texte == null || f.texte.trim().isEmpty()) return;
            }
            sauvegarderPourAnnuler();
            f.appliquer(image);
            terminer(f);
        } else {                                           
            avant = copie(image);                          // pour l'aperçu en direct
            courante = nouvelleForme(droit);
            courante.points.add(p);
            redessiner();
        }
    }

    private void glisse(MouseEvent e) {
        Point p = pointLibre(e);
        if (ecouteur != null) ecouteur.positionChangee(versImage(e));
        if (outil.equals("selection") && debutSelection != null) {
            int x1 = Math.max(0, Math.min(debutSelection.x, p.x)), y1 = Math.max(0, Math.min(debutSelection.y, p.y));
            int x2 = Math.min(image.getWidth(), Math.max(debutSelection.x, p.x));
            int y2 = Math.min(image.getHeight(), Math.max(debutSelection.y, p.y));
            selection = new Rectangle(x1, y1, Math.max(0, x2 - x1), Math.max(0, y2 - y1));
            repaint();
            if (ecouteur != null) ecouteur.imageModifiee();
        } else if (courante != null) {
            if (courante.estGeometrique()) while (courante.points.size() > 1) courante.points.remove(1);
            courante.points.add(p);
            redessiner();
        }
    }

    private void relache() {
        if (outil.equals("selection")) {
            if (selection != null && (selection.width < 2 || selection.height < 2)) selection = null;
            debutSelection = null;
            repaint();
            if (ecouteur != null) ecouteur.imageModifiee();
        } else if (courante != null) {
            annulations.push(avant);     
            if (annulations.size() > 30) annulations.remove(0);                  // point pour annuler
            retablissements.clear();
            Forme f = courante;
            courante = null;
            avant = null;
            terminer(f);
        }
    }

    /**on repart de l'image "avant" et on redessine toute la forme. */
    private void redessiner() {
        Graphics2D g = image.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(avant, 0, 0, null);
        g.dispose();
        courante.appliquer(image);
        repaint();
    }

    // affichage
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        double z = zoom();
        int w = (int) Math.ceil(image.getWidth() * z), h = (int) Math.ceil(image.getHeight() * z);
        g2.drawImage(image, 0, 0, w, h, null);
        g2.setColor(new Color(0xC8C8C8));
        g2.drawRect(0, 0, w - 1, h - 1);
        if (selection != null) {
            g2.setColor(new Color(0x1F6FD0));
            g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{5f, 4f}, 0f));
            g2.drawRect((int) (selection.x * z), (int) (selection.y * z), (int) (selection.width * z), (int) (selection.height * z));
        }
    }

    // API pour la fenêtre
    public BufferedImage getImage() { return image; }
    public Rectangle getSelection() { return selection; }
    public int getIndiceZoom() { return indiceZoom; }
    public void setEcouteur(Ecouteur e) { ecouteur = e; }
    public void setMsgTexte(String s) { msgTexte = s; }
    public void setCouleur1(Color c) { couleur1 = c; }
    public void setCouleur2(Color c) { couleur2 = c; }
    public void setTaille(int t) { taille = t; }
    public void setOpacite(float o) {   opacite = Math.max(0f, Math.min(1f, o)); }
    public void setContour(boolean b) { contour = b; }
    public void setRemplissage(boolean b) { remplissage = b; }

    public void setOutil(String o) {
        outil = o;
        selection = null;
        repaint();
        setCursor(Cursor.getPredefinedCursor(o.equals("zoom") ? Cursor.HAND_CURSOR : Cursor.CROSSHAIR_CURSOR));
    }

    public void setImage(BufferedImage img) {
        sauvegarderPourAnnuler();
        image = img;
        selection = null;
        majTaille();
    }

    public void setIndiceZoom(int i) {
        indiceZoom = Math.max(0, Math.min(ZOOMS.length - 1, i));
        majTaille();
    }

    public void annuler() {
        if (annulations.isEmpty()) return;
        retablissements.push(copie(image));
        image = annulations.pop();
        selection = null;
        majTaille();
    }

    public void retablir() {
        if (retablissements.isEmpty()) return;
        annulations.push(copie(image));
        image = retablissements.pop();
        selection = null;
        majTaille();
    }

    public void nouveau() { setImage(blanche(image.getWidth(), image.getHeight())); }

    public void selectionnerTout() {
        selection = new Rectangle(0, 0, image.getWidth(), image.getHeight());
        repaint();
        if (ecouteur != null) ecouteur.imageModifiee();
    }

    public void supprimerSelection() {
    if (selection == null) return;
    sauvegarderPourAnnuler();
    Graphics2D g = image.createGraphics();
    g.setComposite(AlphaComposite.Clear);
    g.fillRect(selection.x, selection.y, selection.width, selection.height);
    g.dispose();
    selection = null;
    repaint();
}

    public void recadrer() {
        if (selection == null) return;
        setImage(copie(image.getSubimage(selection.x, selection.y, selection.width, selection.height)));
    }

    public void redimensionner(int w, int h) {
        BufferedImage n = blanche(w, h);
        Graphics2D g = n.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        setImage(n);
    }

    public void pivoter() {                                // 90 degree vers la droite
        BufferedImage n = new BufferedImage(image.getHeight(), image.getWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = n.createGraphics();
        g.translate(image.getHeight(), 0);
        g.rotate(Math.PI / 2);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        setImage(n);
    }

    public void retourner() {                              // miroir horizontal
        int w = image.getWidth(), h = image.getHeight();
        BufferedImage n = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = n.createGraphics();
        g.drawImage(image, w, 0, -w, h, null);
        g.dispose();
        setImage(n);
    }

    
    public void appliquerDistante(Forme f) {
        f.appliquer(image);
        if (avant != null) f.appliquer(avant);            
        repaint();
    }
}
