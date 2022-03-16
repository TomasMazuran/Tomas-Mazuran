package com.tomas.exam;

import com.tomas.exam.entity.User;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Group {

    private Socket socket;
    private ServerSocket serverSocket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    Scanner escaner = new Scanner(System.in);
    final String COMANDO_TERMINACION = "salir()";

    List<User> users = new ArrayList<>();
    int cont = 0;

    public void conect(int puerto) {
        try {
            serverSocket = new ServerSocket(puerto);
            showText("Esperando conexión entrante en el puerto " + String.valueOf(puerto) + "...");
            socket = serverSocket.accept();
            showText("Conexión establecida con: " + socket.getInetAddress().getHostName() + "\n\n\n");
        } catch (Exception e) {
            showText("Error en levantarConexion(): " + e.getMessage());
            System.exit(0);
        }
    }
    public void flows() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            showText("Error en la apertura de flujos");
        }
    }

    public void receiveData() {
        String st = "";
        try {
            do {
                st = (String) bufferDeEntrada.readUTF();
                String[] message = st.split(":");
                showText("\n[" + message[0] +"] => " + message[1]);
                System.out.print("\n[" + users.get(cont - 1).getUserName() +"] => ");
            } while (!st.equals(COMANDO_TERMINACION));
        } catch (IOException e) {
            closeConnection();
        }
    }


    public void send(String s) {
        try {
            bufferDeSalida.writeUTF(s);
            bufferDeSalida.flush();
        } catch (IOException e) {
            showText("Error en enviar(): " + e.getMessage());
        }
    }

    public static void showText(String s) {
        System.out.print(s);
    }

    public void writeData() {

        users.add(new User("Tomas"));
        users.add(new User("Jesus"));
        users.add(new User("Celeste"));
        users.add(new User("Paola"));

        while (true) {
            if(cont == users.size() - 1){
                cont = 1;
            }else{
                cont += 1;
            }
            System.out.print("["+ users.get(cont - 1).getUserName() + "] => ");
            send(users.get(cont - 1).getUserName() + ":" + escaner.nextLine());
        }
    }

    public void closeConnection() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
            showText("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            showText("Conversación finalizada....");
            System.exit(0);

        }
    }

    public void executeConnection(int puerto) {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        conect(puerto);
                        flows();
                        receiveData();
                    } finally {
                        closeConnection();
                    }
                }
            }
        });
        hilo.start();
    }

    public static void main(String[] args) throws IOException {
        Group group = new Group();
        showText("Ingresa el puerto [5050 por defecto]: ");
        String puerto = "5050";
        group.executeConnection(Integer.parseInt(puerto));
        group.writeData();
    }
}
