package servers;

import java.io.*;
import java.net.*;

public class StorageServer {

	public static int port = 59000; // ver como e para ser isto
	public static boolean IS_RUNNING = true;
	public static int DATA_SIZE = 32767;

	public void close() {
		IS_RUNNING = false;
	}

	public static void main(String args[]) throws Exception {

		System.out.println("Storage Server");

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-p")) {
				port = Integer.parseInt(args[i + 1]);
			}
		}
		/* try { */

		ServerSocket welcomeSocket = new ServerSocket(port);

		System.out.println("TCP Server started at localhost:" + port);

		System.out.println("Central Server connected");

		Socket connectionSocket = null;
		DataInputStream input = null;
		DataOutputStream output = null;
		
		while (IS_RUNNING) {
			
			connectionSocket = welcomeSocket.accept();

			input = new DataInputStream(connectionSocket.getInputStream());
			output = new DataOutputStream(connectionSocket.getOutputStream());
			// clientSentence = inFromClient.readLine();

			byte[] fileData = new byte[DATA_SIZE];

			int spaceCount = 0;
			byte[] digit = new byte[DATA_SIZE];
			for (int i = 0, j = 0; i < DATA_SIZE; i++) {
				System.out.print("indice: " + i + ")");
				byte tmp = input.readByte();

				if (tmp == '\n') {
					break;
				}
				digit[i] = tmp;

				if (spaceCount == 3) {
					fileData[j++] = digit[i];
				} else if (digit[i] == ' ') {
					spaceCount++;
				}

				// System.out.print(digit[i]);

			}

			
			String st = new String(digit);
			System.out.println("ta vazio = '" + st + "'");

			String fileName = st.split(" ")[1];
			fileName = fileName.trim();

			if (st.startsWith("REQ")) { // REQ Fn
				System.out.println("Finding: " + fileName);

				System.out.println("gonna try to read da file");
				byte[] fileBytes = readFile(fileName);
				
				if (fileBytes == null) {
					output.writeBytes("REP ERR\n");
				} else {
					output.writeBytes("REP ok " + fileBytes.length + " ");
					output.write(fileBytes);
					output.writeBytes("\n");
				}

			} else if (st.startsWith("UPS")) { // UPS fn size data

				// fileName = fileName.substring(0, fileName.length());
				System.out.print("Saving: (" + fileName + ")");
				System.out.print("my name isnt");

				FileOutputStream fileOutput = new FileOutputStream(
						System.getProperty("user.dir") + "/ss/" + port + "/"
								+ fileName);
				fileOutput.write(fileData);
				fileOutput.close();

				//System.out.println(new String(fileData));

				System.out.println("File saved");

				output.writeBytes("AWS ok\n");
			}

			//System.out.println("Received: " + st + ")");
			input.close();
			output.close();
			connectionSocket.close();
		}

		// welcomeSocket.close();
	}

	/*
	 * catch (Exception e) {
	 * System.out.println("Failed to start TCP server at port " + port);
	 * System.out.println(e); } }
	 */

	public static byte[] readFile(String filename) throws IOException {
		try {
			String filePath = System.getProperty("user.dir") + "/ss/" + port
					+ "/" + filename;
			System.out.println("Finding file (" + filePath + ")");
			File file = new File(filePath);

			byte[] fileData = new byte[(int) file.length()];
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			dis.readFully(fileData);
			dis.close();

			System.out.println("Loaded the file");
			return fileData;
		} catch (FileNotFoundException e) {
			System.err
					.println("Couldn't find the file. This error was is in the do: "
							+ e);
			//return "nok".getBytes();
			return null;
		}
	}
}
