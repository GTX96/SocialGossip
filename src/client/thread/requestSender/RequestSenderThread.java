package client.thread.requestSender;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import client.controller.Controller;
import communication.TCPMessages.Message;
import communication.TCPMessages.MessageAnalyzer;
import communication.TCPMessages.request.RequestMessage;
import communication.TCPMessages.response.ResponseMessage;
import communication.TCPMessages.response.fail.ResponseFailedMessage;

/**
 * Modello astratto che rappresenta un Thread che invia una richiesta al server tramite connessione TCP
 * @author Gionatha Sturba
 *
 */
public abstract class RequestSenderThread extends Thread 
{	
	protected Socket connection; //connessione TCP con il server
	protected DataInputStream in; //input stream della connessione
	protected DataOutputStream out; //output stream della connessione
	
	protected final String serverName = "localhost";
	protected final int port = 5000;
	
	protected Controller controller; //controller della finestra dove viene richiamato il thread
	protected RequestMessage request; //messaggio di richiesta da inviare al server
	protected String JsonResponse; //risposta in formato Json che invia il server
	protected JSONObject response; //risposta parsata in formato Json
	protected boolean init; //per gestire corretta inizializzazione del thread
	
	/**
	 * Crea un nuovo thread per inviare una richiesta
	 * @param controller controller della finestra dove viene richiamato il thread
	 */
	public RequestSenderThread(Controller controller,Socket connection,DataInputStream in,DataOutputStream out)
	{
		super();
		
		if(controller == null || connection == null || in == null || out == null)
			throw new NullPointerException();
		
		if(connection.isClosed() == true) {
			throw new IllegalArgumentException();
		}
		
		this.controller = controller;
		this.connection = connection;
		this.in = in;
		this.out = out;
		
		//parametri non inizializati
		request = null;
		JsonResponse = null;
		response = null;
		init = false;
	}
	
	/**
	 * Ciclo di vita del thread che invia la richiesta
	 */
	public void run()
	{
		//fase di inizializzazione
		init();
		
		if(init)
		{
			try {
				//creo messaggio di richiesta
				createRequest();
				
				//se la richiesta e' stata creata,invio il messaggio
				if(request != null)
				{
					//invio richiesta al server
					out.writeUTF(request.getJsonMessage());
					
					//non appena arriva la risposta la leggo
					JsonResponse = in.readUTF();
					
					//analizzo risposta e mando una risposta la server
					analyzeResponse(JsonResponse);
				}
			} catch (IOException e) {
				IOErrorHandler();
				e.printStackTrace();
			}

		}		
	}
	
	/**
	 * Analizza la risposta alla richiesta inviata al server.
	 * @param JsonResponse
	 */
	protected void analyzeResponse(String JsonResponse)
	{
		try 
		{
			//parso messaggio Json rappresentate risposta del server
			System.out.println(JsonResponse); //TODO DEBUG
			response = MessageAnalyzer.parse(JsonResponse);
			
			//se non e' un messaggio di risposta
			if(MessageAnalyzer.getMessageType(response) != Message.Type.RESPONSE) 
			{
				unexpectedMessageHandler();
				return;
			}
			
			//analizzo il tipo del messaggio di risposta
			ResponseMessage.Type outcome = MessageAnalyzer.getResponseType(response);
			
			//tipo risposta non trovato
			if(outcome == null)
			{
				invalidResponseHandler();
				return;
			}
			
			//controllo esito della risposta ricevuta
			switch(outcome) 
			{
				//logout avvenuto
				case SUCCESS:
					successResponseHandler();
					break;
				
				case FAIL:
					//analizzo l'errore riscontrato
					ResponseFailedMessage.Errors error = MessageAnalyzer.getResponseFailedErrorType(response);
					
					//errore non trovato
					if(error == null) 
					{
						invalidResponseErrorTypeHandler();
						return;
					}
					
					failedResponseHandler(error);
					
					break;
					
				default:
					invalidResponseHandler();
					break;
			}
		} 
		catch (ParseException e) 
		{
			parseErrorHandler();
			e.printStackTrace();
		}
	}
	
	/**
	 * Fase di inizializzazione prima di inviare la richiesta al server
	 */
	protected abstract void init();
	/**
	 * Creazione messaggio di richiesta da inviare al server
	 */
	protected abstract void createRequest();

	/**
	 * Gestione errore di IO
	 */
	protected abstract void IOErrorHandler();
	/**
	 * Gestione risposta del server non valida
	 */
	protected abstract void invalidResponseHandler();
	/**
	 * Gestione errore nel tipo dell'errore di risposta
	 */
	protected abstract void invalidResponseErrorTypeHandler();
	/**
	 * Gestione alla risposta di un messaggio di errore da parte del server
	 * @param error errore riscontrato dal server alla richiestat
	 */
	protected abstract void failedResponseHandler(ResponseFailedMessage.Errors error);
	/**
	 * Gestione nel parsing del messaggio di risposta dal server
	 */
	protected abstract void parseErrorHandler();
	/**
	 * Gestione messaggio di risposta inaspettato
	 */
	protected abstract void unexpectedMessageHandler();
	/**
	 * Gestione caso di risposta di successo alla richiesta
	 */
	protected abstract void successResponseHandler();
}
