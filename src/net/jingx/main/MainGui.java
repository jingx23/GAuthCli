package net.jingx.main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class MainGui {

	private static String PATH_GAUTH_SECKEY = System.getProperty("user.home") + "/" + ".gauth" + "/";
	private static String SEC_FILENAME = "sec";
	private static Color COLOR_DEACTIVATE = OSUtils.isMacOSX() ? SystemColor.window : new Color(238,238,238);

	private JFrame frmPinGenerator;
	private JPasswordField txtSecretKey;
	private JTextField txtPin;
	private JButton btnSaveSecret;
	private static final int MIN_KEY_BYTES = 10;
	private JLabel lblTimersec;
	private TimerNextKey timerNextKey;

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
		int dialogHeight = OSUtils.isMacOSX() ? 93 : 120;
		String secret = readSecret();
		frmPinGenerator = new JFrame();
		frmPinGenerator.setIconImage(Toolkit.getDefaultToolkit().getImage(MainGui.class.getResource("/net/jingx/icons/authenticator250_16x16x32.png")));
		frmPinGenerator.setTitle("Pin Generator");
		frmPinGenerator.setBounds(100, 100, 286, dialogHeight);
		frmPinGenerator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPinGenerator.getContentPane().setLayout(null);

		JLabel lblSecretKey = new JLabel("Secret Key:");
		lblSecretKey.setBounds(25, 12, 68, 16);
		frmPinGenerator.getContentPane().add(lblSecretKey);

		txtSecretKey = new JPasswordField();
		txtSecretKey.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				String secret = new String(txtSecretKey.getPassword());
				if(secret != null && secret.length() >= MIN_KEY_BYTES){
					timerNextKey.restart();
					generateAndDisplayPin();
				}else{
					txtPin.setText("Key is too short");
					timerNextKey.stop();
				}
			}
		});
		txtSecretKey.setBounds(95, 6, 134, 28);
		frmPinGenerator.getContentPane().add(txtSecretKey);
		txtSecretKey.setColumns(10);

		JLabel lblPin = new JLabel("Pin:");
		lblPin.setBounds(70, 46, 23, 16);
		frmPinGenerator.getContentPane().add(lblPin);

		txtPin = new JTextField();
		txtPin.setBackground(COLOR_DEACTIVATE);
		txtPin.setEditable(false);
		txtPin.setBounds(95, 40, 134, 28);
		txtPin.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		frmPinGenerator.getContentPane().add(txtPin);
		txtPin.setColumns(10);

		btnSaveSecret = new JButton("");
		btnSaveSecret.setBorder(javax.swing.BorderFactory.createEmptyBorder());
		btnSaveSecret.setIcon(new ImageIcon(MainGui.class.getResource("/net/jingx/icons/disk.png")));
		btnSaveSecret.setBounds(229, 12, 16, 16);
		btnSaveSecret.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String secret = new String(txtSecretKey.getPassword());
				if(secret!=null && !secret.equals("")){
					saveSecret(secret);
				}
			}
		});
		frmPinGenerator.getContentPane().add(btnSaveSecret);

		lblTimersec = new JLabel("");
		lblTimersec.setBounds(230, 46, 43, 16);
		frmPinGenerator.getContentPane().add(lblTimersec);

		timerNextKey = new TimerNextKey(lblTimersec, makeTimerAction());
		if(secret!=null){
			txtSecretKey.setText(secret);
			timerNextKey.start();
		}
		generateAndDisplayPin();
		frmPinGenerator.addWindowFocusListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		    	txtPin.requestFocusInWindow();
		    	copyPinToClipboard();
		    }
		});

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

	private ActionListener makeTimerAction() {
		ActionListener a = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				generateAndDisplayPin();
			}
		};
		return a;
	}

	private void generateAndDisplayPin(){
		String secret = new String(txtSecretKey.getPassword());
		if(secret!=null && !secret.equals("")){
			if(secret.length() < MIN_KEY_BYTES){
				txtPin.setText("Key is too short");
				if(timerNextKey.isRunning()){
					timerNextKey.stop();
				}
			}else{
				txtPin.setText(Main.computePin(secret, null));
				copyPinToClipboard();
			}
		}
	}

	private void copyPinToClipboard() {
		StringSelection ss = new StringSelection(txtPin.getText());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}
}
