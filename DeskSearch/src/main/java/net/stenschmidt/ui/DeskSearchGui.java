package net.stenschmidt.ui;

import java.util.List;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;

import net.stenschmidt.desksearch.SearchResult;

public class DeskSearchGui {

    JTable table = null;

    public void show(List<SearchResult> searchResults, String windowCaption) {

        final JFrame frame = new JFrame(windowCaption);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        final TableModel tableModel = new SearchResultTableModel(searchResults);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        //table.getColumnModel().getColumn(0).setWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(700);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    handleSelectionEvent(e);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setSize(750, 200);
        frame.setVisible(true);
    }

    protected void handleSelectionEvent(ListSelectionEvent e) throws IOException {
        int column = 1;
        int row = table.getSelectedRow();
        String value = table.getModel().getValueAt(row, column).toString();

        if (java.awt.Desktop.isDesktopSupported()) {
            try {
                File myFile = new File(value);
                java.awt.Desktop.getDesktop().open(myFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
