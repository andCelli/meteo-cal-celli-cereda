/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.polimi.cellicereda.meteocal.businesslogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author stefano
 */
public class URLConnectionReader {
    
    public static String getText(String address) throws IOException {
        StringBuilder contents = new StringBuilder();
        BufferedReader br = null;
        
        try {
            URL url = new URL(address);
            br = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = "";
            while (line != null) {
                line = br.readLine();
                contents.append(line).append("\n");
            }
        } finally {
            close(br);
        }
        
        return contents.toString();
    }
    
    private static void close(Reader br) {
        try {
            if (br != null) {
                br.close();
            }
        } catch (Exception e) {
            Logger.getLogger(URLConnectionReader.class.getName()).log(Level.SEVERE, "Unable to close connection", e);
        }
    }
}
