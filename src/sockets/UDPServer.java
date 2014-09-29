package sockets;

import java.io.*;
import java.net.*;

public class UDPServer extends Thread {

  public static int DATA_SIZE = 1024;
  public static int PORT = 9876;

  public UDPServer (int port) {
    PORT = port;
  }

  public void run() {

    try {
      DatagramSocket serverSocket = new DatagramSocket(PORT);
      byte[] receiveData = new byte[DATA_SIZE];
      byte[] sendData = new byte[DATA_SIZE];      

      System.out.println("UDP Server started at localhost:"+PORT);

      while(true) 
      {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);

        String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        System.out.println("RECEIVED: " + sentence);

        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();

        String capitalizedSentence = sentence.toUpperCase();
        sendData = capitalizedSentence.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
      }
    }
    catch (Exception e) {
      System.out.println("Failed to start UDP server at port " + PORT);
      System.out.println(e);
    }
  }

  public static void main(String argv[]) {
    UDPServer udpServer = new UDPServer(58011);
    udpServer.start();
  }


}
