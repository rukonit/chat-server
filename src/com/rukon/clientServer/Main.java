package com.rukon.clientServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// == public methods == //
public class Main {
    public static Map<String, ServerClient> listClient = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(5000);

            while (true) {
                ServerClient t = new ServerClient(serverSocket.accept());
                listClient.put(t.getName(), t);


            }
        }
        catch (IOException e){
            System.out.println("Something went wrong with server socket: " + e.getMessage());
            e.printStackTrace();
        }

    }
}

// == sub classes == //
class ServerClient extends Thread{
    private String userName;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter printWriter;

    public ServerClient(Socket socket) {

        this.socket = socket;
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            this.userName = input.readLine();
            }
        catch (IOException e){
            System.out.println("Data send/receive failed: " + e.getMessage());
            e.printStackTrace();
            }
        this.start();

     }

    @Override
    public void run() {
        System.out.println("Connected");
    try {
        while (true) {
            String inMessage = input.readLine();
            if (inMessage.toString().equals("logout")){
                System.out.println(this.getName() + " is logged out");
                break;

            }
            sendMessage(inMessage);
            System.out.println(inMessage);
        }
    }
    catch (Exception e) {
        System.out.println("Data send/receive failed: " + e.getMessage());
        e.printStackTrace();
    }


    }

    public String getUserName() {
        return userName;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getInput() {
        return input;
    }

    public synchronized PrintWriter getPrintWriter() {
        return this.printWriter;
    }

    public void sendMessage(String message) throws InterruptedException {
        for(Map.Entry<String, ServerClient> client: Main.listClient.entrySet()){
        if(client.getKey() != this.getName()) {
            client.getValue().getPrintWriter().println("[" + this.getName() + "]: " + message);
        }

       }
     }
}
