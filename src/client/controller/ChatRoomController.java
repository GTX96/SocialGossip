package client.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JTextArea;

import client.thread.requestSender.implementation.CloseChatRoomRequestSender;
import client.view.ChatRoomWindow;
import server.model.ChatRoom;
import server.model.User;

/**
 * Controller della GUI di una chatRoom
 * @author Gionatha Sturba
 *
 */
public class ChatRoomController extends Controller
{
	private ChatRoomWindow chatView;
	private User sender;
	private ChatRoom chatRoomReceiver;
	private ChatRoomController controllerRoom = this;
	
	//per invio messaggi
	private DatagramSocket clientSocket; //socket per conettersi al dispatcher dei messaggi della chatroom
	private InetAddress serverAddress; //indirizzo del dispatcher dei messaggi della chatroom
	private int port; //porta su cui e' in ascolto il dispatcher
	
	//buffer
	private static final int BUFFER_LEN = 1024;
	private byte[] buffer = new byte[BUFFER_LEN];

	public ChatRoomController(Socket connection, DataInputStream in, DataOutputStream out,User sender,ChatRoom chatRoomReceiver,Point location) throws SocketException, UnknownHostException 
	{
		super(connection, in, out);
		
		if(sender == null || chatRoomReceiver == null || location == null)
			throw new NullPointerException();
		
		chatView = new ChatRoomWindow(sender.getNickname()+": CHATROOM ["+chatRoomReceiver.getName().toUpperCase()+"]");
		setWindow(chatView);
		window.setLocation(location);
		
		this.sender = sender;
		this.chatRoomReceiver = chatRoomReceiver;
		
		openChat();
		serverAddress = InetAddress.getByName(chatRoomReceiver.getMessageAddress());
		this.port = chatRoomReceiver.getMessagePort();
		
		initListeners();
	}
	
	protected void initListeners()
	{
		//alla chiusura della chat
		chatView.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				closeChat();
			}
		});
		
		//al click sul bottone INVIA
		chatView.getBtnInviaTextButton().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				//invio messaggio sulla chatroom
				sendTextToChatRoom();
			}
		});
		
		//al click sul bottone CHIUDI CHATROOM
		chatView.getBtnChiudiChatRoom().addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new CloseChatRoomRequestSender(controllerRoom, connection, in, out,chatRoomReceiver.getName(),sender.getNickname()).start();
			}
		});
	}
	
	/**
	 * invia il messaggio sulla chatroom,tramite datagramma UDP
	 */
	private void sendTextToChatRoom()
	{
		JTextArea textArea = getTextArea();
		String text = textArea.getText();
		
		if(text == null || text.isEmpty()) {
			return;
		}
		//messaggio valido
		else {
			text = "["+sender.getNickname()+"]"+": "+text+"\n";
			
			//pulisco la textArea
			textArea.setText("");
			
			try {
				buffer = text.getBytes("UTF-8");
				
				//invio il messaggio con un pacchetto UDP,al dispatcher dei messaggi
				DatagramPacket msg = new DatagramPacket(buffer,buffer.length,serverAddress,port);
				
				clientSocket.send(msg);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Chiude la chatroom
	 */
	public synchronized void closeChat() {
		clientSocket.close();
		window.setVisible(false);
		window.dispose();
	}
	
	/**
	 * Apre la chatroom
	 * @throws SocketException errore di connessione
	 */
	public synchronized void openChat() throws SocketException {
		clientSocket = new DatagramSocket();
		window.setVisible(true);
	}
	
	public ChatRoom getChatRoomReceiver() {
		return chatRoomReceiver;
	}

	public boolean isVisible() {
		return window.isVisible();
	}
	
	public JTextArea getTextArea() {
		return chatView.getTextArea();
	}
	
	public JTextArea getJConversationArea() {
		return chatView.getConversationArea();
	}
}
