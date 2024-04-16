package com.finxis.cdm.trainingapp;


public class Trade implements Cloneable {
    private TradeSymbol symbol = null;
    private int quantity = 0;
    private double price = 0;

    private TradeSide side = TradeSide.BUY;

    private boolean canceled = false;
    private boolean isNew = true;
    private String message = null;
    private String ID = null;
    private String originalID = null;
    private static int nextID = 1;

    public Trade() {
        ID = generateID();
    }

    public Trade(String ID) {
        this.ID = ID;
    }

    public Object clone() {
        try {
            Trade trade= (Trade) super.clone();
            trade.setOriginalID(getID());
            trade.setID(trade.generateID());
            return trade;
        } catch (CloneNotSupportedException e) {}
        return null;
    }

    public String generateID() {
        return Long.toString(System.currentTimeMillis() + (nextID++));
    }


    public TradeSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(TradeSymbol symbol) {
        this.symbol = symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public TradeSide getSide() {
        return side;
    }

    public void setSide(TradeSide side) {
        this.side = side;
    }


    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public boolean getCanceled() {
        return canceled;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setOriginalID(String originalID) {
        this.originalID = originalID;
    }

    public String getOriginalID() {
        return originalID;
    }
}
