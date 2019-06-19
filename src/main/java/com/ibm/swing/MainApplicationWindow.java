package com.ibm.swing;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.ibm.release.projects.QuirlDatenpflege;
import com.ibm.release.projects.QuirlImport;

public class MainApplicationWindow {
	public static final String Q_DATENPFLEGE = "qDatenpflege";
	public static final String Q_IMPORT = "qImport";
	
	private JFrame frmQuirlReleaseApp;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainApplicationWindow window = new MainApplicationWindow();
					window.frmQuirlReleaseApp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainApplicationWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmQuirlReleaseApp = new JFrame();
		frmQuirlReleaseApp.getContentPane().setFont(new Font("Tahoma", Font.PLAIN, 17));
		frmQuirlReleaseApp.setTitle("Quirl Release App");
		frmQuirlReleaseApp.setBounds(100, 100, 708, 365);
		frmQuirlReleaseApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQuirlReleaseApp.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Release Creation");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel.setBounds(152, 16, 132, 20);
		frmQuirlReleaseApp.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Choose release type");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_1.setBounds(15, 60, 173, 20);
		frmQuirlReleaseApp.getContentPane().add(lblNewLabel_1);
		
		ButtonGroup releaseType = new ButtonGroup();
		
		JRadioButton qImport = new JRadioButton("QuirlImport");
		qImport.setSelected(true);
		qImport.setFont(new Font("Tahoma", Font.PLAIN, 17));
		qImport.setBounds(15, 103, 117, 29);
		qImport.setActionCommand(Q_IMPORT);
		frmQuirlReleaseApp.getContentPane().add(qImport);
		
		JRadioButton qDatenpflege = new JRadioButton("QuirlDatenpflege");
		qDatenpflege.setFont(new Font("Tahoma", Font.PLAIN, 17));
		qDatenpflege.setBounds(15, 140, 183, 29);
		qDatenpflege.setActionCommand(Q_DATENPFLEGE);
		frmQuirlReleaseApp.getContentPane().add(qDatenpflege);
		
		releaseType.add(qDatenpflege);
		releaseType.add(qImport);
		releaseType.getSelection().getActionCommand();
		
		TextField releaseName = new TextField();
		releaseName.setFont(new Font("Tahoma", Font.PLAIN, 14));
		releaseName.setBounds(251, 101, 420, 29);
		frmQuirlReleaseApp.getContentPane().add(releaseName);
		
		JLabel lblNewLabel_2 = new JLabel("Release Name");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblNewLabel_2.setBounds(398, 60, 132, 20);
		frmQuirlReleaseApp.getContentPane().add(lblNewLabel_2);
		
		JLabel lblProcessMessages = new JLabel("Process Messages");
		lblProcessMessages.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblProcessMessages.setBounds(398, 139, 159, 30);
		frmQuirlReleaseApp.getContentPane().add(lblProcessMessages);
		
		JTextArea processText = new JTextArea();
		processText.setEditable(false);
		processText.setForeground(Color.DARK_GRAY);
		processText.setLineWrap(true);
		processText.setBounds(251, 169, 220, 135);
		processText.setAutoscrolls(true);
		frmQuirlReleaseApp.getContentPane().add(processText);
		PrintStream printStream = new PrintStream(new CustomOutputStream(processText));
		System.setOut(printStream);
		System.setErr(printStream);
		
		JScrollPane scrollPane = new JScrollPane(processText);
		scrollPane.setBounds(251, 167, 420, 137);
		frmQuirlReleaseApp.getContentPane().add(scrollPane);
		
		Button button = new Button("Create Release");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				processText.setText("");
			
				if(!validateFields()) {
					return;
				}
				
				String releaseTypeText = releaseType.getSelection().getActionCommand();
				String releaseNameText = releaseName.getText();
				
				try {
					boolean withSuccess = false;
					switch (releaseTypeText) {
					case Q_DATENPFLEGE:
						withSuccess = new QuirlDatenpflege(releaseNameText).createRelease();
						break;
					case Q_IMPORT:
						withSuccess = new QuirlImport(releaseNameText).createRelease();
						break;
					}

					if (withSuccess)
						System.out.println("Well done!");
					else {
						System.out.println("Something went a little ... mnaah. Please check and fix");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}

			private boolean validateFields() {
				return !releaseName.getText().isEmpty();
			}
		});
		
		button.setFont(new Font("Tahoma", Font.PLAIN, 17));
		button.setBounds(47, 258, 141, 46);
		frmQuirlReleaseApp.getContentPane().add(button);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalGlue.setBounds(242, 258, -86, 1);
		frmQuirlReleaseApp.getContentPane().add(horizontalGlue);
	}
}
