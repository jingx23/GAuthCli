package net.jingx.main;

import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class MainGui {

	private static String PATH_GAUTH_SECKEY = System.getProperty("user.home") + "/" + ".gauth";
	private static String SEC_FILENAME = "sec";
	
	private JFrame frmPinGenerator;
	private JTextField txtSecretKey;
	private JTextField txtPin;
	private JButton btnSaveSecret;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGui window = new MainGui();
					window.frmPinGenerator.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		String secret = readSecret();
		frmPinGenerator = new JFrame();
		frmPinGenerator.setTitle("Pin Generator");
		frmPinGenerator.setBounds(100, 100, 286, 186);
		frmPinGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPinGenerator.getContentPane().setLayout(null);
		
		JLabel lblSecretKey = new JLabel("Secret Key:");
		lblSecretKey.setBounds(26, 34, 68, 16);
		frmPinGenerator.getContentPane().add(lblSecretKey);
		
		txtSecretKey = new JTextField();
		txtSecretKey.setBounds(96, 28, 134, 28);
		frmPinGenerator.getContentPane().add(txtSecretKey);
		txtSecretKey.setColumns(10);
		
		JLabel lblPin = new JLabel("Pin:");
		lblPin.setBounds(71, 68, 23, 16);
		frmPinGenerator.getContentPane().add(lblPin);

		txtPin = new JTextField();
		txtPin.setBackground(SystemColor.window);
		txtPin.setEditable(false);
		txtPin.setBounds(96, 62, 134, 28);
		txtPin.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		frmPinGenerator.getContentPane().add(txtPin);
		txtPin.setColumns(10);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String secret = txtSecretKey.getText();
				if(secret!=null && !secret.equals("")){
					txtPin.setText(Main.computePin(txtSecretKey.getText(), null));	
				}
			}
		});
		btnGenerate.setBounds(101, 100, 117, 29);
		frmPinGenerator.getContentPane().add(btnGenerate);
		
		btnSaveSecret = new JButton("");
		btnSaveSecret.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		btnSaveSecret.setIcon(new ImageIcon(MainGui.class.getResource("/net/jingx/icons/disk.png")));
		btnSaveSecret.setBounds(228, 29, 29, 29);
		btnSaveSecret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String secret = txtSecretKey.getText();
				if(secret!=null && !secret.equals("")){
					saveSecret(secret);	
				}
			}
		});
		frmPinGenerator.getContentPane().add(btnSaveSecret);
		if(secret!=null){
			txtSecretKey.setText(secret);
			btnGenerate.doClick();
		}

	}
	
	private String readSecret(){
		try{
			String secret = null;
			File fSec = new File(PATH_GAUTH_SECKEY + SEC_FILENAME);
			if(fSec.exists()){
				 FileReader fr = new FileReader(fSec);
				 BufferedReader br = new BufferedReader(fr);
				 secret = br.readLine();
				 br.close();
			}
			return secret;
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private void saveSecret(String secret){
		try{
			File fDirGAuth = new File(PATH_GAUTH_SECKEY);
			fDirGAuth.mkdirs();
			FileWriter fw = new FileWriter(PATH_GAUTH_SECKEY + SEC_FILENAME);
			fw.write(secret);
			fw.flush();
			fw.close();
		}catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void doSetVisible() {
		frmPinGenerator.setVisible(true);
	}
}
