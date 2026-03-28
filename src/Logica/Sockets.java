/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Logica;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Andres Araya
 */
public class Sockets {

    private ServerSocket servidor;
    private Socket socket;
    private ObjectInputStream receptor;
    private ObjectOutputStream emisor;

    //Constructor que se comporta como Servidor
    public Sockets(int port) {
        try {
            servidor = new ServerSocket(port);
            socket = servidor.accept();
            System.out.println("Equipo conectado: " + socket.toString());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    //Constructor que se comporta como cliente
    public Sockets(String ip, int port) {
        try {
            socket = new Socket(ip, port);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public Ajedrez recibir() {
        Ajedrez salida = null;
        try {
            receptor = new ObjectInputStream(socket.getInputStream());
            salida = (Ajedrez) receptor.readObject();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return salida;
    }

    public void enviar(Ajedrez gato) {
        try {
            emisor = new ObjectOutputStream(socket.getOutputStream());
            emisor.writeObject(gato);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

}
