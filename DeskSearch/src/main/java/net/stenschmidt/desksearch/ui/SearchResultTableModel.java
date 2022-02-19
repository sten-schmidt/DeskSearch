package net.stenschmidt.desksearch.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import net.stenschmidt.desksearch.SearchResult;

public final class SearchResultTableModel extends AbstractTableModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String[] COLUMN_NAMES = { "ID", "PATH" };

    private static final Class<?>[] COLUMN_CLASSES = { String.class, String.class };

    private final List<SearchResult> searchResults;

    public SearchResultTableModel(final List<SearchResult> searchResults) {
        // eigentlich wird eine tiefe Kopie benöigt, um Read-only zu sein
        this.searchResults = new ArrayList<>(searchResults);
    }

    @Override
    public int getRowCount() {
        return searchResults.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final SearchResult searchResult = searchResults.get(rowIndex);

        if (columnIndex == 0)
            return searchResult.getId();
        else if (columnIndex == 1)
            return searchResult.getPath();

        throw new IllegalArgumentException("Invalid columIndex " + columnIndex);
    }

    @Override
    public String getColumnName(final int columnIndex) {
        return COLUMN_NAMES[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        return COLUMN_CLASSES[columnIndex];
    }
}
