package com.finxis.cdm.trainingapp.ui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.finxis.cdm.trainingapp.CdmTradingDemoApplication;
import com.finxis.cdm.trainingapp.TradeTableModel;

public class TradePanel extends JPanel {


    private JTable tradeTable = null;
    public TradePanel(TradeTableModel tradeTableModel, CdmTradingDemoApplication application) {


        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;


        tradeTable = new TradeTable(tradeTableModel, application);
        add(new JScrollPane(tradeTable), constraints);
    }

    public JTable tradeTable() {
        return tradeTable;
    }
}
