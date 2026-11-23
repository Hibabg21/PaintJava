import java.awt.image.BufferedImage;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Accès à la base de données (JDBC). Créer la base une fois :  CREATE DATABASE paintdb;
 * et mettre le connecteur (mysql-connector-j.jar) dans le classpath.
 */
public class DessinDAO {
    private String url  = System.getProperty("paint.db.url", "jdbc:mysql://localhost:3306/paintdb");
    private String user = System.getProperty("paint.db.user", "root");
    private String pass = System.getProperty("paint.db.pass", "");

    private Connection connexion() throws SQLException {
        return DriverManager.getConnection(url, user, pass);
    }

    public void initialiser() throws SQLException {
        try (Connection c = connexion(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS dessin (id INT AUTO_INCREMENT PRIMARY KEY, "
                + "nom VARCHAR(100) NOT NULL, date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP, contenu LONGBLOB NOT NULL)");
        }
    }

    public void enregistrer(String nom, BufferedImage img) throws SQLException, java.io.IOException {
        try (Connection c = connexion();
             PreparedStatement ps = c.prepareStatement("INSERT INTO dessin(nom, contenu) VALUES (?, ?)")) {
            ps.setString(1, nom);
            ps.setBytes(2, ProjetPaint.versPng(img));
            ps.executeUpdate();
        }
    }

    public Map<Integer, String> lister() throws SQLException {
        Map<Integer, String> res = new LinkedHashMap<Integer, String>();
        try (Connection c = connexion(); Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, nom FROM dessin ORDER BY date_creation DESC")) {
            while (rs.next()) res.put(rs.getInt("id"), rs.getString("nom"));
        }
        return res;
    }

    public BufferedImage charger(int id) throws SQLException, java.io.IOException {
    try (Connection c = connexion();
         PreparedStatement ps = c.prepareStatement(
             "SELECT contenu FROM dessin WHERE id = ?")) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return ProjetPaint.depuisPng(rs.getBytes("contenu"));
            }
        }
    }

    throw new SQLException("Dessin introuvable : " + id);
}
}
