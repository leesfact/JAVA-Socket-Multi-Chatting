package main;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ServerApplication {
	
	public static void main(String[] args) {
		JFrame serverFrame = new JFrame("서버");
		serverFrame.setSize(150, 80);

        JLabel statusLabel = new JLabel("서버 구동 중...", SwingConstants.CENTER);
        serverFrame.add(statusLabel, BorderLayout.CENTER);

        serverFrame.setVisible(true);
        
        ServerSocket serverSocket = null;
        try {
			serverSocket = new ServerSocket(9090);
			while(true) {
				Socket socket = serverSocket.accept();
				ConnectedSocket connectedSocket = new ConnectedSocket(socket);
				connectedSocket.start();
			}
			
			
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
