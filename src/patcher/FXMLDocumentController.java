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
import javafx.scene.control.TextField;

/**
 *
 * @author lenna
 */



public class FXMLDocumentController implements Initializable {    
    
    @FXML
    private Label label;
    
    @FXML
    private TextField serverURL;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    @FXML
    private void handlePatch(ActionEvent event) throws IOException {
        System.out.println("You clicked me!");
        //split url into server and folderstucture
        String[] uri = serverURL.getText().split("/",2);
        String serverAddress = uri[0];
        String folder = "/"+uri[1];
        
        FTPClient ftp = ftpConnect(serverAddress);
        label.setText("Connecting to "+serverAddress);
        if (ftp==null){
            label.setText("Connection failed: incorrect url");
            return;
        }
        
        label.setText("Connection success: "+folder);
        
        FTPFile[] files = ftp.listFiles("/");
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

    private FTPClient ftpConnect(String url) throws IOException {
        //new ftp client
        FTPClient ftp = new FTPClient();
        //Try to connect
        try{
            ftp.connect(url);
        }
        catch(IOException object){
        return null;
        }
        
        ftp.enterLocalPassiveMode();
        ftp.login("anonymous", "");
        return ftp;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    
    
}
