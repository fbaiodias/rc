package servers;

import java.io.*;
import java.net.*;
import java.util.Arrays;

class User {
	public static int DATA_SIZE = 300000;
	public static int CS_PORT = 59022;
	public static String CS_NAME = "localhost";
	public static int portSS = -1;
	public static String IPSS;

	public static DatagramSocket clientSocket;

	public static void main(String args[]) throws Exception {
		System.out.println("User");

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-n")) {
				CS_NAME = args[i + 1];
			} else if (args[i].equals("-p")) {
				CS_PORT = Integer.parseInt(args[i + 1]);
			}
		}

		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
				System.in));
		clientSocket = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(CS_NAME);

		byte[] sendData = new byte[DATA_SIZE];
		byte[] receiveData = new byte[DATA_SIZE];

		Socket s = null;
		DataInputStream input = null;
		DataOutputStream output = null;

		Socket ss = null;
		DataInputStream inputSS = null;
		DataOutputStream outputSS = null;

		String sentence = "";

		while (true) {

			sentence = inFromUser.readLine();

			if (sentence.equals("list")) {
				
				sendData = new String("LST\n").getBytes();

				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, CS_PORT);
				clientSocket.send(sendPacket);
				

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				clientSocket.receive(receivePacket);

				String reply = new String(receivePacket.getData()).split("\n")[0];

				if (reply.startsWith("AWL")) {
					String[] parts = reply.split(" ");
					IPSS = parts[1];
					portSS = Integer.parseInt(parts[2]);

					System.out.println("SS is located at " + IPSS + ":"	+ portSS);

					//Mostrar lista de ficheiros disponiveis
					int length = Integer.parseInt(parts[3]);
					for (int i = 0; i < length; i++) {
						System.out.println((i + 1) + ": " + parts[4 + i]);
					}
				} else {
					System.err.println("Server returned an error: " + reply);
				}
			} else if (sentence.equals("exit")) {
				break;
			} else if (sentence.startsWith("retrieve")) {
				if (portSS != -1) {
					ss = new Socket(IPSS, portSS);
					inputSS = new DataInputStream(ss.getInputStream());
					outputSS = new DataOutputStream(ss.getOutputStream());
					String fileName = sentence.substring(9);

					String message = "REQ " + fileName + "\n";
					if (ss != null) {
						outputSS.writeBytes(message); // UTF is a string encoding
					}


					byte[] digit = new byte[DATA_SIZE];
					int spaceCount = 0;
					int fileSize = -1;

					try {
						for (int i = 0; spaceCount < 3; i++) {
							byte tmp = inputSS.readByte();
							digit[i] = tmp;
							
							if (tmp == '\n') {
								break;
							}
							
							if (tmp == ' ') {
								spaceCount++;
								if (spaceCount == 3) {
									String response = new String(digit);
									fileSize = Integer
											.parseInt(response.split(" ")[2]);
								}
							}
							

						}
						
						//Verificar se o ficheiro foi recebido com sucesso
						if (fileSize == -1) {
							System.err.println("File not found on server");
						} else {
							byte[] fileData = new byte[fileSize];

							for (int j = 0; j < fileSize; j++) {
								fileData[j] = inputSS.readByte();
							}
							FileOutputStream fileOutput = new FileOutputStream("files/"
									+ fileName);
							fileOutput.write(fileData);
							fileOutput.close();

							System.out.println("File saved");
						}
						
					} catch (Exception e) {
						System.err.println("Unexpected error: " + e);
					}

					ss.close();
					inputSS.close();
					outputSS.close();
				} else {
					System.err.println("Storage server unknown. Please run 'list' before 'retrieve'");
				}

			} else if (sentence.startsWith("upload")) {
				try {
					s = new Socket(CS_NAME, CS_PORT);
					input = new DataInputStream(s.getInputStream());
					output = new DataOutputStream(s.getOutputStream());

					//System.out.println("Connected to TCP Central Server at " + CS_NAME + ":" + CS_PORT);

					String fileName = sentence.substring(7);

					byte[] fileBytes = readFile(fileName);

					String message = "UPR " + fileName + "\n";
					output.writeBytes(message); // UTF is a string encoding

					String filePath = System.getProperty("user.dir") + "/files/"
							+ fileName;
					File file = new File(filePath);
					int fileSize = (int) file.length() + 7;

					byte[] digit = new byte[fileSize];
					for (int i = 0; i < fileSize; i++) {
						byte tmp = input.readByte();

						if (tmp == '\n') {
							break;
						}

						digit[i] = tmp;
					}

					String st = new String(digit);

					if (st.startsWith("AWR")) {
						String status = st.substring(4);
						if (status.startsWith("dup")) {
							System.err.println("Duplicate file");
						} else if (status.startsWith("new")) {
							System.out.println("Sending file...");

							message = "UPC " + fileBytes.length + " ";
							output.writeBytes(message);
							output.write(fileBytes);
							output.writeBytes("\n");
							System.out.println("File uploaded");
						} else {
							System.out.println("Received: " + st); // SABER DISTO
						}
					} else {
						System.out.println("Received: " + st); // SABER DISTO
					}

					s.close();
					input.close();
					output.close();
				} catch (ConnectException e) {
					System.err.println("Server unavailable");
				}
				
			} else {
				System.err.println("UNKNOWN COMMAND");
			}
		}

		// ACHO QUE ISTO NAO TAVA A FAZER NADA:
		// SABER DISTO

		/*
		 * Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		 * public void run() { System.out.println("In shutdown hook");
		 * clientSocket.close(); } }, "Shutdown-thread"));
		 */
	}

	public static byte[] readFile(String filename) throws IOException {
		try {
			File file = new File(System.getProperty("user.dir") + "/files/"
					+ filename);
			byte[] fileData = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(fileData);
			dis.close();

			return fileData;
		} catch (FileNotFoundException e) {
			System.err.println("Local file not found");
			return "nok".getBytes();
		}
	}
}
