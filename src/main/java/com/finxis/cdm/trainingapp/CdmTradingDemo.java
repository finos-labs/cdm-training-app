package com.finxis.cdm.trainingapp;

import com.finxis.cdm.trainingapp.ui.TradingFrame;
import com.rosetta.model.lib.process.PostProcessStep;

import javax.swing.*;

public class CdmTradingDemo {

    private PostProcessStep keyProcessor = null;
    private JFrame frame = null;
    private static CdmTradingDemo trainingApp;

    public CdmTradingDemo(String[] args) throws Exception {

        TradeTableModel tradeTableModel = tradeTableModel();
        CdmTradingDemoApplication application = application();

        frame = new TradingFrame(tradeTableModel, application);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    protected TradeTableModel tradeTableModel() {
        return new TradeTableModel();
    }
    protected CdmTradingDemoApplication application() {
        return new CdmTradingDemoApplication();
    }

    public JFrame getFrame() {
        return frame;
    }

    public static CdmTradingDemo get() {
        return trainingApp;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("CDM Application Demo");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        trainingApp = new CdmTradingDemo(args);


    }

}
