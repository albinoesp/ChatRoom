/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.net.Socket;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 *
 * @author jesus
 */
public class ServerInput implements Runnable {

    public static ArrayList<ServerInput> usersConnected = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String user;

    public ServerInput(Socket socket) {
        try {
            this.socket = socket;
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.user = reader.readLine();
            usersConnected.add(this);
            globalMessage("Server: " + user + " se ha conectado.");
        } catch (IOException e) {
            closeConnections(socket, reader, writer);
        }
    }

    public void run() {
        String message;

        while (socket.isConnected()) {
            try {
                message = reader.readLine();
                globalMessage(message);
            } catch (IOException e) {
                closeConnections(socket, reader, writer);
                break;
            }
        }
    }

    public void globalMessage(String message) {
        for (ServerInput serverInput : usersConnected) {
            try {
                if (!serverInput.user.equals(user)) {
                    serverInput.writer.write(message);
                    serverInput.writer.newLine();
                    serverInput.writer.flush();
                }
            } catch (IOException e) {
                closeConnections(socket, reader, writer);
            }
        }
    }
    
    public void exitUser(){
        usersConnected.remove(this);
        globalMessage("Server: " + user + " se ha desconectado.");
    }
    
    public void closeConnections(Socket socket, BufferedReader reader, BufferedWriter writer){
        exitUser();
        try{
            if (reader != null){
                reader.close();
            }
            if (writer != null){
                writer.close();
            }
            if (socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
