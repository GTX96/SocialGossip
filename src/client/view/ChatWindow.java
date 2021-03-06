package client.view;

import java.awt.EventQueue;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import java.awt.Color;

import javax.swing.border.Border;
import javax.swing.JButton;
import java.awt.SystemColor;
import java.awt.Font;
import javax.swing.JScrollPane;

/**
 * GUI della chat con un altro utente
 * @author Gionatha Sturba
 *
 */
public class ChatWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7018723357317188387L;
	protected JTextArea conversationArea;
	protected JTextArea textArea;
	protected JButton btnInviaTextButton;
	protected JButton btnInviaFile;
	
	
	public static final int WIDTH = 370;
	public static final int HEIGHT = 400;
	protected JScrollPane scrollPane;
	protected JScrollPane scrollPane_1;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatWindow frame = new ChatWindow("ciao");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ChatWindow(String title) {
		getContentPane().setBackground(SystemColor.desktop);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100,WIDTH, HEIGHT);
		setResizable(false);
		setTitle(title);
		getContentPane().setLayout(null);
		
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 12, 350, 250);
		getContentPane().add(scrollPane);

		conversationArea = new JTextArea();
		scrollPane.setViewportView(conversationArea);
		conversationArea.setFont(new Font("Dialog", Font.BOLD, 15));
		conversationArea.setBackground(new Color(240, 248, 255));
		conversationArea.setEditable(false);
		conversationArea.setBorder(new LineBorder(new Color(0, 0, 0), 2, true));
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 300, 250, 50);
		getContentPane().add(scrollPane_1);
		
		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		textArea.setFont(new Font("Dialog", Font.PLAIN, 17));
		textArea.setBackground(new Color(240, 248, 255));
		textArea.setBorder(border);
		
		btnInviaTextButton = new JButton("Invia");
		btnInviaTextButton.setBounds(270, 300, 90, 50);
		getContentPane().add(btnInviaTextButton);
		
		btnInviaFile = new JButton("Invia File");
		btnInviaFile.setBounds(10, 276, 100, 20);
		getContentPane().add(btnInviaFile);
		
	}

	public JButton getBtnInviaTextButton() {
		return btnInviaTextButton;
	}

	public JButton getBtnInviaFile() {
		return btnInviaFile;
	}

	public JTextArea getConversationArea() {
		return conversationArea;
	}

	public JTextArea getTextArea() {
		return textArea;
	}
}
