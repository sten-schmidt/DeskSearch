package net.stenschmidt.desksearch.ui;

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
import java.awt.event.*;

public class DeskSearchGui {

    JTable table = null;

    public void show(List<SearchResult> searchResults, String windowCaption) {

        final JFrame frame = new JFrame(windowCaption);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        final TableModel tableModel = new SearchResultTableModel(searchResults);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(35);
        // table.getColumnModel().getColumn(0).setWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(35);
        table.getColumnModel().getColumn(1).setPreferredWidth(700);

        ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    //handleSelectionEvent(e);
                }
            }
        });

//      protected void handleSelectionEvent(ListSelectionEvent e) throws IOException {
//      openDocument();
//  }

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openDocument();
                }
            }
        });

        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        frame.setSize(1024, 400);
        frame.setVisible(true);
    }

    void openDocument() {
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
