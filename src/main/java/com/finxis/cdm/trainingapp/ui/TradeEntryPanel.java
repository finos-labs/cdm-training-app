package com.finxis.cdm.trainingapp.ui;


import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.finxis.cdm.trainingapp.*;

import com.finxis.cdm.trainingapp.TradeTableModel;
import com.finxis.cdm.trainingapp.util.ValidateDoubleTextField;
import com.finxis.cdm.trainingapp.util.ValidateIntegerTextField;


@SuppressWarnings("unchecked")
public class TradeEntryPanel extends JPanel implements Observer {
    private boolean symbolEntered = false;
    private boolean quantityEntered = false;
    private boolean sessionEntered = false;

    private static CdmTradingDemo trainingApp;

    private final JComboBox symbolComboBox = new JComboBox(TradeSymbol.toArray());
    private final ValidateIntegerTextField quantityTextField = new ValidateIntegerTextField();

    private final JComboBox sideComboBox = new JComboBox(TradeSide.toArray());

    private final ValidateDoubleTextField priceTextField = new ValidateDoubleTextField();

    private final JComboBox sessionComboBox = new JComboBox();

    private final JLabel messageLabel = new JLabel(" ");
    private final JButton submitButton = new JButton("Submit");

    private TradeTableModel tradeTableModel = null;
    private transient CdmTradingDemoApplication application = null;

    private final GridBagConstraints constraints = new GridBagConstraints();

    public TradeEntryPanel(final TradeTableModel tradeTableModel,
                           final CdmTradingDemoApplication application) {
        setName("TradeEntryPanel");
        this.tradeTableModel = tradeTableModel;
        this.application = application;


        setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        setLayout(new GridBagLayout());
        createComponents();
    }

    public void addActionListener(ActionListener listener) {
        submitButton.addActionListener(listener);
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
        if (message == null || message.equals(""))
            messageLabel.setText(" ");
    }

    public void clearMessage() {
        setMessage(null);
    }

    private void createComponents() {
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;

        int x = 0;
        int y = 0;

        add(new JLabel("Side"), x, y);
        add(new JLabel("Symbol"), ++x, y);
        add(new JLabel("Quantity"), ++x, y);
        add(new JLabel("Price"), ++x, y);

        constraints.ipadx = 30;

        sideComboBox.setName("SideComboBox");
        add(sideComboBox, x=0, ++y);
        symbolComboBox.setName("SymbolComboBox");
        add(symbolComboBox, ++x, y);
        constraints.ipadx = 0;
        quantityTextField.setName("QuantityTextField");
        add(quantityTextField, ++x, y);
        priceTextField.setName("PriceTextField");
        add(priceTextField, ++x, y);
        submitButton.setName("Submit Button");
        add(submitButton, ++x, y);

        submitButton.setEnabled(true);
        submitButton.addActionListener(new SubmitListener());
        activateSubmit();
    }

    private JComponent add(JComponent component, int x, int y) {
        constraints.gridx = x;
        constraints.gridy = y;
        add(component, constraints);
        return component;
    }

    private void activateSubmit() {
        //TradeType type = (TradeType) typeComboBox.getSelectedItem();

    }

    public void update(Observable o, Object arg) {

    }

    private class SubmitListener implements ActionListener {


        public void actionPerformed(ActionEvent e) {
            Trade trade = new Trade();
            trade.setSide((TradeSide) sideComboBox.getSelectedItem());
            trade.setSymbol((TradeSymbol) symbolComboBox.getSelectedItem());
            trade.setQuantity(Integer.parseInt(quantityTextField.getText()));
            trade.setPrice(Double.parseDouble(priceTextField.getText()));

            CdmTradingDemoApplication cdmTradingDemoApplication = new CdmTradingDemoApplication();
            try {
                LocalDateTime localDateTime = LocalDateTime.now();
                String defaultLocalTimeZone = "UTC";
                ZonedDateTime TDzonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of(defaultLocalTimeZone));
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSz");
                String TDformattedDateTimeString = TDzonedDateTime.format(formatter);

                String sideStr = trade.getSide().getName().trim().toString();
                String symbolStr = trade.getSymbol().getName().trim().toString();
                String securityIsinStr ="GB00BD0PCK97";
                String securityCcyStr = "GBP";
                String quantityStr = quantityTextField.getText().trim().toString();
                String priceStr = priceTextField.getText().trim().toString();
                String buyerStr = "BANK-A";
                String sellerStr = "BANK-B";
                String executionVenueMicStr = "BDTS";
                String executionTypeMicStr = "OTC";
                String executionTypeStr = "OFF_FACILITY";
                String tradeUTIStr = "CDM20241000";
                String tradeDateStr = TDformattedDateTimeString;

                cdmTradingDemoApplication.executeTradeWorkflow(sideStr, symbolStr, securityIsinStr, securityCcyStr,
                        quantityStr, priceStr, buyerStr, sellerStr, executionVenueMicStr, executionTypeMicStr, executionTypeStr,
                        tradeUTIStr, tradeDateStr);

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            tradeTableModel.addTrade(trade);
            //application.send(trade);
        }
    }
    private class SubmitActivator implements KeyListener, ItemListener {
        public void keyReleased(KeyEvent e) {
            Object obj = e.getSource();
            if (obj == symbolComboBox) {
                symbolEntered = testField(obj);
            } else if (obj == quantityTextField) {
                quantityEntered = testField(obj);
            }
            activateSubmit();
        }

        public void itemStateChanged(ItemEvent e) {
            sessionEntered = sessionComboBox.getSelectedItem() != null;
            activateSubmit();
        }

        private boolean testField(Object o) {
            String value = ((JTextField) o).getText();
            value = value.trim();
            return value.length() > 0;
        }

        public void keyTyped(KeyEvent e) {}

        public void keyPressed(KeyEvent e) {}
    }
}
