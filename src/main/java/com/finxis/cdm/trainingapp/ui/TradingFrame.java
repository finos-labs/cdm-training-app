package com.finxis.cdm.trainingapp.ui;

import com.finxis.cdm.trainingapp.CdmTradingDemoApplication;
import com.finxis.cdm.trainingapp.TradeTableModel;

import javax.swing.*;
import java.awt.*;

public class TradingFrame extends JFrame {

    public TradingFrame(TradeTableModel tradeTableModel, CdmTradingDemoApplication application){
        super();
        setTitle("CDM Demo Trade Booking App");
        setSize(600,400);

        createMenuBar(application);
        getContentPane().add(new TradingPanel(tradeTableModel, application), BorderLayout.CENTER);
        setVisible(true);
    }

    private void createMenuBar(final CdmTradingDemoApplication application) {
        JMenuBar menubar = new JMenuBar();
    }
}
