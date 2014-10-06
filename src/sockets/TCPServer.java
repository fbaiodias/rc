package sockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer extends Thread {

  public static int PORT = 58011;
  public static boolean IS_RUNNING = true;
  public static int DATA_SIZE = 1024;
  ArrayList<String> serverList = new ArrayList<String>();
  ArrayList<String> fileList = new ArrayList<String>();

  public TCPServer (int port) {
    PORT = port;
  }

  public void run() {

    try {
    	
    	//ler ficheiros onde esta a informacao relativa aos SS
        BufferedReader inServers = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/cs/servers.txt"));
        BufferedReader inFiles = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/cs/files.txt"));
        
        String line;
        
        //le a lista de StorageServers
        while (true) {
      	  line = inServers.readLine();
      	  if (line != null) {
      		  serverList.add(line);
      	  } else {
      		  break;
      	  }
        }
        
        //le a lista de ficheiros
        while (true) {
      	  line = inFiles.readLine();
      	  if (line != null) {
      		  fileList.add(line);
      	  } else {
      		  break;
      	  }
        }
        
        inServers.close();
        inFiles.close();
    	
      String clientSentence;
      String capitalizedSentence;
      ServerSocket welcomeSocket = new ServerSocket(PORT);
      
      System.out.println("TCP Server started at localhost:"+PORT);

      Socket connectionSocket = welcomeSocket.accept();
      
      //nao sei o que estes dois sao
      BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
      DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

      DataInputStream input = new DataInputStream( connectionSocket.getInputStream()); 
      DataOutputStream output = new DataOutputStream( connectionSocket.getOutputStream()); 
      
      System.out.println("Client connected");

      while(IS_RUNNING) {
        //clientSentence = inFromClient.readLine();

      	byte[] digit = new byte[DATA_SIZE];
        for(int i = 0; i < DATA_SIZE; i++) {
        	digit[i] = input.readByte();
        	
        	//System.out.print(digit[i]);

        	if(digit[i] == '\n') {
        		break;
        	}
        }

        String st = new String(digit);
        
        
        System.out.println("Received: " + st);
        capitalizedSentence = st.toUpperCase() + '\n';
        outToClient.writeBytes(capitalizedSentence);
      }
      
      // welcomeSocket.close();
    }
    catch (Exception e) {
      System.out.println("Failed to start TCP server at port " + PORT);
      System.out.println(e);
    }
  }

  public void close() {
	IS_RUNNING = false;
  }

  public static void main(String argv[]) {
    TCPServer tcpServer = new TCPServer(58011);
    tcpServer.start();
  }


}
