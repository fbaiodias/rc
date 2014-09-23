import java.io.*;
import java.net.*;

class User 
{
  public static int CS_PORT = 9876;
  public static int DATA_SIZE = 1024;
  public static String CS_NAME = "localhost";

  public static void main(String args[]) throws Exception 
  {
    for (int i=0; i < args.length; i++) {
      switch(args[i]) {
        case "-n":
          CS_NAME = args[i+1];
        break;
        case "-p":
          CS_PORT = Integer.parseInt(args[i+1]);
        break;
      }
    }

    System.out.println("Connecting to UDP server at "+CS_NAME+":"+CS_PORT);

    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
    DatagramSocket clientSocket = new DatagramSocket();
    InetAddress IPAddress = InetAddress.getByName(CS_NAME);

    byte[] sendData = new byte[DATA_SIZE];
    byte[] receiveData = new byte[DATA_SIZE];

    while(true) {
      String sentence = inFromUser.readLine();

      if(sentence.equals("list")) {
        sendData = new String("LST").getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, CS_PORT);
        clientSocket.send(sendPacket);

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        String modifiedSentence = new String(receivePacket.getData());

        System.out.println("LIST: " + modifiedSentence);
      }
      else if(sentence.equals("exit")) {
        break;
      }
      else if(sentence.startsWith("retrieve")) {
        System.out.println("RETRIEVE: "+sentence.substring(9));
      }
      else if(sentence.startsWith("upload")) {
        System.out.println("UPLOAD: "+sentence.substring(7));
      }
      else {
        System.out.println("UNKNOWN COMMAND");
      }
    }

    clientSocket.close();
  } 
}