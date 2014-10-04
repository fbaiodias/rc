package servers;

import java.io.*;
import java.net.*;

class User 
{
  public static int DATA_SIZE = 10024;
  public static int CS_PORT = 9876;
  public static String CS_NAME = "localhost";
  public static int portSS = -1;
  public static String IPSS;
  
  public static DatagramSocket clientSocket;

  public static void main(String args[]) throws Exception 
  {
    for (int i=0; i < args.length; i++) {
      if(args[i].equals("-n")) {
        CS_NAME = args[i+1];
      } else if(args[i].equals("-p")) {
        CS_PORT = Integer.parseInt(args[i+1]);
      }
    }

    System.out.println("Connecting to UDP server at "+CS_NAME+":"+CS_PORT);

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName(CS_NAME);

    byte[] sendData = new byte[DATA_SIZE];
    byte[] receiveData = new byte[DATA_SIZE];

    Socket s = new Socket(CS_NAME, CS_PORT); 
    DataInputStream input = new DataInputStream( s.getInputStream()); 
    DataOutputStream output = new DataOutputStream( s.getOutputStream()); 

    System.out.println("Connected to TCP server at "+CS_NAME+":"+CS_PORT);

    while(true) {
      String sentence = inFromUser.readLine();

      if(sentence.equals("list")) {
        sendData = new String("LST\n").getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, CS_PORT);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String reply = new String(receivePacket.getData()).split("\n")[0];
        
        if(reply.startsWith("AWL")) {
	        String[] parts = reply.split(" ");
	        IPSS = parts[1];
	        portSS = Integer.parseInt(parts[2]);
	
	        System.out.println("SS is located at " + IPSS + ":" + portSS);
	
	        int length = Integer.parseInt(parts[3]);
	        for(int i=0; i<length; i++) {
	        	System.out.println((i+1)+": "+parts[4+i]);
	        }
        } else {
	        System.out.println("LIST: " + reply);	        
        }
      }
      else if(sentence.equals("exit")) {
        break;
      }
      else if(sentence.startsWith("retrieve")) {
        System.out.println("RETRIEVE: "+sentence.substring(9));
      }
      else if(sentence.startsWith("upload")) {
        String fileName = sentence.substring(7);
        
        byte[] fileBytes = readFile(fileName);
        
        // System.out.println("UPLOAD: "+fileName);
        
        String message = "UPR "+fileName+"\n";
        output.writeBytes(message); // UTF is a string encoding
        
        byte[] digit = new byte[DATA_SIZE];
        for(int i = 0; i < DATA_SIZE; i++) {
        	digit[i] = input.readByte();
        	
        	//System.out.print(digit[i]);

        	if(digit[i] == '\n') {
        		break;
        	}
        }

        String st = new String(digit);
        
        // System.out.print(st);

        if(st.startsWith("AWR")) {
            String status = st.substring(4);
            if(status.startsWith("dup")) {
            	System.out.println("Duplicate file");
            } else if(status.startsWith("new")) {
            	System.out.println("Sending file");
            	
                message = "UPC "+fileBytes.length+" "+fileBytes+"\n";
                output.writeBytes(message); // UTF is a string encoding
            	System.out.println("File uploaded");
            } else {
                System.out.println("Received: "+ st);             	
            }
        } else {
            System.out.println("Received: "+ st); 
        }
      }
      else {
        System.out.println("UNKNOWN COMMAND");
      }
    }

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        public void run() {
            System.out.println("In shutdown hook");
            clientSocket.close();
        }
    }, "Shutdown-thread"));
  } 

  public static byte[] readFile(String filename) throws IOException {
    try {
      File file = new File(System.getProperty("user.dir") + "/files/" + filename);
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
