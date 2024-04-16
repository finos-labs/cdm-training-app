package com.finxis.cdm.trainingapp.ui;

import com.finxis.cdm.trainingapp.CdmTradingDemoApplication;
import com.finxis.cdm.trainingapp.TradeTableModel;

import java.awt.*;

import javax.swing.*;

public class TradingPanel extends JPanel{

    private final TradePanel tradePanel;
    private final TradeEntryPanel tradeEntryPanel;
    public TradingPanel(TradeTableModel tradeTableModel, CdmTradingDemoApplication application) {

        setName("Trade Training App");

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        tradeEntryPanel = new TradeEntryPanel(tradeTableModel, application);
        constraints.insets = new Insets(0, 0, 5, 0);
        add(tradeEntryPanel, constraints);

        constraints.gridx++;
        constraints.weighty = 10;

        JTabbedPane tabbedPane = new JTabbedPane();
        tradePanel = new TradePanel(tradeTableModel, application);

        tabbedPane.add("Trades", tradePanel);
        add(tabbedPane, constraints);


    }



}
