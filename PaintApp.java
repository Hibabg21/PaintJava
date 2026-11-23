import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** Fenêtre principale : menus, rubandu(Sélection / Image / Outils / Pinceaux / Formes / Couleurs), zone de dessin. */
public class PaintApp extends JFrame implements ZoneDessin.Ecouteur, BoutonCouleur.Ecouteur {
    private static final Color FOND = new Color(0xF3F3F3), RUBAN = new Color(0xFAFAFA), TRAIT = new Color(0xE0E0E0);
    private static final int[][] PALETTE = {
        {0x000000, 0x7F7F7F, 0x880015, 0xED1C24, 0xFF7F27, 0xFFF200, 0x22B14C, 0x00A2E8, 0x3F48CC, 0xA349A4},
        {0xFFFFFF, 0xC3C3C3, 0xB97A57, 0xFFAEC9, 0xFFC90E, 0xEFE4B0, 0xB5E61D, 0x99D9EA, 0x7092BE, 0xC8BFE7}};

    private ResourceBundle rb;
    private ZoneDessin zone;
    private ApercuCouleurs apercu = new ApercuCouleurs();
    private List<BoutonRuban> outils = new ArrayList<BoutonRuban>();
    private BoutonCouleur[] perso = new BoutonCouleur[10];
    private BoutonRuban btnCrayon;
   private volatile ClientReseau client;
    private Thread autosave;
    private JLabel lblPosition = new JLabel(" "), lblSelection = new JLabel(" "), lblTaille = new JLabel(" ");
    private JComboBox<String> comboZoom;
    private JSlider sliderZoom;
    private boolean ajustementZoom;

    public PaintApp(Locale locale, BufferedImage initiale) {
        rb = ResourceBundle.getBundle("Messages", locale);
        setTitle(t("app.titre"));
        setSize(1366, 860);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(FOND);

        zone = new ZoneDessin(initiale != null ? initiale : ZoneDessin.blanche(1152, 648));
        zone.setMsgTexte(t("msg.texte"));
        zone.setEcouteur(this);

        setJMenuBar(creerMenu());
        add(creerRuban(), BorderLayout.NORTH);
        add(creerCentre(), BorderLayout.CENTER);

        add(creerStatut(), BorderLayout.SOUTH);
        imageModifiee();
        activer(btnCrayon);

        // autosave a chaque 30s
        autosave = new Thread(() -> boucleAutosave(), "auto-sauvegarde");
        autosave.setDaemon(true);
        autosave.start();
    }

    private String t(String cle) {
        try { return rb.getString(cle); } catch (MissingResourceException e) { return cle; }
    }

    
    public void positionChangee(Point p) { lblPosition.setText(p == null ? " " : p.x + ", " + p.y + " px"); }
    public void couleurChoisie(Color c, boolean clicDroit) { choisirCouleur(c, clicDroit); }
    public void couleurPipette(Color c, boolean clicDroit) { choisirCouleur(c, clicDroit); }

    public void formeTerminee(Forme f) {                  // si on eest en connect
        try { if (client != null) client.envoyer(f); } catch (IOException ex) { ex.printStackTrace(); }
    }

    public void imageModifiee() {
        lblTaille.setText("\u25A2 " + zone.getImage().getWidth() + " \u00D7 " + zone.getImage().getHeight() + "px");
        Rectangle s = zone.getSelection();
        lblSelection.setText(s == null ? " " : "\u2B1A " + s.width + " \u00D7 " + s.height + "px");
        ajustementZoom = true;                            // ignore boucle evenement
        comboZoom.setSelectedIndex(zone.getIndiceZoom());
        sliderZoom.setValue(zone.getIndiceZoom());
        ajustementZoom = false;
    }

    private void choisirCouleur(Color c, boolean droit) {
        if (droit) { zone.setCouleur2(c); apercu.setSecondaire(c); }
        else       { zone.setCouleur1(c); apercu.setPrimaire(c); }
    }

    // menus
    private JMenuBar creerMenu() {
        JMenuBar barre = new JMenuBar();
        barre.setBackground(Color.WHITE);
        barre.setBorder(new MatteBorder(0, 0, 1, 0, TRAIT));

        JMenu fichier = new JMenu(t("menu.fichier"));
        fichier.add(item("f.nouveau", KeyEvent.VK_N, e -> zone.nouveau()));
        fichier.add(item("f.ouvrir", KeyEvent.VK_O, e -> ouvrirProjet()));
        fichier.add(item("f.ouvrirImage", 0, e -> ouvrirImage()));
        fichier.add(item("f.enregistrer", KeyEvent.VK_S, e -> enregistrerProjet()));
        fichier.add(item("f.exporter", KeyEvent.VK_E, e -> exporterImage()));
        fichier.addSeparator();
        fichier.add(item("f.quitter", 0, e -> System.exit(0)));

        JMenu edition = new JMenu(t("menu.edition"));
        edition.add(item("e.annuler", KeyEvent.VK_Z, e -> zone.annuler()));
        edition.add(item("e.retablir", KeyEvent.VK_Y, e -> zone.retablir()));
        edition.addSeparator();
        edition.add(item("e.toutSelectionner", KeyEvent.VK_A, e -> zone.selectionnerTout()));
        edition.add(item("e.supprimer", 0, e -> zone.supprimerSelection()));
        edition.add(item("e.recadrer", 0, e -> zone.recadrer()));

        JMenu affichage = new JMenu(t("menu.affichage"));
        affichage.add(item("v.zoomPlus", KeyEvent.VK_PLUS, e -> zone.setIndiceZoom(zone.getIndiceZoom() + 1)));
        affichage.add(item("v.zoomMoins", KeyEvent.VK_MINUS, e -> zone.setIndiceZoom(zone.getIndiceZoom() - 1)));
        affichage.add(item("v.zoom100", KeyEvent.VK_0, e -> zone.setIndiceZoom(3)));

        JMenu reseau = new JMenu(t("menu.reseau"));
        reseau.add(item("r.serveur", 0, e -> demarrerServeur()));
        reseau.add(item("r.connecter", 0, e -> seConnecter()));
        reseau.add(item("r.deconnecter", 0, e -> seDeconnecter()));

        JMenu base = new JMenu(t("menu.base"));
        base.add(item("d.enregistrer", 0, e -> enregistrerBase()));
        base.add(item("d.ouvrir", 0, e -> ouvrirBase()));

        JMenu langue = new JMenu(t("menu.langue"));
        JMenuItem fr = new JMenuItem("Français"), en = new JMenuItem("English");
        fr.addActionListener(e -> changerLangue(Locale.FRENCH));
        en.addActionListener(e -> changerLangue(Locale.ENGLISH));
        langue.add(fr);
        langue.add(en);

        for (JMenu m : new JMenu[]{fichier, edition, affichage, reseau, base, langue}) barre.add(m);
        barre.add(Box.createHorizontalStrut(14));
        barre.add(bouton("save", 18, "f.enregistrer", 4, e -> enregistrerProjet()));
        barre.add(bouton("share", 18, "f.exporter", 4, e -> exporterImage()));
        barre.add(Box.createHorizontalStrut(10));
        barre.add(bouton("undo", 18, "e.annuler", 4, e -> zone.annuler()));
        barre.add(bouton("redo", 18, "e.retablir", 4, e -> zone.retablir()));
        return barre;
    }

    private JMenuItem item(String cle, int touche, ActionListener action) {
        JMenuItem it = new JMenuItem(t(cle));
        if (touche != 0) it.setAccelerator(KeyStroke.getKeyStroke(touche, InputEvent.CTRL_DOWN_MASK));
        it.addActionListener(action);
        return it;
    }

    // ruban
    private JPanel creerRuban() {
        JPanel r = new JPanel();
        r.setLayout(new BoxLayout(r, BoxLayout.X_AXIS));
        r.setBackground(RUBAN);
        r.setBorder(new MatteBorder(0, 0, 1, 0, TRAIT));

        // selection, Image, Outils
        r.add(groupe("g.selection", panneau(new GridBagLayout(), outil("select", 34, "g.selection", "selection", 8))));
        r.add(groupe("g.image", panneau(new GridLayout(2, 2, 4, 4),
            bouton("crop", 22, "img.recadrer", 5, e -> zone.recadrer()),
            bouton("resize", 22, "img.redim", 5, e -> redimensionner()),
            bouton("rotate", 22, "img.pivoter", 5, e -> zone.pivoter()),
            bouton("flip", 22, "img.retourner", 5, e -> zone.retourner()))));
        btnCrayon = outil("pencil", 22, "o.crayon", "crayon", 5);
        r.add(groupe("g.outils", panneau(new GridLayout(2, 3, 4, 4), btnCrayon,
            outil("fill", 22, "o.remplir", "remplir", 5), outil("text", 22, "o.texte", "texte", 5),
            outil("eraser", 22, "o.gomme", "gomme", 5), outil("picker", 22, "o.pipette", "pipette", 5),
            outil("zoom", 22, "o.zoom", "zoom", 5))));

        // Pinceaux 
        final BoutonRuban pinceaux = new BoutonRuban(Icones.get("brush", 34), t("g.pinceaux"), 8);
        outils.add(pinceaux);
        final JPopupMenu menuPinceaux = new JPopupMenu();
        for (final String ty : new String[]{"pinceau", "marqueur", "aerographe"}) {
            JMenuItem it = new JMenuItem(t("o." + ty));
            it.addActionListener(e -> { zone.setOutil(ty); activer(pinceaux); });
            menuPinceaux.add(it);
        }
        pinceaux.addActionListener(e -> menuPinceaux.show(pinceaux, 0, pinceaux.getHeight()));
        r.add(groupe("g.pinceaux", panneau(new GridBagLayout(), pinceaux)));

        // Formes
        JPanel grille = panneau(new GridLayout(3, 6, 2, 2));
        for (final String f : Forme.FORMES) {
            final BoutonRuban b = new BoutonRuban(Icones.forme(f, 20), f, 2);
            outils.add(b);
            b.addActionListener(e -> { zone.setOutil(f); activer(b); });
            grille.add(b);
        }
        final JCheckBox cContour = new JCheckBox(t("forme.contour"), true), cRempl = new JCheckBox(t("forme.remplissage"));
        cContour.setOpaque(false);
        cRempl.setOpaque(false);
        cContour.addActionListener(e -> zone.setContour(cContour.isSelected()));
        cRempl.addActionListener(e -> zone.setRemplissage(cRempl.isSelected()));
        JPanel pFormes = panneau(new BorderLayout(8, 0));
        pFormes.add(grille, BorderLayout.CENTER);
        pFormes.add(panneau(new GridLayout(2, 1), cContour, cRempl), BorderLayout.EAST);
        r.add(groupe("g.formes", pFormes));

        // Couleurs : 2 lignes de couleurs + 1 ligne de couleurs personnalisées 
        JPanel palette = panneau(new GridLayout(3, 10, 3, 3));
        for (int[] ligne : PALETTE)
            for (int rgb : ligne) palette.add(new BoutonCouleur(new Color(rgb), this));
        for (int i = 0; i < perso.length; i++) palette.add(perso[i] = new BoutonCouleur(null, this));
        BoutonRuban roue = new BoutonRuban(Icones.roueCouleurs(34), t("s.perso"), 4);
        roue.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, t("s.perso"), Color.BLACK);
            if (c == null) return;
            for (BoutonCouleur b : perso) if (b.getCouleur() == null) { b.setCouleur(c); break; }
            choisirCouleur(c, false);
        });
        JPanel pCoul = panneau(new BorderLayout(8, 0));
        pCoul.add(apercu, BorderLayout.WEST);
        pCoul.add(palette, BorderLayout.CENTER);
        pCoul.add(panneau(new GridBagLayout(), roue), BorderLayout.EAST);
        r.add(groupe("g.couleurs", pCoul));

        r.add(Box.createHorizontalGlue());
        return r;
    }

    
    private JPanel panneau(LayoutManager layout, Component... composants) {
        JPanel p = new JPanel(layout);
        p.setOpaque(false);
        for (Component c : composants) p.add(c);
        return p;
    }

    
    private JPanel groupe(String cle, JComponent contenu) {
        JPanel p = panneau(new BorderLayout(0, 3));
        p.setBorder(new EmptyBorder(8, 12, 3, 12));
        p.add(contenu, BorderLayout.CENTER);
        JLabel titre = new JLabel(t(cle), SwingConstants.CENTER);
        titre.setFont(titre.getFont().deriveFont(Font.PLAIN, 11.5f));
        titre.setForeground(new Color(0x555555));
        p.add(titre, BorderLayout.SOUTH);
        JPanel w = panneau(new BorderLayout(), p);
        w.setBorder(new MatteBorder(0, 0, 0, 1, TRAIT));
        return w;
    }

    private BoutonRuban bouton(String icone, int taille, String cleInfo, int marge, ActionListener a) {
        BoutonRuban b = new BoutonRuban(Icones.get(icone, taille), t(cleInfo), marge);
        b.addActionListener(a);
        return b;
    }

    /**  un seul outil actif à la fois qui en acdre bleu. */
    private BoutonRuban outil(String icone, int taille, String cleInfo, final String nomOutil, int marge) {
        final BoutonRuban b = bouton(icone, taille, cleInfo, marge, null);
        outils.add(b);
        b.addActionListener(e -> { zone.setOutil(nomOutil); activer(b); });
        return b;
    }

    private void activer(BoutonRuban b) {
        for (BoutonRuban o : outils) o.setActif(o == b);
    }

    // zone du centre
    private JPanel creerCentre() {
        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(FOND);

        final JSlider taille = new JSlider(SwingConstants.VERTICAL, 1, 50, 3);
        taille.addChangeListener(e -> zone.setTaille(taille.getValue()));
        final JSlider opacite = new JSlider(SwingConstants.VERTICAL, 1, 100, 100);
        opacite.addChangeListener(e -> zone.setOpacite(opacite.getValue() / 100f));
        JPanel glissieres = panneau(new GridLayout(1, 2, 8, 0), carte("taille", t("s.taille"), taille),
                                    carte("opacite", t("s.opacite"), opacite));
        glissieres.setBorder(new EmptyBorder(18, 16, 40, 8));
        centre.add(glissieres, BorderLayout.WEST);

        JPanel feuille = new JPanel(new GridBagLayout());          
        feuille.setBackground(FOND);
        feuille.setBorder(new EmptyBorder(24, 24, 24, 24));
        feuille.add(zone);
        JScrollPane defile = new JScrollPane(feuille);
        defile.setBorder(null);
        defile.getViewport().setBackground(FOND);
        centre.add(defile, BorderLayout.CENTER);
        return centre;
    }

    
    private JPanel carte(String icone, String info, JSlider s) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(new CompoundBorder(new LineBorder(TRAIT, 1, true), new EmptyBorder(10, 4, 10, 4)));
        s.setOpaque(false);
        s.setToolTipText(info);
        p.add(new JLabel(Icones.get(icone, 22), SwingConstants.CENTER), BorderLayout.NORTH);
        p.add(s, BorderLayout.CENTER);
        return p;
    }

    
    

    // barre de menus
    private JPanel creerStatut() {
        JPanel b = new JPanel(new BorderLayout());
        b.setBackground(RUBAN);
        b.setBorder(new MatteBorder(1, 0, 0, 0, TRAIT));
        JPanel gauche = panneau(new FlowLayout(FlowLayout.LEFT, 14, 2), lblPosition, lblSelection, lblTaille);
        for (Component l : gauche.getComponents()) l.setPreferredSize(new Dimension(150, 26));
        b.add(gauche, BorderLayout.WEST);

        String[] valeurs = new String[ZoneDessin.ZOOMS.length];
        for (int i = 0; i < valeurs.length; i++) valeurs[i] = ZoneDessin.ZOOMS[i] + "%";
        comboZoom = new JComboBox<String>(valeurs);
        comboZoom.addActionListener(e -> { if (!ajustementZoom) zone.setIndiceZoom(comboZoom.getSelectedIndex()); });
        sliderZoom = new JSlider(0, ZoneDessin.ZOOMS.length - 1, zone.getIndiceZoom());
        sliderZoom.setOpaque(false);
        sliderZoom.setPreferredSize(new Dimension(150, 22));
        sliderZoom.addChangeListener(e -> { if (!ajustementZoom) zone.setIndiceZoom(sliderZoom.getValue()); });
        JButton moins = new JButton("–"), plus = new JButton("+");
        moins.addActionListener(e -> zone.setIndiceZoom(zone.getIndiceZoom() - 1));
        plus.addActionListener(e -> zone.setIndiceZoom(zone.getIndiceZoom() + 1));
        for (JButton j : new JButton[]{moins, plus}) {
            j.setContentAreaFilled(false);
            j.setBorder(new EmptyBorder(2, 6, 2, 6));
        }
        b.add(panneau(new FlowLayout(FlowLayout.RIGHT, 6, 1), comboZoom, moins, sliderZoom, plus), BorderLayout.EAST);
        return b;
    }

    // fichiers
    private File choisir(boolean ouvrir, String description, String... extensions) {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(description, extensions));
        int rep = ouvrir ? fc.showOpenDialog(this) : fc.showSaveDialog(this);
        return rep == JFileChooser.APPROVE_OPTION ? fc.getSelectedFile() : null;
    }

    private void ouvrirProjet() {
        File f = choisir(true, "Projet Paint (*.paint)", "paint");
        if (f == null) return;
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(f)))) {
            zone.setImage(((ProjetPaint) in.readObject()).image());          // deserialisation
        } catch (Exception ex) { erreur(ex); }
    }

    private void enregistrerProjet() {
        File f = choisir(false, "Projet Paint (*.paint)", "paint");
        if (f == null) return;
        if (!f.getName().endsWith(".paint")) f = new File(f.getPath() + ".paint");
        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(f)))) {
            out.writeObject(new ProjetPaint(zone.getImage()));               // sererialisation
        } catch (Exception ex) { erreur(ex); }
    }

    private void ouvrirImage() {
        File f = choisir(true, "Images", "png", "jpg", "jpeg", "gif", "bmp");
        if (f == null) return;
        try {
            BufferedImage img = ImageIO.read(f);
            if (img == null) throw new IOException("Format non reconnu");
            zone.setImage(ZoneDessin.copie(img));
        } catch (IOException ex) { erreur(ex); }
    }

    private void exporterImage() {
    File f = choisir(false, "PNG / JPG", "png", "jpg");
    if (f == null) return;

    String nom = f.getName().toLowerCase();
    String format = nom.matches(".*\\.jpe?g") ? "jpg" : "png";

    if (!nom.matches(".*\\.(png|jpe?g)")) {
        f = new File(f.getPath() + "." + format);
    }

    try {
        BufferedImage img = zone.getImage();

        if (format.equals("jpg")) {
            BufferedImage rgb = new BufferedImage(
                img.getWidth(),
                img.getHeight(),
                BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = rgb.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, rgb.getWidth(), rgb.getHeight());
            g.drawImage(img, 0, 0, null);
            g.dispose();

            img = rgb;
        }

        ImageIO.write(img, format, f);

    } catch (IOException ex) {
        erreur(ex);
    }
}

    private void redimensionner() {
        JSpinner w = new JSpinner(new SpinnerNumberModel(zone.getImage().getWidth(), 1, 5000, 1));
        JSpinner h = new JSpinner(new SpinnerNumberModel(zone.getImage().getHeight(), 1, 5000, 1));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{t("msg.largeur"), w, t("msg.hauteur"), h},
            t("img.redim"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) zone.redimensionner((Integer) w.getValue(), (Integer) h.getValue());
    }

    // autosave
    private void boucleAutosave() {
    while (!Thread.currentThread().isInterrupted()) {
        try {
            Thread.sleep(30000);

            final BufferedImage[] copie = new BufferedImage[1];

            SwingUtilities.invokeAndWait(() ->
                copie[0] = ZoneDessin.copie(zone.getImage())
            );

            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("autosave.paint"))) {
                out.writeObject(new ProjetPaint(copie[0]));
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


    // socket
    private void demarrerServeur() {
        try { ServeurPaint.demarrer(5000); info(t("msg.serveurOk")); } catch (IOException ex) { erreur(ex); }
    }

    private void seConnecter() {
        final String hote = JOptionPane.showInputDialog(this, t("msg.adresse"), "localhost");
        if (hote == null || hote.trim().isEmpty()) return;
        new Thread(() -> {                                   
            try {
                client = new ClientReseau(hote.trim(), 5000, f -> SwingUtilities.invokeLater(() -> zone.appliquerDistante(f)));
                SwingUtilities.invokeLater(() -> info(t("msg.connecte")));
            } catch (IOException ex) { SwingUtilities.invokeLater(() -> erreur(ex)); }
        }, "connexion").start();
    }

    private void seDeconnecter() {
        try { if (client != null) client.fermer(); } catch (IOException ex) { }
        client = null;
        info(t("msg.deconnecte"));
    }

    // base de donnes
    private void enregistrerBase() {
        final String nom = JOptionPane.showInputDialog(this, t("msg.nom"));
        if (nom == null || nom.trim().isEmpty()) return;
        final BufferedImage copie = ZoneDessin.copie(zone.getImage());
        new Thread(() -> {
            try {
                DessinDAO dao = new DessinDAO();
                dao.initialiser();
                dao.enregistrer(nom.trim(), copie);
                SwingUtilities.invokeLater(() -> info(t("msg.baseOk")));
            } catch (Exception ex) { SwingUtilities.invokeLater(() -> erreur(ex)); }
        }, "jdbc-enregistrer").start();
    }

    private void ouvrirBase() {
        new Thread(() -> {
            try {
                final DessinDAO dao = new DessinDAO();
                dao.initialiser();
                final Map<Integer, String> liste = dao.lister();
                SwingUtilities.invokeLater(() -> choisirDansBase(dao, liste));
            } catch (Exception ex) { SwingUtilities.invokeLater(() -> erreur(ex)); }
        }, "jdbc-lister").start();
    }

    private void choisirDansBase(final DessinDAO dao, Map<Integer, String> liste) {
        if (liste.isEmpty()) { info(t("msg.aucun")); return; }
        JComboBox<String> combo = new JComboBox<String>();
        final List<Integer> ids = new ArrayList<Integer>(liste.keySet());
        for (int id : ids) combo.addItem(id + " – " + liste.get(id));
        int ok = JOptionPane.showConfirmDialog(this, new Object[]{t("msg.choisir"), combo},
            t("d.ouvrir"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return;
        final int id = ids.get(combo.getSelectedIndex());
        new Thread(() -> {
            try {
                final BufferedImage img = dao.charger(id);
                SwingUtilities.invokeLater(() -> zone.setImage(img));
            } catch (Exception ex) { SwingUtilities.invokeLater(() -> erreur(ex)); }
        }, "jdbc-charger").start();
    }

    
    private void changerLangue(Locale nouvelle) {
        new PaintApp(nouvelle, ZoneDessin.copie(zone.getImage())).setVisible(true);
        dispose();
    }

    public void dispose() {
        if (autosave != null) {
        autosave.interrupt();}
        super.dispose();
    }

    private void erreur(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), t("msg.erreur"), JOptionPane.ERROR_MESSAGE);
    }

    private void info(String m) {
        JOptionPane.showMessageDialog(this, m, t("msg.info"), JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) { }
        final Locale loc = Locale.getDefault().getLanguage().equals("fr") ? Locale.FRENCH : Locale.ENGLISH;
        SwingUtilities.invokeLater(() -> new PaintApp(loc, null).setVisible(true));
    }
}
