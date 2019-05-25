package main;

import java.awt.EventQueue;

import communication.ISerialListener;
import communication.SerialComm;
import communication.Subject;

public class Main {

	private ISerialListener sc;
	private Frame frame;
	public static void main(String[] args) {
		//System.out.println(System.getProperty("user.dir"));
          new Main();
          
	}

	public Main() {
		 sc = new SerialComm(115200); //Baud_rate
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
				    frame = new Frame(sc);
					((Subject)sc).register(frame);//Registering myself to subject
					frame.setSubject((Subject)sc);//Set the subject I want to observe..
					
//					//Dummy observer...
//					Observer obj1 = new Subscriber("HIHI");
//					((Subject)sc).register(obj1);
//					obj1.setSubject((Subject)sc);
					

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
	
		
		
		
		
	}

}
