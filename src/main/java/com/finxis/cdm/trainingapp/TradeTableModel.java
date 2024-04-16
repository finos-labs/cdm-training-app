package com.finxis.cdm.trainingapp;

import javax.swing.table.AbstractTableModel;
import java.util.HashMap;

public class TradeTableModel extends AbstractTableModel {

    private final static int TICKER = 0;
    private final static int QUANTITY = 1;
    private final static int SIDE = 2;
    private final static int PRICE = 3;

    private final HashMap<Integer, Trade> rowToTrade;
    private final HashMap<String, Integer> idToRow;
    private final HashMap<String, Trade> idToTrade;

    private final String[] headers;

    public TradeTableModel() {
        rowToTrade = new HashMap<>();
        idToRow = new HashMap<>();
        idToTrade = new HashMap<>();

        headers = new String[]
                  {"Symbol", "Quantity", "Side", "Price"};
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public void addTrade(Trade trade) {
        int row = rowToTrade.size();

        rowToTrade.put(row, trade);
        idToRow.put(trade.getID(), row);
        idToTrade.put(trade.getID(), trade);

        fireTableRowsInserted(row, row);
    }

    public void updateTrade(Trade trade, String id) {

        if (!id.equals(trade.getID())) {
            String originalID = trade.getID();
            trade.setID(id);
            replaceTrade(trade, originalID);
            return;
        }

        Integer row = idToRow.get(trade.getID());
        if (row == null)
            return;
        fireTableRowsUpdated(row, row);
    }

    public void replaceTrade(Trade trade, String originalID) {

        Integer row = idToRow.get(originalID);
        if (row == null)
            return;

        rowToTrade.put(row, trade);
        idToRow.put(trade.getID(), row);
        idToTrade.put(trade.getID(), trade);

        fireTableRowsUpdated(row, row);
    }

    public void addID(Trade trade, String newID) {
        idToTrade.put(newID, trade);
    }

    public Trade getTrade(String id) {
        return idToTrade.get(id);
    }

    public Trade getTrade(int row) {
        return rowToTrade.get(row);
    }

    public void setValueAt(Object value, int rowIndex, int columnIndex) { }

    public Class<String> getColumnClass(int columnIndex) {
        return String.class;
    }

    public int getRowCount() {
        return rowToTrade.size();
    }

    public int getColumnCount() {
        return headers.length;
    }

    public String getColumnName(int columnIndex) {
        return headers[columnIndex];
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Trade trade = rowToTrade.get(rowIndex);
        switch (columnIndex) {
        case TICKER:
            return trade.getSymbol();
        case QUANTITY:
            return trade.getQuantity();
        case SIDE:
            return trade.getSide();
        case PRICE:
            return trade.getPrice();
        }
        return "";
    }
}
