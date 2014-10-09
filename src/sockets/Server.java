package sockets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

public abstract class Server extends Thread {

	public abstract void run();

	public abstract void close();

	public ArrayList<String> readFile(String name) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					System.getProperty("user.dir") + "/cs/" + name + ".txt"));
			String line;
			while (true) {
				line = in.readLine();
				if (line != null) {
					list.add(line);
				} else {
					break;
				}
			}
		in.close();
		} catch (Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	
	
	
	public void addFile(String fileName) {
		String filePath = System.getProperty("user.dir") + "/cs/files.txt";
		Writer output;
		try {
			output = new BufferedWriter(new FileWriter(filePath, true));
			if(!readFile("files").isEmpty()) {
				output.append("\n");
			}
			output.append(fileName);
			output.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}
