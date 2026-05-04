package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Tubería de comunicación cliente-servidor por sockets TCP con
 * serialización de objetos.
 *
 * Detalles importantes:
 *  - El ObjectOutputStream se construye antes que el ObjectInputStream
 *    (protocolo de Java: el constructor de OOS envía 4 bytes de cabecera,
 *    y el de OIS se bloquea hasta leer los 4 bytes del peer).
 *  - Ambos streams se construyen una sola vez por conexión.
 *  - Después de writeObject() llamamos a flush() y reset() para
 *    invalidar el caché interno de referencias y forzar que el peer
 *    reciba el estado más reciente del objeto.
 *
 * @author Andres Araya
 */
public class Sockets {

    private ServerSocket servidor;
    private Socket socket;
    private ObjectInputStream receptor;
    private ObjectOutputStream emisor;
    private volatile boolean conectado;

    /** Constructor servidor: bloquea hasta que el cliente se conecta. */
    public Sockets(int port) {
        try {
            servidor = new ServerSocket(port);
            socket = servidor.accept();
            System.out.println("Equipo conectado: " + socket.toString());
            inicializarStreams();
        } catch (IOException ex) {
            System.err.println("Error servidor: " + ex.getMessage());
            cerrar();
        }
    }

    /** Constructor cliente: se conecta a la IP y puerto indicados. */
    public Sockets(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            inicializarStreams();
        } catch (IOException ex) {
            System.err.println("Error cliente: " + ex.getMessage());
            cerrar();
        }
    }

    private void inicializarStreams() throws IOException {
        // OOS primero: envía la cabecera del stream al peer.
        emisor = new ObjectOutputStream(socket.getOutputStream());
        emisor.flush();
        // OIS después: lee la cabecera enviada por el peer (puede bloquear).
        receptor = new ObjectInputStream(socket.getInputStream());
        conectado = true;
    }

    public boolean estaConectado() {
        return conectado;
    }

    /** Recibe un Ajedrez del peer. Devuelve null si la conexión cae. */
    public Ajedrez recibir() {
        try {
            if (!conectado) return null;
            return (Ajedrez) receptor.readObject();
        } catch (Exception ex) {
            // EOFException o conexión cerrada: marcamos como desconectado.
            conectado = false;
            return null;
        }
    }

    /** Envía un Ajedrez al peer. reset() evita que se reenvíe la misma referencia cacheada. */
    public synchronized void enviar(Ajedrez juego) {
        try {
            if (!conectado) return;
            emisor.writeObject(juego);
            emisor.flush();
            emisor.reset();
        } catch (IOException ex) {
            System.err.println("Error al enviar: " + ex.getMessage());
            conectado = false;
        }
    }

    public void cerrar() {
        conectado = false;
        try { if (receptor != null) receptor.close(); } catch (IOException ignored) {}
        try { if (emisor != null) emisor.close(); } catch (IOException ignored) {}
        try { if (socket != null && !socket.isClosed()) socket.close(); } catch (IOException ignored) {}
        try { if (servidor != null && !servidor.isClosed()) servidor.close(); } catch (IOException ignored) {}
    }
}
