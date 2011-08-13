package net.jingx.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

public class TimerNextKey extends Timer {

	private static final long serialVersionUID = 7015868851197079067L;
	
	private static int counter;
	private JLabel lblTimersec;
	
	public TimerNextKey(JLabel lblTimersec, ActionListener actionToCall) {
		super(1000, new Action(lblTimersec, actionToCall));
		counter = PasscodeGenerator.INTERVAL;
		this.lblTimersec = lblTimersec;
	}

	@Override
	public void stop() {
		super.stop();
		lblTimersec.setText("");
	}
	
	@Override
	public void restart() {
		super.restart();
		counter = PasscodeGenerator.INTERVAL;
	}

	static class Action implements ActionListener {
		private JLabel lblToUpdate;
		private ActionListener actionToCall;
		
		public Action(JLabel lblToUpdate, ActionListener actionToCall){
			this.lblToUpdate = lblToUpdate;
			this.actionToCall = actionToCall;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			counter--;
			lblToUpdate.setText(counter + " sec");
			if(counter==0){
				actionToCall.actionPerformed(e);
				counter = PasscodeGenerator.INTERVAL;
				lblToUpdate.setText(counter + " sec");
			}
			
		}
		
	}
	
}
