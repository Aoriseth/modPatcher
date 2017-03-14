/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patcher;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import java.io.File;

/**
 *
 * @author lenna
 */



public class FXMLDocumentController implements Initializable {    
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handlePatch(ActionEvent event) throws IOException {
        System.out.println("You clicked me!");
        label.setText("Patching!");
        //new ftp client
        FTPClient ftp = new FTPClient();
        //Try to connect
        ftp.connect("www.cockx.me");
        ftp.enterLocalPassiveMode();
        ftp.login("anonymous", "");
        FTPFile[] files = ftp.listFiles("/mods");
        for (FTPFile file : files) {
            System.out.println(file.getName());
        }
        ftp.disconnect();
        
        System.out.println("=====Local Files======");
        File dir = new File(".");
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
            ftp.disconnect();
        }
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    
}
