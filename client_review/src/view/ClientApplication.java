package view;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.google.gson.Gson;

import Dto.RequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ClientApplication extends JFrame {

	private static final long serialVersionUID = 1L;
	private static ClientApplication instance;
	
	private Gson gson;
	private Socket socket;
	
	private JPanel mainPanel;
	private CardLayout mainCard;
	
	private JTextField usernameField;
	private JTextField sendMessageField;

	@Setter
	private List<Map<String,String>> roomInfoList;
	private DefaultListModel<String> roomNameListModel;
	private DefaultListModel<String> usernameListModel;
	
	private JList roomList;
	private JList joinUserList;
	
	private JTextArea chattingContent;
	
	
	public static ClientApplication getInstance() {
		if(instance == null) {
			try {
				instance = new ClientApplication();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientApplication frame = ClientApplication.getInstance();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	private ClientApplication() throws IOException {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				RequestDto<String> requestDto = new RequestDto<String>("exitRoom", null);
				sendRequest(requestDto);
			}
		});
		
		/*=============<< init >>============ */
		gson = new Gson();
		try {
			socket = new Socket("127.0.0.1", 9090); 
			ClientRecive clientRecive = new ClientRecive(socket);
			clientRecive.start();
			
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (ConnectException e1) {
			JOptionPane.showMessageDialog(this, "서버에 접속할 수 없습니다.", "접속오류", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
																						
		/*=============<< frame set >>============ */
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 480, 800);
		setResizable(false);
        setTitle("KakaoTalk");
		
		/*=============<< images set >>============ */
		final BufferedImage loginImg = ImageIO.read(ClientApplication.class.getResourceAsStream("/images/kakao.png"));
		final BufferedImage chattingImg = ImageIO.read(ClientApplication.class.getResourceAsStream("/images/kakaoChatting.png"));
		/*=============<< panel >>============ */
		mainPanel = new JPanel();
		JPanel loginPanel = new JPanel(){
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(loginImg, 0, 0, getWidth(), getHeight(), null);
            }
        };
		JPanel roomListPanel = new JPanel(){
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(chattingImg, 0, 0, getWidth(), getHeight(), null);
            }
        };
		JPanel roomPanel = new JPanel();
		
		
		/*=============<< layout >>============ */
		mainCard = new CardLayout();
		mainPanel.setLayout(mainCard);
		loginPanel.setLayout(null);
		roomListPanel.setLayout(null);
		roomPanel.setLayout(null);
		
		/*=============<< panel set >>============ */
		setContentPane(mainPanel);
		mainPanel.add(loginPanel, "loginPanel");
		mainPanel.add(roomListPanel, "roomListPanel");
		mainPanel.add(roomPanel, "roomPanel");
		
		
		/*=============<< login panel >>============ */
		
		JButton enterButton = new JButton();
		enterButton.setIcon(new ImageIcon(ClientApplication.class.getResource("/images/kakao_login_medium_narrow.png")));
		
		usernameField = new JTextField();
		usernameField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					RequestDto<String> usernameCheckReqDto = 
							new RequestDto<String>("usernameCheck", usernameField.getText());
					sendRequest(usernameCheckReqDto);
				}
			}
		});
		
		
		
		
		usernameField.setBounds(130, 532, 180, 52);
		loginPanel.add(usernameField);
		usernameField.setColumns(10);
		
		enterButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RequestDto<String> usernameCheckReqDto = 
						new RequestDto<String>("usernameCheck", usernameField.getText());
				sendRequest(usernameCheckReqDto);
			}
		});
		
		
		enterButton.setBounds(130, 603, 187, 44);
		loginPanel.add(enterButton);
		
		
		
		/*=============<< roomList panel  >>============ */
		
		
		JScrollPane roomListScroll = new JScrollPane();
		roomListScroll.setBounds(0, 96, 464, 660);
		roomListPanel.add(roomListScroll);
		
		roomNameListModel = new DefaultListModel<String>();
		roomList = new JList(roomNameListModel);
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int selectedIndex = roomList.getSelectedIndex();
					RequestDto<Map<String, String>> requestDto = 
							new RequestDto<Map<String,String>>("enterRoom", roomInfoList.get(selectedIndex));
					sendRequest(requestDto);
				}
			}
		});
		roomListScroll.setViewportView(roomList);
		
		JButton createRoomButton = new JButton();
		createRoomButton.setIcon(new ImageIcon(ClientApplication.class.getResource("/images/plus2.png")));
		createRoomButton.addMouseListener(new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			String roomName = null;
			while(true) {
				roomName = JOptionPane.showInputDialog(null, "생성할 방의 제목을 입력하세요", "방생성", JOptionPane.PLAIN_MESSAGE);
				if(roomName == null) {
					return;
				}
				if(!roomName.isBlank()) {
					break;
				}
				JOptionPane.showMessageDialog(null, "공백은 사용할 수 없습니다.", "방생성 오류", JOptionPane.ERROR_MESSAGE);
			}
			RequestDto<String> requestDto = new RequestDto<String>("createRoom", roomName);
			sendRequest(requestDto);
		}
	});
		createRoomButton.setBounds(103, 23, 67, 51);
		roomListPanel.add(createRoomButton);
		
		/*=============<< room panel  >>============ */
		
		JScrollPane joinUserListScroll = new JScrollPane();
		joinUserListScroll.setBounds(0, 6, 385, 103);
		roomPanel.add(joinUserListScroll);
		
		usernameListModel = new DefaultListModel<String>();
		joinUserList = new JList(usernameListModel);
		joinUserListScroll.setViewportView(joinUserList);
		
		
		JButton roomExitButton = new JButton();
		roomExitButton.setIcon(new ImageIcon(ClientApplication.class.getResource("/images/Exit.png")));
		roomExitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(JOptionPane.showConfirmDialog(null, "정말로 방을 나가시겠습니까?","방 나가기",JOptionPane.YES_NO_OPTION)==0) {
					RequestDto<String> requestDto =  new RequestDto<String>("exitRoom", null);
					sendRequest(requestDto);
				}
			}
		});
		roomExitButton.setBounds(401, 35, 51, 50);
		roomPanel.add(roomExitButton);
		
		JScrollPane chattingContentScroll = new JScrollPane();
		chattingContentScroll.setBounds(0, 121, 470, 516);
		roomPanel.add(chattingContentScroll);
		
		chattingContent = new JTextArea();
		chattingContentScroll.setViewportView(chattingContent);
		chattingContent.setEditable(false);
		
		
		
		sendMessageField = new JTextField();
		sendMessageField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ENTER) {
					RequestDto<String> requestDto = new RequestDto<String>("sendMessage", sendMessageField.getText());
					sendMessageField.setText("");
					sendRequest(requestDto);
				}
			}
		});
		sendMessageField.setBounds(0, 649, 385, 107);
		roomPanel.add(sendMessageField);
		sendMessageField.setColumns(10);
		
		JButton sendButton = new JButton();
		sendButton.setIcon(new ImageIcon(ClientApplication.class.getResource("/images/Enter.png")));
		sendButton.addMouseListener (new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				RequestDto<String> requestDto = new RequestDto<String>("sendMessage", sendMessageField.getText());
				sendMessageField.setText("");
				sendRequest(requestDto);
			}
		});
		sendButton.setBounds(401, 678, 51, 50);
		roomPanel.add(sendButton);	
	}
	
	private void sendRequest(RequestDto<?> requestDto) {
		String reqJson = gson.toJson(requestDto);
		OutputStream outputStream = null;
		PrintWriter printWriter = null;
		try {
			outputStream = socket.getOutputStream();
			printWriter = new PrintWriter(outputStream, true);
			printWriter.println(reqJson);
			System.out.println("클라이언트 -> 서버: " + reqJson);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
