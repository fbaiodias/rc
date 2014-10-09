package sockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class UDPServer extends Server {

  public static int DATA_SIZE = 32767;
  public static int PORT = 58022;  
  public static boolean IS_RUNNING = true;

  public UDPServer (int port) {
    PORT = port;
  }

  public void run() {
	 
    try {
      DatagramSocket serverSocket = new DatagramSocket(PORT);
      byte[] receiveData = new byte[DATA_SIZE];
      byte[] sendData = new byte[DATA_SIZE];      
      
      String resSentence = "";

      System.out.println("UDP Server started at localhost:"+PORT);

      while(IS_RUNNING) 
      {
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(receivePacket);

        
        String sentence = new String(receivePacket.getData(), 0, receivePacket.getLength());
        
        if (sentence.startsWith("LST")) {
        	ArrayList<String> serverList = readFile("servers");
        	String chosenSS = serverList.get((int)Math.round(Math.random()*(serverList.size()-1)));
        	
        	ArrayList<String> fileList = readFile("files");
        	
			System.out.println("Number of files: "+fileList.size());
			
			if(fileList.isEmpty() || fileList.size() == 0) {
				resSentence = "EOF";
	        	resSentence += "\n";
			}
			else {
	        	resSentence = "AWL " + chosenSS + " " + fileList.size() + " ";
	        	
	        	for (String s : fileList) {
	        		resSentence += s + " ";
	        	}
	        	
	        	resSentence = resSentence.substring(0, resSentence.length()-1);
	        	resSentence += "\n";
			}
        }

        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();

        sendData = resSentence.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
        serverSocket.send(sendPacket);
      }
      
      serverSocket.close();
    }
    catch (Exception e) {
      System.out.println(e);
    }
  }
  
  public void close() {
	IS_RUNNING = false;
  }


  public static void main(String argv[]) {
    UDPServer udpServer = new UDPServer(58011);
    udpServer.start();
  }


}
