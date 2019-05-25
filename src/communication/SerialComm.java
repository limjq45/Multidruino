package communication;




import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.table.TableModel;

import communication.SerialWriter.SerialSendListener;
import jssc.*;


public class SerialComm implements SerialPortEventListener, ISerialListener, Subject{

    private HashMap<String,SerialPort> serialPortMapper = new HashMap<String,SerialPort>();
    
	/** Default bits per second for COM port. */
	private int baud_rate = 115200;
	List<String> l;

	HashMap<String,Object[]> modelMapper = new HashMap<String,Object[]>();
    private TableModel model;
	
   //For interface
	private List<Observer> observers;
	private String message;
	private boolean changed;
	private final Object MUTEX= new Object();
	
	
	public SerialComm(int baud_rate) {
		this.baud_rate = baud_rate;
		observers=new ArrayList<>();
	}
	

	public boolean write(final String msg) {

		for(int i =0 ; i<modelMapper.size();i++) {
			if((Boolean)modelMapper.get(l.get(i))[0]==true) {
				
					SerialWriter sw = new SerialWriter(l.get(i),serialPortMapper.get(l.get(i)),msg,new SerialSendListener() {	
					//SerialWriter sw = new SerialWriter(l.get(i),outputMapper.get(l.get(i)),msg,new SerialSendListener() {
						 @Override
				            public void onSuccess(String port, String msg1) {
				                //DO your logic here on success
							 postMessage("[SUCC][SEND]["+port+"] "+msg1);
				            }

				            @Override
				            public void onFail(String port, String msg1) {
				               //DO your logic here on fail
				            	postMessage("[FAIL][SEND]["+port+"] "+msg1);
				            }
					});
					Thread thread = new Thread(sw);
			        thread.start();
			        
					
				
			}
		}
		return true;
	}
	
	
	public void setModel(TableModel model) {
		this.model = model;
		modelMapper.clear();

		for(int i = 0; i<model.getRowCount();i++) {
			Object o[] = {model.getValueAt(i, 2),model.getValueAt(i, 3)};
			modelMapper.put((String) model.getValueAt(i, 0), o);
		}
		l = new ArrayList<String>(modelMapper.keySet());
	}
	
	public TableModel getModel() {
		return model;
	}
	
	
	public boolean connect(String port){
		SerialPort serialPort = new SerialPort(port);
		
		try {
		    serialPort.openPort();
		    serialPort.setParams(baud_rate,
		                         SerialPort.DATABITS_8,
		                         SerialPort.STOPBITS_1,
		                         SerialPort.PARITY_NONE);

		    serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | 
		                                  SerialPort.FLOWCONTROL_RTSCTS_OUT);
          
		    serialPort.addEventListener(this, SerialPort.MASK_RXCHAR);
		    serialPortMapper.put(port, serialPort);
		    return true;
		}
		catch (SerialPortException ex) {
		    System.out.println("There are an error on port т: " + ex);
		}
		
		return false;
	}
	
	
	
	public String[] getPortList()
    {
		return SerialPortList.getPortNames();   
    }
	
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close(String portName) {
		  System.out.println("Disconnection called for "+portName);
	        
		
		Thread t=new Thread() {
			
			@Override
			public void run() {
				try {
					serialPortMapper.get(portName).closePort();
				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
		
       
	
	}

	

	@Override
	public void register(Observer obj) {
	   // System.out.println("REGISTERING.."+obj);
		if(obj == null) throw new NullPointerException("Null Observer");
		synchronized (MUTEX) {
		if(!observers.contains(obj)) {observers.add(obj); }//System.out.println("REGISTERED.."+obj);}
		}
	}

	@Override
	public void unregister(Observer obj) {
		synchronized (MUTEX) {
		observers.remove(obj);
		}
	}

	@Override
	public void notifyObservers() {
		
		
		List<Observer> observersLocal = null;
		//synchronization is used to make sure any observer registered after message is received is not notified
		synchronized (this) {
			if (!changed)
				return;
		   // System.out.println(observers);
			observersLocal = new ArrayList<>(observers);
			this.changed=false;
		}
		for (Observer obj : observersLocal) {
			//System.out.println("UPDATING"+obj);
			obj.update();
		}

	}

	@Override
	public Object getUpdate(Observer obj) {
		//System.out.println("Called from "+obj+" msg is "+this.message);
		return this.message;
	}
	
	//method to post message to the topic
	public void postMessage(String msg){
		//System.out.println("Message Posted to Topic:"+msg);
		this.message=msg;
		this.changed=true;
		notifyObservers();
	}

	

	@Override
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		String src = (event.getPortName()).trim();
		
		 if(event.isRXCHAR() && event.getEventValue() > 0) {
	            try {
	                String receivedData = serialPortMapper.get(src).readString(event.getEventValue());
	                receivedData = receivedData.trim();
	                if(receivedData.length()>0) {
	            	if((Boolean)modelMapper.get(src)[1]==true) { //If recv is ticked
    			         System.out.println("[RECV]["+src+"] "+receivedData);
		                postMessage("[RECV]["+src+"] "+receivedData);
	    				}
	                }
	               
	            }
	            catch (SerialPortException ex) {
	                System.out.println("Error in receiving string from COM-port: " + ex);
	            }
	        }
		
	}

	
}

class SerialWriter extends Thread implements Runnable
{
   private SerialPort os;
   private String msg;
   private String name;
   private SerialSendListener listener;
   
   public SerialWriter(String name, SerialPort os, String msg, SerialSendListener listener) {
		this.os = os;
		this.name = name;
		this.msg = msg;
		this.listener = listener;
	}
	    public void run() 
	    {  
	    	try {
	    		System.out.println("Writting "+msg+" to "+name);
				os.writeBytes(new String(msg+"\n").getBytes());
	    		//os.writeString(msg);
				 if (listener != null)
					 listener.onSuccess(name,msg);
				//System.out.println("Written "+msg+" to "+name);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				if (listener != null)
					listener.onFail(name,msg);
				e.printStackTrace();
			}
	    }
	    public String getPortName() {
	    	return name;
	    }
	    public void setListner(SerialSendListener listener) {
	        this.listener = listener;
	    }
	    /**
	     * here is your observer class
	     */
	    
	    public interface SerialSendListener {

	        void onSuccess(String port, String msg);

	        void onFail(String port, String msg);
	    }
	    
	    
	    
	    
}

