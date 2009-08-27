package org.ooc.frontend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class CompilerDaemon {

	public CompilerDaemon() {
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					
					ServerSocket server = new ServerSocket(14269);
					Socket socket = server.accept();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					while(socket.isConnected()) {
						String line = reader.readLine();
						StringTokenizer st = new StringTokenizer(line, " ");
						String[] args = new String[st.countTokens()];
						int index = 0;
						while(st.hasMoreElements()) {
							args[index] = st.nextToken();
						}
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
		System.out.println("Daemon started");
		
	}
	
}
