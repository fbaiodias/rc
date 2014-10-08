package sockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer extends Server {

	public static int PORT = 58022;
	public static boolean IS_RUNNING = true;
	public static int DATA_SIZE = 32767;

	public TCPServer(int port) {
		PORT = port;
	}

	public void run() {

		try {
			ServerSocket welcomeSocket = new ServerSocket(PORT);

			System.out.println("TCP Server started at localhost:" + PORT);

			Socket connectionSocket = null;

			DataInputStream input = null;
			DataOutputStream output = null;

			System.out.println("Client connected to TCP");

			while (IS_RUNNING) {
				
				connectionSocket = welcomeSocket.accept();

				input = new DataInputStream(
						connectionSocket.getInputStream());
				output = new DataOutputStream(
						connectionSocket.getOutputStream());
				
				byte[] digit = new byte[DATA_SIZE];
				for (int i = 0; i < DATA_SIZE; i++) {
					System.out.println("printing " + i);
					byte tmp = input.readByte();
					
					System.out.print(digit[i]);

					if (tmp == '\n') {
						break;
					}
					
					digit[i] = tmp;
					
				}

				String st = new String(digit);
				System.out.println("TCP RECEIVED: " + st);
				
				String message = "";
				
				
				if (st.startsWith("UPR")) {
					String fileName = st.split(" ")[1];
					fileName = fileName.trim();
					ArrayList<String> fileList = readFile("files");
					
					message = "AWR new\n";
					
					if (fileList.contains(fileName)) {
						message = "AWR dup\n";
					}

					if (welcomeSocket != null) {
						System.out.println("Writing this message:" + message);
						output.writeBytes(message); // UTF is a string
														// encoding
					}

					digit = new byte[DATA_SIZE];
					for (int i = 0; i < DATA_SIZE; i++) {
						System.out.print("Am i stuck?" + i);
						byte tmp = input.readByte();

						

						if (tmp == '\n') {
							break;
						}
						
						digit[i] = tmp;
					}
					
					st = new String(digit);
					
					if (st.startsWith("UPC")) {
						System.out.println("UPC up in this upc");
						String[] parts = st.split(" ", 3);
						
						String fileSize = parts[1];
						
						Socket socket;
						DataInputStream inputSS;
						DataOutputStream outputSS;
						
						for(String s : readFile("servers")) {
							String[] sParts = s.split(" ");
							socket = new Socket(sParts[0], Integer.parseInt(sParts[1])); 
							inputSS = new DataInputStream( socket.getInputStream());
							outputSS = new DataOutputStream( socket.getOutputStream());
							outputSS.writeBytes("UPS " + fileName + " " + fileSize + " " + parts[2] + "\n");
						}
						//verificar se tive sucesso ou nao pois palmas
						//James Marcus
						
						addFile(fileName);
						
						
					}
				}
				else {
					message = "ERR";
				}

				input.close();
				output.close();
				connectionSocket.close();
				
			}

			// welcomeSocket.close();
		} catch (Exception e) {
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
