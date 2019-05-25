package communication;





import javax.swing.table.TableModel;

public interface ISerialListener  {
     
	
	
	public boolean connect(String port);
	public boolean write(String msg);
	public void close(String portName);
	
	//Getter
	public String[] getPortList();
	public TableModel getModel();
	
	//Setter
	public void setModel(TableModel model);

	

	
	
	
	
}
