package main;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import javax.swing.border.EmptyBorder;


import communication.ISerialListener;
import communication.Observer;
import communication.Subject;
import util.TableCheckBox;

import javax.swing.JFileChooser;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JTextArea;

import java.awt.Font;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileWriter;

import java.awt.event.ItemEvent;



import javax.swing.border.TitledBorder;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultCaret;


import javax.swing.JCheckBox;

import javax.swing.ImageIcon;



public class Frame extends JFrame implements Observer{

	private JPanel contentPane;
	private ISerialListener sc;
	private TableCheckBox tcb;
	private JTable table;


	private JTextField textField;
	private JTextField txtAbc;
	private JScrollPane scrollPane_1;
	private JTextArea textArea;

	private Timer refreshTable; 
	private Subject topic;

	/**
	 * Create the frame.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Frame(ISerialListener sc) {
		
		this.sc = sc;
		
		//Setting up look and feel
		try {UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e1) {e1.printStackTrace();}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 723, 555);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		textField = new JTextField();
		textField.setText("1000");
		textField.setBounds(394, 118, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JButton btnAuto = new JButton("AUTO");
		btnAuto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!sc.write("AUTO")) {
					promptMessage("Write Error","Unable to write");
				}
			}
		});
		btnAuto.setBounds(636, 129, 61, 43);
		contentPane.add(btnAuto);
		
		JButton btnNewButton = new JButton("Send");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!sc.write(Integer.toHexString(Integer.parseInt(textField.getText())))) {
					promptMessage("Write Error","Unable to write");
				}

			}
		});
		btnNewButton.setBounds(500, 117, 89, 23);
		contentPane.add(btnNewButton);
		
		txtAbc = new JTextField();
		txtAbc.setText("XPA");
		txtAbc.setBounds(394, 152, 86, 20);
		contentPane.add(txtAbc);
		txtAbc.setColumns(10);
		
		JButton button = new JButton("Send");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!sc.write(txtAbc.getText())) {
					promptMessage("Write Error","Unable to write");
				}

			}
		});
		button.setBounds(500, 149, 89, 23);
		contentPane.add(button);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, "Setup", TitledBorder.LEFT, TitledBorder.TOP, null, null));
		panel.setBounds(39, 21, 330, 119);
		contentPane.add(panel);
		panel.setLayout(null);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(6, 30, 318, 78);
		scrollPane_1.setBorder(BorderFactory.createEmptyBorder());
		panel.add(scrollPane_1);
		
		
		
		tcb = new TableCheckBox(constructPortListTable(sc.getPortList()));
		scrollPane_1.setViewportView(tcb);
	    table = tcb.getTable();
	    table.getTableHeader().setReorderingAllowed(false);
		JButton btnRefreshPort = new JButton("");
		btnRefreshPort.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				
				updateTable();
			}
		});
	
		table = addListenerToTable(table);
		sc.setModel(table.getModel());
       
		
		btnRefreshPort.setIcon(new ImageIcon(Frame.class.getResource("/com/sun/javafx/scene/web/skin/Redo_16x16_JFX.png")));
		btnRefreshPort.setBounds(295, 11, 23, 23);
		btnRefreshPort.setFocusable(false);
		panel.add(btnRefreshPort);
		
		JPanel consolePanel = new JPanel();
		consolePanel.setBorder(new TitledBorder(null, "Console", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		consolePanel.setBounds(39, 152, 330, 217);
		contentPane.add(consolePanel);
		consolePanel.setLayout(null);
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 19, 310, 162);
		consolePanel.add(scrollPane);
		
	    textArea = new JTextArea();
	    textArea.setEditable(false);
	    textArea.setFont(new Font("Courier New", Font.PLAIN, 13));
	    scrollPane.setViewportView(textArea);
	    DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		 caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    JCheckBox chckbxAutoscroll = new JCheckBox("Autoscroll");
	    chckbxAutoscroll.setBounds(10, 186, 73, 23);
	    consolePanel.add(chckbxAutoscroll);
	    chckbxAutoscroll.setSelected(true);
	    
	    JButton btnSave = new JButton("Log to File");
	    btnSave.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		JFileChooser example = new JFileChooser(){
	    		    @Override
	    		    public void approveSelection(){
	    		        File f = getSelectedFile();
	    		       
	    		        if(f.exists() && getDialogType() == SAVE_DIALOG){
	    		            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
	    		            switch(result){
	    		                case JOptionPane.YES_OPTION:
	    		                    super.approveSelection();
	    		                    try(FileWriter fw = new FileWriter(getSelectedFile())) {
	    			    	    	    fw.write(textArea.getText());
	    			    	    	    fw.close();
	    			    	    	} catch (Exception ex) {
	    			    	            ex.printStackTrace();
	    			    	        }
	    		                  
	    		                    return;
	    		                case JOptionPane.NO_OPTION:
	    		                    return;
	    		                case JOptionPane.CLOSED_OPTION:
	    		                    return;
	    		                case JOptionPane.CANCEL_OPTION:
	    		                    cancelSelection();
	    		                    return;
	    		            }
	    		        }
	    		        super.approveSelection();
	    		    }        
	    		};
	    		example.setCurrentDirectory(new File(System.getProperty("user.home")));
	    		FileFilter filter = new FileNameExtensionFilter("Text Documents (*.txt)","txt");
	    	    example.setFileFilter(filter);
	    	    int retrival = example.showSaveDialog(null);
	    	  
	    	    if (retrival == JFileChooser.APPROVE_OPTION) {
	    	    	String[] choosenFilter=null;
	    	    	String extension = "";
	    	    	String choosenFilter_check = example.getFileFilter().getDescription();
	    	    	if(choosenFilter_check.contains(".")) {
	    	    		choosenFilter = ((FileNameExtensionFilter)example.getFileFilter()).getExtensions(); 
	    	    		
	    	    		extension = choosenFilter[0];
	    	    	}
	    	    	//for(int i =0  ; i<choosenFilter.length;i++)
	    	    		//System.out.println("@@"+choosenFilter[i]);
	    	    	
	    	    	String fileName = example.getSelectedFile().toString();
	    	    	if(!fileName.contains(".")) {
	    	    		fileName = fileName+"."+extension;
	    	    	}
	    	    	System.out.println(example.getSelectedFile());
	    	    	try(FileWriter fw = new FileWriter(new File(fileName))) {
	    	    	    fw.write(textArea.getText());
	    	    	    fw.close();
	    	    	} catch (Exception ex) {
	    	            ex.printStackTrace();
	    	        }
	    	    	
	    	    }
	    	   
	    		
	    	}
	    });
	    btnSave.setBounds(231, 186, 89, 23);
	    consolePanel.add(btnSave);
	    chckbxAutoscroll.addItemListener(new ItemListener() {
	    	public void itemStateChanged(ItemEvent e) {
                if(!chckbxAutoscroll.isSelected())
                	caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
                else {
                	textArea.setCaretPosition(textArea.getDocument().getLength());
                	caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
                }
                	
	    	}
	    });

		setVisible(true);
//		refreshTable =   new Timer(500,actionListener);
//		refreshTable.start();
		
		
	}

	public Object[][] constructPortListTable(String[] portList){
		 Object[][] tablePortList = new Object[portList.length][4];
	        for(int i = 0; i<tablePortList.length;i++) {
	        	
	        	for(int j = 0; j<4;j++) {
	        		if(j == 0)
	        		tablePortList[i][j] = portList[i].toString();
	        		else {
	        			tablePortList[i][j] = false;
	        		}
	        	}
	        }
	        return tablePortList;
	}
	
	private void updateTable() {
		TableModel previousData = sc.getModel();
		tcb = new TableCheckBox(constructPortListTable(sc.getPortList()));
		scrollPane_1.setViewportView(tcb);
		table = tcb.getTable();
	    table.getTableHeader().setReorderingAllowed(false);
		table = addListenerToTable(table);
		
		TableModel tt = table.getModel();
		for(int i = 0; i<tt.getRowCount();i++) {
			
			for(int j = 0; j<previousData.getRowCount();j++) {
				if(tt.getValueAt(i, 0).equals(previousData.getValueAt(j, 0))) {
					boolean connected = (boolean) previousData.getValueAt(j, 1);
					boolean send = (boolean) previousData.getValueAt(j, 2);
					boolean recv = (boolean) previousData.getValueAt(j, 3);
					tt.setValueAt(connected, i, 1);
					tt.setValueAt(send, i, 2);
					tt.setValueAt(recv, i, 3);
				}
			}					
		}
	}
	
	private JTable addListenerToTable(JTable table) {
		
		table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = table.getSelectedRow();
                        int viewCol = table.getSelectedColumn();
                        int modelCol = 0;
                        int modelRow = 0;
                        
                        	if (viewRow < 0) {
                        		
                        	}
                        	else {
                             modelRow = table.convertRowIndexToModel(viewRow);                 
                             modelCol = table.convertColumnIndexToModel(viewCol); 
                            }
                        	
                        	
                        	 TableModel tt = table.getModel();
                             
                             String selected_port = (String)tt.getValueAt(modelRow,0);
                             if((Boolean)tt.getValueAt(modelRow,1)==false  && modelCol == 1) {
                             	
                          		if(sc.connect(selected_port)) {
                          			textArea.append(selected_port+ " Connected\n");
                          			tt.setValueAt(true, modelRow, 1);
                          			tt.setValueAt(true, modelRow, 2);
     	                     		tt.setValueAt(true, modelRow, 3);
                          		}
                          		else {
                          			promptMessage("Connection Error","Unable to connect to "+selected_port);
                          			((DefaultTableModel)table.getModel()).removeRow(modelRow);
                          		}
                             }
                             else if((Boolean)tt.getValueAt(modelRow,1)==true  && modelCol == 1) {
                            	 tt.setValueAt(false, modelRow, 2);
                            	 tt.setValueAt(false, modelRow, 2);
                          		tt.setValueAt(false, modelRow, 3);
                          		sc.close(selected_port);
                          		textArea.append(selected_port+ " Disconnected\n");
                             }
                             sc.setModel(tt);
                             table.clearSelection(); 
                    }
                }
        );
		return table;
	}
	
	
	ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent actionEvent) {
        	updateTable(); 
        }
    };
	
	@Override
	public void update() {
		String msg = (String) topic.getUpdate(this);
		if(msg == null){
			//System.out.println("@@@"+":: No new message");
		}else
			textArea.append(msg+"\n");
		//System.out.println("@@@"+":: Consuming message::"+msg);
	}
	
	@Override
	public void setSubject(Subject sub) {
		this.topic=sub;
	}
	
	
	public void promptMessage(String title, String msg) {
		JOptionPane.showMessageDialog(null, 
                msg, 
                title, 
                JOptionPane.WARNING_MESSAGE);
	}
}
