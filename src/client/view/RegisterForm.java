package client.view;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

import java.awt.Font;
import java.awt.Image;

import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JSeparator;


/**
 * GUI della form di registrazione
 * @author Gionatha Sturba
 *
 */
public class RegisterForm extends JFrame 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1214889789655181279L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField ConfirmpasswordField;
	private JButton btnTornaALogin;
	private JButton btnInvia;
	private JComboBox<String> comboBox;

	public RegisterForm() 
	{
		initWindowContent();
	}
	
	private void initWindowContent() 
	{
		this.setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 19));
		lblPassword.setBounds(50, 192, 165, 35);
		contentPane.add(lblPassword);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 19));
		lblUsername.setBounds(50, 112, 165, 15);
		contentPane.add(lblUsername);
		
		JLabel lblRepeatPassword = new JLabel("Conferma Password");
		lblRepeatPassword.setFont(new Font("Dialog", Font.BOLD, 19));
		lblRepeatPassword.setBounds(50, 287, 268, 25);
		contentPane.add(lblRepeatPassword);
		
		btnInvia = new JButton("Invia");
		btnInvia.setBounds(50, 460, 180, 50);
		contentPane.add(btnInvia);
		
		comboBox = new JComboBox<String>();
		comboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"it", "en", "fr", "de", "es", "ja", "la", "pt", "ro", "ru", "sk", "sl", "sq"}));
		comboBox.setBounds(260, 390, 101, 35);
		contentPane.add(comboBox);
		
		JLabel linguaLabel = new JLabel("Seleziona Lingua");
		linguaLabel.setFont(new Font("Dialog", Font.BOLD, 19));
		linguaLabel.setBounds(50, 390, 216, 29);
		contentPane.add(linguaLabel);
		
		usernameField = new JTextField();
		usernameField.setToolTipText("Inserisci Username");
		usernameField.setForeground(new Color(150, 150, 150));
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
		usernameField.setColumns(10);
		usernameField.setBounds(50, 133, 310, 48);
		contentPane.add(usernameField);
		
		JLabel lblRegistratiSuSocialgossip = new JLabel("Registrati su SocialGossip");
		lblRegistratiSuSocialgossip.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegistratiSuSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 27));
		lblRegistratiSuSocialgossip.setBounds(190, 22, 464, 48);
		contentPane.add(lblRegistratiSuSocialgossip);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(0, 0, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(400, 100, 1, 460);
		contentPane.add(separator);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 17));
		passwordField.setBounds(50, 223, 310, 48);
		contentPane.add(passwordField);
		
		ConfirmpasswordField = new JPasswordField();
		ConfirmpasswordField.setFont(new Font("Dialog", Font.PLAIN, 17));
		ConfirmpasswordField.setBounds(50, 313, 310, 48);
		contentPane.add(ConfirmpasswordField);
		
		JLabel lblSeiGiaRegistrato = new JLabel("Sei gia' registrato?");
		lblSeiGiaRegistrato.setFont(new Font("Dialog", Font.BOLD, 19));
		lblSeiGiaRegistrato.setBounds(495, 421, 257, 33);
		contentPane.add(lblSeiGiaRegistrato);
		
		btnTornaALogin = new JButton("Torna a Login");
		btnTornaALogin.setBounds(503, 460, 180, 50);
		contentPane.add(btnTornaALogin);
		
		
		JLabel image = new JLabel("New label");
		Image icon = new ImageIcon(this.getClass().getResource("/icon.png")).getImage();
		image.setIcon(new ImageIcon(icon));
		image.setBounds(450, 100, 300, 300);
		contentPane.add(image);
	}
	
	public JComboBox<String> getComboBox() {
		return comboBox;
	}
	
	public JButton getBtnInvia() {
		return btnInvia;
	}

	public JTextField getUsernameField() {
		return usernameField;
	}

	public JPasswordField getPasswordField() {
		return passwordField;
	}

	public JPasswordField getConfirmPasswordField() {
		return ConfirmpasswordField;
	}

	public JButton getBtnTornaALogin() {
		return btnTornaALogin;
	}
}
