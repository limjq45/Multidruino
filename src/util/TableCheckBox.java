package util;
import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

public class TableCheckBox extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private Object[][] data;
    
    
    public TableCheckBox(Object[][] data) {
        Object[] columnNames = {"COM PORT", "Connect", "Allow Send", "Allow Recv"};
        this.data = data;

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
     
        table = new JTable(model) {
        	 
            private static final long serialVersionUID = 1L;
            public boolean isCellEditable(int row, int column) {   
            	if(column == 0)
                return false;      
            	else
            		return true;
            };
            /*@Override
            public Class getColumnClass(int column) {
            return getValueAt(0, column).getClass();
            }*/
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    case 2:
                        return Boolean.class;
                    case 3:
                        return Boolean.class;
                    default:
                        return Boolean.class;
                }
            }
        };
        
       
        table.setFocusable(false);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
        
    }
   
    public JTable getTable() {
    	return table;
    }
    public ListSelectionModel getSelectionModel() {
    	return table.getSelectionModel();
    }
    public TableModel getTableModel() {
    	return table.getModel();
    }

   
}