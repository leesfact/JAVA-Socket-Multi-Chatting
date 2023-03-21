package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import Dto.RequestDto;
import Dto.ResponseDto;
import entity.Room;
import lombok.Getter;

@Getter
public class ConnectedSocket extends Thread {

	private static List<ConnectedSocket> connectedSocketList = new ArrayList<>();
	private static List<Room> roomList = new ArrayList<>();
	private Socket socket;
	private String username;
	
	private Gson gson;
	
	public ConnectedSocket(Socket socket) {
		this.socket = socket;
		gson = new Gson();
		
	}
	
	@Override
	public void run() {
		BufferedReader bufferedReader;
		try {
			while(true) {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String requestJson = bufferedReader.readLine();
			
			System.out.println("요청: " + requestJson);
			requestMapping(requestJson);
			}
		} catch(SocketException e){
			connectedSocketList.remove(this);
			System.out.println(username + ": 클라이언트 종료");
		}catch (IOException e) {
			e.printStackTrace();
		}
}
	
	private void requestMapping(String requestJson) {
		RequestDto<?> requestDto = gson.fromJson(requestJson, RequestDto.class);
		Room room = null;
		
		switch(requestDto.getResource()) {
			case "usernameCheck":
				checkUsername((String) requestDto.getBody());
				break;
			case "createRoom":
				String createMessage = username + "님이 방을 생성하였습니다.";
				room = new Room((String) requestDto.getBody(), username);
				room.getUsers().add(this);
				roomList.add(room);
				sendToMe(new ResponseDto<String>("createRoomSuccessfully", null));
				refreshUsernameList(room);
				sendToAll(refreshRoomList(), connectedSocketList);
				sendToRoom(new ResponseDto<String>("createMessage", createMessage), room.getUsers());
				break;
			case "enterRoom":
				String welcomeMessage = username + "님이 입장하였습니다.";
				room = findRoom((Map<String, String>)requestDto.getBody()); 
				room.getUsers().add(this); //connectedSocket
				sendToMe(new ResponseDto<String>("enterRoomSuccessfully",null));
				refreshUsernameList(room);
				sendToRoom(new ResponseDto<String>("welcomeMessage", welcomeMessage), room.getUsers());
				break;
			case "sendMessage" :
				room = findConnectedRoom(username);
				sendToAll(new ResponseDto<String>("reciveMessage", username + " >>> " + (String)requestDto.getBody()), room.getUsers());
				break;
				
			case "exitRoom":
				String exitMessage = username + "님이 퇴장하였습니다.";
				room = findConnectedRoom(username);
				try {
					if(room.getRoomOwner().equals(username)) { //roomOwner이 나가면,
						
						sendToRoom(new ResponseDto<String>("exitMessage", exitMessage), room.getUsers());
						exitRoomAll(room);
					}else { // 유저가 나가면
						sendToRoom(new ResponseDto<String>("exitMessage", exitMessage), room.getUsers());
						exitRoom(room);
					}	
				}catch(NullPointerException e){
					sendToRoom(new ResponseDto<String>("exitMessage", exitMessage), room.getUsers());
					System.out.println("클라이언트 강제 종료");
				}
				break;
		}
	}
	
	private void checkUsername(String username) {
		if(username.isBlank()) {
			sendToMe(new ResponseDto<String>("usernameCheckIsBlank", "사용자 이름은 공백일 수 없습니다"));
			return;
		}
		
		for(ConnectedSocket connectedSocket : connectedSocketList) {
			if(connectedSocket.getUsername().equals(username)) {
				sendToMe(new ResponseDto<String>("usernameCheckIsDuplicate", "이미 사용중인 이름입니다."));
				return;
			}
		}
		
		this.username = username;
		connectedSocketList.add(this);
		sendToMe(new ResponseDto<String>("usernameCheckSuccessfully", null));
		sendToMe(refreshRoomList());
	}
	
	private ResponseDto<List<Map<String, String>>> refreshRoomList() {
		List<Map<String, String>> roomNameList = new ArrayList<>();
		
		for(Room room : roomList) {
			Map<String, String> roomInfo = new HashMap<>();
			roomInfo.put("roomName", room.getRoomName());
			roomInfo.put("roomOwner", room.getRoomOwner());
			roomNameList.add(roomInfo);
		}
		
		ResponseDto<List<Map<String, String>>> responseDto = new ResponseDto<List<Map<String, String>>>("refreshRoomList", roomNameList);
		return responseDto;
	}
	
	private Room findConnectedRoom(String username) {
		for(Room r : roomList) {
			for(ConnectedSocket cs : r.getUsers()) {
				if(cs.getUsername().equals(username)) {
					return r;
				}
			}
		}
		return null;
	}
	
	private Room findRoom(Map<String, String> roomInfo) {
		for(Room room : roomList) {
			if(room.getRoomName().equals(roomInfo.get("roomName"))
					&& room.getRoomOwner().equals(roomInfo.get("roomOwner"))) {
				return room;
			}
		}
		return null;
	}
	
	private void refreshUsernameList(Room room) {
		List<String> usernameList = new ArrayList<>();
		usernameList.add("방제목: " + room.getRoomName());
		for(ConnectedSocket connectedSocket : room.getUsers()) {
			if(connectedSocket.getUsername().equals(room.getRoomOwner())) {
				usernameList.add(connectedSocket.getUsername() + "(방장)");
				continue;
			}
			usernameList.add(connectedSocket.getUsername());
		}
		ResponseDto<List<String>> responseDto = new ResponseDto<List<String>>("refreshUsernameList", usernameList);
		sendToAll(responseDto, room.getUsers());
	}
	
	private void exitRoomAll(Room room) {
		sendToAll(new ResponseDto<String>("exitRoom", null), room.getUsers());
		roomList.remove(room);
		sendToAll(refreshRoomList(), connectedSocketList);
	}
	
	
	private void exitRoom(Room room) {
		room.getUsers().remove(this);
		sendToMe(new ResponseDto<String>("exitRoom", null));
		refreshUsernameList(room);
		
	}
	
	
	private void sendToMe(ResponseDto<?> responseDto) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream, true);
			
			String responseJson = gson.toJson(responseDto);
			printWriter.println(responseJson);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void sendToAll(ResponseDto<?> responseDto, List<ConnectedSocket> connectedSockets) {
		for(ConnectedSocket connectedSocket : connectedSockets) {
			try {
				OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
				PrintWriter printWriter = new PrintWriter(outputStream, true);
				
				String responseJson = gson.toJson(responseDto);
				printWriter.println(responseJson);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void sendToRoom(ResponseDto<?> responseDto, List<ConnectedSocket> users) {
	    for (ConnectedSocket connectedSocket : users) {
	        try {
	            OutputStream outputStream = connectedSocket.getSocket().getOutputStream();
	            PrintWriter printWriter = new PrintWriter(outputStream,true);
	            printWriter.println(gson.toJson(responseDto));
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
}

