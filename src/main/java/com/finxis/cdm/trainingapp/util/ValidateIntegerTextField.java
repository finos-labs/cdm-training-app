package com.finxis.cdm.trainingapp.util;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class ValidateIntegerTextField extends JTextField {

    public void processKeyEvent(KeyEvent e) {
        char keyChar = e.getKeyChar();
        if (((keyChar >= '0') && (keyChar <= '9')) ||
                (keyChar == 8) || (keyChar == 127)) {
            super.processKeyEvent(e);
        }
    }
}