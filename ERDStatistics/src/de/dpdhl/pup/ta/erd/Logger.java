/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package de.dpdhl.pup.ta.erd;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TextArea;

/**
 *
 * @author cyqjefe0019
 */
public class Logger {
    
    private static final Logger instance = new Logger();
    
    private Logger(){}
    
    private static TextArea _ta;
    
    public static Logger getInstance() {
        return instance;
    }
    
    /**
     *
     * @param ta
     */
    public void setFXMLTextAreaLogger(TextArea ta) {
        _ta = ta;
    }
    
    public void log(String text) {
        StringBuilder sb = new StringBuilder();
        LocalDateTime ld = LocalDateTime.now();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy.MM.dd HH:mm:ss");
            String sDate = formatter.format( ld );
            sb.append(sDate);
            sb.append(" ");
            sb.append(text);
        } catch (Exception ex) {
            sb.append("Exception in logging : ");
            sb.append(ex.getMessage());
        }
        sb.append("\n");
        // Last Log in first line
        if (_ta != null) {
            String s = _ta.getText();
            _ta.clear();
            _ta.appendText(sb.toString());
            _ta.appendText(s);
            
            _ta.positionCaret(0);
        } else {
            System.out.println("Missing initialization ");
        }
    }
}
