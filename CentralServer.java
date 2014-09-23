import java.io.*; 
import java.net.*; 

class CentralServer 
{
  public static int PORT = 9876;
  public static int DATA_SIZE = 1024;
  
  public static void main(String args[]) throws Exception 
  {
    DatagramSocket serverSocket = new DatagramSocket(PORT);
    byte[] receiveData = new byte[DATA_SIZE];
    byte[] sendData = new byte[DATA_SIZE];      

    System.out.println("Starting UDP server at port " + PORT);

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
}