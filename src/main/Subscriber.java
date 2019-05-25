package main;

import communication.Observer;
import communication.Subject;

public class Subscriber implements Observer{
	private String name;
	private Subject topic;
	
	public Subscriber(String nm){
		this.name=nm;
	}
	
	@Override
	public void update() {
		String msg = (String) topic.getUpdate(this);
		if(msg == null){
			System.out.println(name+":: No new message");
		}else
		System.out.println(name+":: Consuming message::"+msg);
	}

	
	@Override
	public void setSubject(Subject sub) {
		this.topic=sub;
	}
}
