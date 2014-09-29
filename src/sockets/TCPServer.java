package sockets;

import java.io.*;
import java.net.*;

public class TCPServer extends Thread {

  public static int PORT = 6789;

  public TCPServer (int port) {
    PORT = port;
  }

  public void run() {

    try {
      String clientSentence;
      String capitalizedSentence;
      ServerSocket welcomeSocket = new ServerSocket(PORT);

      System.out.println("TCP Server started at localhost:"+PORT);

      while(true) {
        Socket connectionSocket = welcomeSocket.accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        clientSentence = inFromClient.readLine();
        System.out.println("Received: " + clientSentence);
        capitalizedSentence = clientSentence.toUpperCase() + '\n';
        outToClient.writeBytes(capitalizedSentence);
      }
    }
    catch (Exception e) {
      System.out.println("Failed to start TCP server at port " + PORT);
      System.out.println(e);
    }
  }

  public static void main(String argv[]) {
    TCPServer tcpServer = new TCPServer(58011);
    tcpServer.start();
  }


}
