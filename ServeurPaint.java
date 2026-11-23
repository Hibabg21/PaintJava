import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/** Serveur du dessin collaboratif */
public class ServeurPaint {
    private static List<ObjectOutputStream> clients = new ArrayList<ObjectOutputStream>();
    private static ServerSocket serveur;

    /** Démarre le serveur dans un thread  */
    public static void demarrer(int port) throws IOException {
    serveur = new ServerSocket(port);

    Thread accueil = new Thread(() -> {
        try {
            while (!serveur.isClosed()) {
                final Socket s = serveur.accept();

                Thread t = new Thread(() -> gererClient(s));
                t.setDaemon(true);
                t.start();
            }
        } catch (SocketException e) {
            if (!serveur.isClosed()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    accueil.setDaemon(true);
    accueil.start();

    System.out.println("Serveur Paint démarré sur le port " + port);
}

    
private static void gererClient(Socket s) {
    ObjectOutputStream out = null;

    try {
        out = new ObjectOutputStream(s.getOutputStream());
        out.flush();

        ObjectInputStream in =
            new ObjectInputStream(s.getInputStream());

        synchronized (clients) {
            clients.add(out);
        }

        while (true) {
            Object o = in.readObject();

            if (o instanceof Forme) {
                diffuser(o, out);
            }
        }

    } catch (EOFException | SocketException e) {
        System.out.println("Client déconnecté");

    } catch (IOException | ClassNotFoundException e) {
        e.printStackTrace();

    } finally {
        synchronized (clients) {
            clients.remove(out);
        }

        try {
            if (out != null) out.close();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

private static void diffuser(Object o, ObjectOutputStream origine) {
    List<ObjectOutputStream> copieClients;

    synchronized (clients) {
        copieClients = new ArrayList<>(clients);
    }

    for (ObjectOutputStream c : copieClients) {
        if (c == origine) continue;

        try {
            synchronized (c) {
                c.writeObject(o);
                c.flush();
                c.reset();
            }

        } catch (IOException ex) {
            synchronized (clients) {
                clients.remove(c);
            }

            try {
                synchronized (c) {
                    c.close();
                }
            } catch (IOException ignored) { }
        }
    }
}


    public static void main(String[] args) throws Exception {
        demarrer(args.length > 0 ? Integer.parseInt(args[0]) : 5000);
        Thread.currentThread().join();                          
    }
}
