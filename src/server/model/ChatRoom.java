package server.model;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.MulticastSocket;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server.model.exception.UserAlreadyRegistered;
import server.thread.DispatcherChatRoomMessage;
import utils.PortScanner;

/**
 * Rappresenta un gruppo di utenti in social Gossip
 * @author Gionatha Sturba
 *
 */
public class ChatRoom implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1787928909027681736L;
	
	private String name; //nome chatroom
	
	private InetAddress msAddress; //indirizzo ip del multicast socket
	private InetAddress messageAddress; //indirizzo ip del thread che distribuisce i messaggi della chatroom
	private int msPort; //porta per indirizzo multicast
	private int messagePort;  //porta per ricezione messaggi
	
	private transient MulticastSocket ms;
	
	//thread dispatcher dei messaggi della chatroom
	private transient DispatcherChatRoomMessage dispatcherMessage;
	
	private List<User> subscribers; //lista utenti iscritti
		
	//serializzazione
	public static final String FIELD_NAME = "name";
	public static final String FIELD_MS_ADDRESS = "ms-address";
	public static final String FIELD_MS_PORT = "ms-port";
	public static final String FIELD_MESSAGE_ADDRESS = "message-address";
	public static final String FIELD_MESSAGE_PORT = "message-port";
	public static final String FIELD_LIST_SUBS = "list-subscriber";

	/**
	 * Crea una nuova chatroom vuota,con un nome e un indirizzo assegnato
	 * @param name
	 * @param address
	 * @throws Exception 
	 */
	public ChatRoom(String name,InetAddress msAddress,InetAddress messageAddress) throws Exception
	{
		if(name == null || msAddress == null)
			throw new NullPointerException();
		
		if(name.isEmpty() || !msAddress.isMulticastAddress())
			throw new IllegalArgumentException();
		
		this.name = name;
		subscribers = new LinkedList<User>();

		
		//inizializzo multicast
		this.msAddress = msAddress;
		this.msPort = PortScanner.freePort();
		
		//porta non trovata
		if(msPort == -1)
			throw new Exception();

		MulticastSocket ms = new MulticastSocket(msPort);
		this.ms = ms;
		
		//inizializzo indirizzo thread listener messaggi
		this.messageAddress = messageAddress;
	
		//faccio partire il thread che si occupa di gestire i messaggi della chatroom
		dispatcherMessage = new DispatcherChatRoomMessage(ms,msAddress);
		this.messagePort = dispatcherMessage.getListeningPort();
		dispatcherMessage.start();
	}
	
	/**
	 * Crea una chatroom a partire dal nome,l'indirizzo di multicast,l'indirizzo del dispatcher e la lista degli iscritti
	 * @param name nome chatroom
	 * @param msAddress indirizzo multicast
	 * @param port porta multicast
	 * @param messageAddress indirzzo dispatcher messaggi
	 * @param messagePort porta su cui e' in ascolto il dispatcher
	 * @param subs lista iscritti
	 */
	public ChatRoom(String name,InetAddress msAddress,int port,InetAddress messageAddress,int messagePort,List<User> subs)
	{
		if(name == null || msAddress == null || port <= 0 || messageAddress == null || messagePort <= 0 || subs == null)
			throw new IllegalArgumentException();
		
		this.name = name;
		this.msAddress = msAddress;
		this.msPort = port;
		this.messageAddress = messageAddress;
		this.messagePort = messagePort;
		this.subscribers = subs;
	}

	
	/**
	 * Crea una chatroom ,a partire dal solo nome
	 * @param name
	 */
	public ChatRoom(String name)
	{
		if(name == null)
			throw new NullPointerException();
		
		if(name.isEmpty())
			throw new IllegalArgumentException();
		
		this.name = name;
		this.msAddress = null;
		this.subscribers = null;
	}
	
	/**
	 * Chiude la chatroom
	 */
	public synchronized void close() {
		dispatcherMessage.interrupt();
		
		while(dispatcherMessage.isAlive()) {
			//aspetto che il thread termini
		}
	}
	
	/**
	 * 
	 * @return nome della chatroom
	 */
	public synchronized String getName() {
		return this.name;
	}
	
	/**
	 * 
	 * @return lista degli utenti iscritti alla chatroom
	 */
	public synchronized List<User> getSubscribers(){
		return Collections.unmodifiableList(subscribers);
	}
	
	/**
	 * 
	 * @return numero degli iscritti alla chatroom
	 */
	public synchronized Integer numSubscribers() {
		return new Integer(subscribers.size());
	}
	
	
	
	/**
	 * Aggiunge un nuovo iscritto alla chatRoom. Il primo utente ad essere inserito e' l'admin
	 * @param user
	 * @throws UserAlreadyRegistered
	 */
	public synchronized void addNewSubscriber(User user) throws UserAlreadyRegistered
	{
		if(user == null)
			throw new NullPointerException();
		
		//se l'utente e' gia' iscritto
		if(subscribers.contains(user))
			throw new UserAlreadyRegistered();
		
		subscribers.add(user);
	}
	
	public static JSONObject toJsonObject(ChatRoom cr)
	{
		if(cr == null)
			throw new NullPointerException();
		
		JSONObject jsonChatRoom = new JSONObject();
		
		//nome chatroom
		jsonChatRoom.put(FIELD_NAME,cr.getName());
		//indirizzo multicast
		jsonChatRoom.put(FIELD_MS_ADDRESS,cr.getIPAddress());
		//porta indirizzo multicast
		jsonChatRoom.put(FIELD_MS_PORT, cr.getPort());
		//indrizzo per ricevere messaggi
		jsonChatRoom.put(FIELD_MESSAGE_ADDRESS,cr.getMessageAddress());
		//porta per ricevere messaggi
		jsonChatRoom.put(FIELD_MESSAGE_PORT,cr.getMessagePort());
		
		//utenti registrati alla chatroom
		JSONArray subs = new JSONArray();
		
		for (User sub : cr.getSubscribers()) 
		{
			subs.add(User.toJsonObject(sub));
		}
		
		jsonChatRoom.put(FIELD_LIST_SUBS,subs);
		
		return jsonChatRoom;
	}
	
	@Override
	public String toString()
	{
		return "["+name.toUpperCase()+"] "+"Iscritti: "+numSubscribers();
	}
	
	/**
	 * 
	 * @return indirizzo ip multicast della chatroom
	 */
	public synchronized String getIPAddress() {
		return msAddress.toString().replaceAll("[^\\d.]","");
	}
	
	/**
	 * 
	 * @return porta su cui e' attivo il multicast socket della chatroom
	 */
	public synchronized Integer getPort() {
		return new Integer(msPort);
	}
	
	/**
	 * 
	 * @return indirizzo ip del thread che distribuisce i messaggi della chatroom
	 */
	public synchronized String getMessageAddress() {
		return messageAddress.toString().replaceAll("[^\\d.]","");
	}
	
	/**
	 * 
	 * @return multicast socket della chatroom
	 */
	public synchronized MulticastSocket getMulticastSocket() {
		return this.ms;
	}
	
	/**
	 * 
	 * @return porta sui cui e' in ascolto il thread dispatcher dei messaggi
	 */
	public synchronized Integer getMessagePort() {
		return new Integer(messagePort);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj == null)
			return false;
		
		ChatRoom other = (ChatRoom) obj;
		
		//2 chatroom sono uguali se hanno lo stesso nome
		
		if (name == null) {
			if (other.name != null)
				return false;
			
		} else if (!name.equalsIgnoreCase(other.name))
			return false;
		
		return true;
	}
}
