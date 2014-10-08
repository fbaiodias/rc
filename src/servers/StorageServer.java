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

		Socket connectionSocket = welcomeSocket.accept();

		DataInputStream input = new DataInputStream(
				connectionSocket.getInputStream());
		DataOutputStream output = new DataOutputStream(
				connectionSocket.getOutputStream());

		System.out.println("Central Server connected");

		while (IS_RUNNING) {
			// clientSentence = inFromClient.readLine();

			byte[] fileData = new byte[DATA_SIZE];

			int spaceCount = 0;
			byte[] digit = new byte[DATA_SIZE];
			for (int i = 0, j = 0; i < DATA_SIZE;) {

				byte tmp = input.readByte();

				if (tmp != '\n') {
					digit[i] = tmp;

					if (spaceCount == 3) {
						fileData[j++] = digit[i];
					} else if (digit[i] == ' ') {
						spaceCount++;
					}
					i++;
				}

				// System.out.print(digit[i]);

			}

			
			String st = new String(digit);
			System.out.println("ta vazio = " + st + ")");

			String fileName = st.split(" ")[1];
			fileName = fileName.trim();

			if (st.startsWith("REQ")) { // REQ Fn
				System.out.println("Finding: " + fileName);

				System.out.println("gonna try to read da file");
				byte[] fileBytes = readFile(fileName);

				System.out.println("gonna send da file nao");
				output.writeBytes("REP ok " + fileBytes.length + " ");
				output.write(fileBytes);
				output.writeBytes("\n");
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
			return "nok".getBytes();
		}
	}
}
