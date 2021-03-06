package client.thread;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import client.controller.ChatRoomController;
import client.controller.HubController;
import server.model.ChatRoom;

/**
 * Thread che ascolta i messaggi provenienti da altre chatroom
 * @author Gionatha Sturba
 *
 */
public class ListenerChatRoomMessage extends Thread
{
	private ChatRoom chatroom;
	private String name;
	private InetAddress chatroomAddr;
	private HubController controller;
	private static final int BUFFER_LEN = 1024;
	private static final int timeout = 600;
	
	private MulticastSocket ms; //multicast socket per ricevere messaggi della chatroom
	
	
	public ListenerChatRoomMessage(HubController controller,ChatRoom chatroom) throws UnknownHostException, IOException 
	{
		if(chatroom == null || controller == null)
			throw new NullPointerException();
		
		this.chatroom = chatroom;
		this.controller = controller;
		
		this.name = chatroom.getName();
		this.ms = new MulticastSocket(chatroom.getPort());
		this.chatroomAddr = InetAddress.getByName(chatroom.getIPAddress());
		
		//join sull'multicast socket,dove vengono trasmessi i messaggi
		ms.joinGroup(chatroomAddr);
		ms.setSoTimeout(timeout);

	}
	
	/**
	 * Ciclo di vita del thread
	 */
	public void run()
	{
		byte[] buffer = new byte[BUFFER_LEN];
		
		DatagramPacket receivedPacket = new DatagramPacket(buffer,buffer.length);

		while(!Thread.interrupted())
		{
			try {				
				ms.receive(receivedPacket);
				
				//parso il messaggio arrivato
				String text = new String(receivedPacket.getData(),0,receivedPacket.getLength(), "UTF-8");
				
				//prendo il controller della chatrooms
				ChatRoomController chatroomControl = controller.openChatRoomFromNewMessage(chatroom);
				
				//se e' arrivato il messaggio di chiusura
				if(text.equals("CHATROOM CLOSED"))
				{
					chatroomControl.showInfoMessage("ChatRoom CHIUSA!","CHIUSURA",true);
					chatroomControl.closeChat();
					break;
				}
				else {
					//aggiungo testo alla conversazione della chatroom
					chatroomControl.getJConversationArea().append(text);
				}	
			} 
			//timeout
			catch(SocketTimeoutException e) {}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//chiudo socket
		close();
	}
	
	public String getChatRoomName() {
		return name;
	}
	
	/**
	 * Termina il listener
	 */
	public void close()
	{
		try {
			//lascio il gruppo di multicast
			ms.leaveGroup(chatroomAddr);
			//chiudo il socket
			ms.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
