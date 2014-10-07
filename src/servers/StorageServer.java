package servers;

import java.io.*;
import java.net.*;

public class StorageServer {

  public static int port = 9880; //ver como e para ser isto
  public static boolean IS_RUNNING = true;
  public static int DATA_SIZE = 1024;

  public void close() {
	IS_RUNNING = false;
  }
  
  
  public static void main(String args[]) throws Exception {

    try {

      ServerSocket welcomeSocket = new ServerSocket(port);
      
      System.out.println("TCP Server started at localhost:"+port);

      Socket connectionSocket = welcomeSocket.accept();
      


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
        
        if (st.startsWith("REQ")) {
        	String fileName = st.split(" ")[1];
        	
        	fileName = fileName.substring(0, fileName.length()-1);
        	System.out.println("Finding: " + fileName);
        	
        	
        	byte[] fileBytes = readFile(fileName);
        	output.writeBytes("REP ok " + fileBytes.length + " ");
        	output.write(fileBytes);
        	output.writeBytes("\n");
        }
        
        System.out.println("Received: " + st);
      }
      
      // welcomeSocket.close();
    }
    catch (Exception e) {
      System.out.println("Failed to start TCP server at port " + port);
      System.out.println(e);
    }
  }
  
  
  public static byte[] readFile(String filename) throws IOException {
	    try {
	      File file = new File(System.getProperty("user.dir") + "/ss/9880/atum.txt");
	      
		  byte[] fileData = new byte[(int) file.length()];
		  DataInputStream dis = new DataInputStream(new FileInputStream(file));
		  dis.readFully(fileData);
		  dis.close();
		  
		  System.out.println("Loaded the file");
		  return fileData;
	    } catch (FileNotFoundException e) {
	      System.err.println("Couldn't find the file");
		  return "nok".getBytes();
		}
	  }
}
