package servers; 

import sockets.*;


class CentralServer {
  
  public static int PORT = 9877;
  public static int DATA_SIZE = 1024;
  
  public static void main(String args[]) throws Exception {
    for (int i=0; i < args.length; i++) {
      if(args[i].equals("-p")) {
        PORT = Integer.parseInt(args[i+1]);
      }
    }

    TCPServer tcpServer = new TCPServer(PORT);
    tcpServer.start();

    UDPServer udpServer = new UDPServer(PORT);
    udpServer.start();
  } 
}