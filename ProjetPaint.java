
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/** Le dessin est sauvegardé et stocké sous forme de tableau d'octets. */
public class ProjetPaint implements Serializable {

    private static final long serialVersionUID = 1L;

    public byte[] png;

    public ProjetPaint(BufferedImage img) throws IOException {
        png = versPng(img);
    }

    public BufferedImage image() throws IOException {
        return depuisPng(png);
    }

    public static byte[] versPng(BufferedImage img) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if (!ImageIO.write(img, "png", bos)) {
            throw new IOException("Impossible d'écrire l'image au format PNG");
        }

        return bos.toByteArray();
    }

    public static BufferedImage depuisPng(byte[] data) throws IOException {
        if (data == null) {
            throw new IOException("Les données de l'image sont nulles");
        }

        BufferedImage lu = ImageIO.read(new ByteArrayInputStream(data));

        if (lu == null) {
            throw new IOException("Impossible de lire l'image PNG");
        }

        return ZoneDessin.copie(lu);
    }
}
