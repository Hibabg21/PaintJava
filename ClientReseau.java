
import java.io.*;
import java.net.Socket;

/**
 * Côté client : envoie ses formes au serveur
 * et reçoit celles des autres utilisateurs.
 */
public class ClientReseau {

    public interface Reception {
        void formeRecue(Forme f);
    }

    private final Socket socket;
    private final ObjectOutputStream out;

    public ClientReseau(String hote, int port,
                        final Reception reception) throws IOException {

        socket = new Socket(hote, port);

        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();

        final ObjectInputStream in =
            new ObjectInputStream(socket.getInputStream());

        Thread t = new Thread(() -> {
            try {
                while (!socket.isClosed()) {
                    Object obj = in.readObject();

                    if (obj instanceof Forme) {
                        reception.formeRecue((Forme) obj);
                    }
                }

            } catch (EOFException | java.net.SocketException e) {
                System.out.println("Connexion fermée.");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }, "reception-reseau");

        t.setDaemon(true);
        t.start();
    }

    public synchronized void envoyer(Forme f) throws IOException {
        out.reset();
        out.writeObject(f);
        out.flush();
    }

    public synchronized void fermer() throws IOException {
        socket.close();
    }
}
