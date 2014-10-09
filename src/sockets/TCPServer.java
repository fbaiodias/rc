package sockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class TCPServer extends Server {

	public static int PORT = 58022;
	public static boolean IS_RUNNING = true;
	public static int DATA_SIZE = 300000;

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
					byte tmp = input.readByte();

					if (tmp == '\n') {
						break;
					}
					
					digit[i] = tmp;
					
				}

				String st = new String(digit);
				
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
						output.writeBytes(message); // UTF is a string
														// encoding
					}

					byte[] digitUPC = new byte[DATA_SIZE];
					int spaceCount = 0;
					int fileSize = -1;
					for (int i = 0; spaceCount < 2; i++) {
						byte tmp = input.readByte();
						digitUPC[i] = tmp;
						
						if (tmp == '\n') {
							break;
						}
						
						if (tmp == ' ') {
							spaceCount++;
							if (spaceCount == 2) {
								String response = new String(digitUPC);
								fileSize = Integer
										.parseInt(response.split(" ")[1]);
								System.out.println("parseInt: fileSize=" + fileSize);
							}
						}
					}
					
					String messageUp = new String(digitUPC);
					
					//Verificar se o ficheiro foi recebido com sucesso
					
					byte[] fileData = null;
					if (fileSize == -1) {
						System.err.println("File not found on server");
					} else {
						fileData = new byte[fileSize];

						for (int j = 0; j < fileSize; j++) {
							fileData[j] = input.readByte();
						}
						
						st = new String(fileData);
					}
					
					
					if (messageUp.startsWith("UPC")) {
						System.out.println("Starts with UPC. " + " fileName:" + fileName + " fileSize:" + fileSize + " message:" + messageUp);
						System.out.println(st);
						
						Socket socket;
						DataInputStream inputSS;
						DataOutputStream outputSS;
						
						for(String s : readFile("servers")) {
							String[] sParts = s.split(" ");
							socket = new Socket(sParts[0], Integer.parseInt(sParts[1])); 
							inputSS = new DataInputStream( socket.getInputStream());
							outputSS = new DataOutputStream( socket.getOutputStream());
							
							byte[] sss = ("UPS " + fileName + " " + fileSize + " ").getBytes();

							byte[] c = new byte[sss.length + fileData.length];
							System.arraycopy(sss, 0, c, 0, sss.length);
							System.arraycopy(fileData, 0, c, sss.length, fileData.length);
							outputSS.write(c);
							outputSS.write('\n');
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
			//System.out.println(e);
			e.printStackTrace();
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
