package com.tomas.exam;

import java.net.*;
import java.io.*;
import java.util.Scanner;
public class Client {
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    Scanner teclado = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";

    public void conect(String ip, int puerto) {
        try {
            socket = new Socket(ip, puerto);
            showText("Conectado a :" + socket.getInetAddress().getHostName());
        } catch (Exception e) {
            showText("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }

    public static void showText(String s) {
        System.out.println(s);
    }

    public void openFlows() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            showText("Error en la apertura de flujos");
        }
    }

    public void send(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            showText("IOException on enviar");
        }
    }

    public void closeConnection() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
            showText("Conexión terminada");
        } catch (IOException e) {
            showText("IOException on cerrarConexion()");
        }finally{
            System.exit(0);
        }
    }

    public void executeConnection(String ip, int puerto) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    conect(ip, puerto);
                    openFlows();
                    receiveData();
                } finally {
                    closeConnection();
                }
            }
        });
        hilo.start();
    }

    public void receiveData() {
        String st = "";
        try {
            do {

                st = (String) bufferDeEntrada.readUTF();
                String[] message = st.split(":");
                showText("\n[" + message[0] +"] => " + message[1]);
                System.out.print("\n[Juan] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {}
    }

    public void writeData() {
        String entrada = "";
        while (true) {
            System.out.print("[Juan] => ");
            entrada = "Juan:" + teclado.nextLine();
            if(entrada.length() > 0)
                send(entrada);
        }
    }

    public static void main(String[] argumentos) {
        Client cliente = new Client();
        String ip = "localhost";

        String puerto = "5050";
        cliente.executeConnection(ip, Integer.parseInt(puerto));
        cliente.writeData();
    }
}