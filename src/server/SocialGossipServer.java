package server;

import java.io.IOException;

import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import communication.RMI.RMIServerInterface;
import server.model.*;
import server.thread.UserRequestHandler;
import utils.Config;

/**
 * Rappresenta la struttura del server di Social Gossip.
 * Implementa anche il bootstrapper del server.
 * @author Gionatha Sturba
 *
 */
public class SocialGossipServer implements Runnable
{
	private Network reteSG; //rappresenta la struttura della rete degli utenti di social gossip
	private List<ChatRoom> chatrooms; //lista delle chatroom attive
	
	private ServerSocket listenerSocket = null; //socket in cui e' in ascolto il server
	private ThreadPoolExecutor executor = null; //pool di thread per gestire i vari client che arrivano
	
	public SocialGossipServer(int port) throws IOException 
	{		
		reteSG = new Network();
		listenerSocket = new ServerSocket(port);
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		chatrooms = new LinkedList<ChatRoom>();
	}
	
	//ciclo di vita del server di social gossip
	@Override
	public void run() 
	{	
		try 
		{
			//inizializzo protocollo RMI
			initRMI();
			
			while(true)
			{
				Socket newClient = listenerSocket.accept();
				
				//sottometto la gestione del client arrivato ad un thread del pool
				executor.submit(new UserRequestHandler(newClient,reteSG,chatrooms));
				
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally 
		{
			try 
			{
				//chiudo socket di accettazione
				if(listenerSocket != null)
					listenerSocket.close();
				
				//chiudo il pool di thread
				if(executor != null)
					executor.shutdownNow();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Inizializzazione del protocollo RMI lato server
	 * @throws RemoteException se c'e' un errore nell'inizializzazione del protocollo RMI. 
	 */
	private void initRMI() throws RemoteException
	{
		//oggetto che gestisce i canali RMI degli utenti
		RMIChannelManager RMIUserChannelManager = new RMIChannelManager(reteSG);
		
		//creo lo stub dell'oggetto allocato precedentemente
		RMIServerInterface stub = (RMIServerInterface) UnicastRemoteObject.exportObject(RMIUserChannelManager,3900);
		
		//creo il registro
		LocateRegistry.createRegistry(Config.SERVER_RMI_PORT);
		
		//ottengo il registro
		Registry reg = LocateRegistry.getRegistry(Config.SERVER_RMI_PORT);
		
		//istanzio l'oggetto 
		reg.rebind(Config.SERVER_RMI_SERVICE_NAME,stub);
	}
	
	/**
	 * Bootstrapper del server
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			//creo istanza del server di social gossip
			SocialGossipServer server = new SocialGossipServer(Config.SERVER_TCP_PORT);
			
			//faccio partire il server
			server.run();
			
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
