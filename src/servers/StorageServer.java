package servers;

import java.io.*;
import java.net.*;

public class StorageServer {

  public static int port = 59000; //ver como e para ser isto
  public static boolean IS_RUNNING = true;
  public static int DATA_SIZE = 32767;

  public void close() {
	IS_RUNNING = false;
  }
  
  
  public static void main(String args[]) throws Exception {

	  for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				port = Integer.parseInt(args[i + 1]);
			}
		}
    /*try {*/

      ServerSocket welcomeSocket = new ServerSocket(port);
      
      System.out.println("TCP Server started at localhost:"+port);

      Socket connectionSocket = welcomeSocket.accept();
      


      DataInputStream input = new DataInputStream( connectionSocket.getInputStream()); 
      DataOutputStream output = new DataOutputStream( connectionSocket.getOutputStream()); 
      
      System.out.println("Client connected");

      while(IS_RUNNING) {
        //clientSentence = inFromClient.readLine();

    	byte[] fileData = new byte[DATA_SIZE];
    	
    	int spaceCount = 0;
      	byte[] digit = new byte[DATA_SIZE];
        for(int i = 0, j=0; i < DATA_SIZE; i++) {
        	digit[i] = input.readByte();

        	if(digit[i] == '\n') {
        		break;
        	}
        	
        	if(spaceCount == 3) {
        		fileData[j++] = digit[i];
        	}
        	else if(digit[i] == ' ') {
        		spaceCount++;
           	}
        	//System.out.print(digit[i]);

        }

        String st = new String(digit);

        String fileName = st.split(" ")[1];
    	fileName = fileName.substring(0, fileName.length()-1);
        
        if (st.startsWith("REQ")) { // REQ Fn
        	System.out.println("Finding: " + fileName);
        	
        	byte[] fileBytes = readFile(fileName);
        	output.writeBytes("REP ok " + fileBytes.length + " ");
        	output.write(fileBytes);
        	output.writeBytes("\n");
        }
        else if (st.startsWith("UPS")) { // UPS fn size data
        	System.out.println("Saving: (" + fileName + ")");
        	System.out.println("my name isnt");
        	FileOutputStream fileOutput = new FileOutputStream (System.getProperty("user.dir") + "/ss/" + port + "/" + "testa.txt");
            fileOutput.write(fileData);
            fileOutput.close();
            
            System.out.println(new String(fileData));     
            
            System.out.println("File saved");

        	output.writeBytes("AWS ok\n");
        }
        
        System.out.println("Received: " + st);
      }
      
      // welcomeSocket.close();
    }
    /*catch (Exception e) {
      System.out.println("Failed to start TCP server at port " + port);
      System.out.println(e);
    }
  }*/
  
  
  public static byte[] readFile(String filename) throws IOException {
	    try {
	    	File file = new File(System.getProperty("user.dir") + "/ss/" + port + "/" + filename);
	      
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
