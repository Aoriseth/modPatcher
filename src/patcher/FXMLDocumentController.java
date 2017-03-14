/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package patcher;

import java.io.BufferedOutputStream;
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
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
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
    private ProgressBar progress;
    
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
        
        System.out.println("=====Local Files======");
        File dir = new File(".");
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
        
        ArrayList<String> missing = new ArrayList<>();
        
        System.out.println("=====Server Files======");
        FTPFile[] files = ftp.listFiles(folder);
        for (FTPFile file : files) {
            boolean found = false;
            for(File test:filesList){
                if(test.getName().equals(file.getName())){
                    found=true;
                }
            }
            if(found==false){
                missing.add(file.getName());
            }
            
            
        }
      
        updateFiles(missing, folder, ftp);
            
        ftp.disconnect();
    }

    private void updateFiles(ArrayList<String> missing, String folder, FTPClient ftp) throws IOException {
        
        
        
        
        System.out.println(missing.size() + " new items found. Now downloading");
        double step = 0.0;
        if (missing.size() > 0) {
            step = 100.0 / missing.size();
        }

        double progressFill = 0.0;
        progress.setProgress(progressFill);

        for (String item : missing) {
            System.out.println("File missing, downloading: " + item);
            String remoteFile1 = folder + item;
            File downloadFile1 = new File(item);
            boolean success;
            try (OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1))) {
                success = ftp.retrieveFile(remoteFile1, outputStream1);
            }

            if (success) {
                System.out.println("File " + item + " has been downloaded successfully.");
                
                progressFill += step;
                final double run = progressFill;
                Platform.runLater(() -> {
                    progress.setProgress(run);
                });
                
                
            }
        }
        progress.setProgress(100.0);
        System.out.println("Finished downloading missing files.");
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
