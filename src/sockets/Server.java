package sockets;

import java.io.BufferedReader;
import java.io.FileReader;
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
}
