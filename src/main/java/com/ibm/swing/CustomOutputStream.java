package com.ibm.swing;

import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;


/**
 * Will put all the errors and prints to the given textArea
 * @author AdrielIclodean
 *
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea textArea;

    public CustomOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        textArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        textArea.setCaretPosition(textArea.getDocument().getLength());
        // keeps the textArea up to date
        textArea.update(textArea.getGraphics());
        
    }
    
    
}