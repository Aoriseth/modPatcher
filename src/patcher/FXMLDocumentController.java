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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

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
    private TextField locdir;
    
    String serverAddress ;
    String folder;
    String localdir;
    
    @FXML
    private void handleDirchoose(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        label.setText("trying to open file dialog");

    }
    
    
    
    @FXML
    private void handlePatch(ActionEvent event) throws IOException {
        System.out.println("You clicked me!");
        //split url into server and folderstucture
        String[] uri = serverURL.getText().split("/",2);
        serverAddress = uri[0];
        folder = "/"+uri[1];
        
        FTPClient ftp = ftpConnect(serverAddress);
        label.setText("Connecting to "+serverAddress);
        if (ftp==null){
            label.setText("Connection failed: incorrect url");
            return;
        }
        
        label.setText("Connection success: "+folder);
        
        System.out.println("=====Local Files======");
        localdir = locdir.getText();
        File dir = new File(localdir);
        
        File[] filesList = getLocalFiles(dir);
        ArrayList<String> missing = getMissing(ftp, filesList);
        updateFiles(missing, ftp);
            
        ftp.disconnect();
    }

    private File[] getLocalFiles(File dir) {
        File[] filesList = dir.listFiles();
        for (File file : filesList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
        return filesList;
    }

    private ArrayList<String> getMissing(FTPClient ftp, File[] filesList) throws IOException {
        ArrayList<String> missing = new ArrayList<>();
        FTPFile[] files = ftp.listFiles(folder);
        for (FTPFile file : files) {
            boolean found = false;
            for(File test:filesList){
                if(test.getName().equals(file.getName())){
                    found=true;
                }
            }
            if(found==false){
                if(file.isFile()){
                     missing.add(file.getName());
                }
               
            }
        }
        return missing;
    }

    private void updateFiles(ArrayList<String> missing, FTPClient ftp) throws IOException {
        
        System.out.println(missing.size() + " new items found.");
        double step = 0.0;
        if (missing.size() > 0) {
            step = 100.0 / missing.size();
        }

        double progressFill = 0.0;
        progress.setProgress(progressFill);

        for (String item : missing) {
            System.out.println("File missing, downloading: " + item);
            String remoteFile1 = folder + item;
            File downloadFile1 = new File(localdir+"/"+item);
            
            boolean success;
            try (OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1))) {
                success = ftp.retrieveFile(remoteFile1, outputStream1);
            }

            if (success) {
                System.out.println("File " + item + " has been downloaded successfully.");
                label.setText(item +" has been downloaded sucessfully");
                progressFill += step;
                progress.setProgress(progressFill);
                
                
            }
        }
        progress.setProgress(100.0);
        System.out.println("Finished downloading missing files.");
        label.setText("Finished downloading missing files.");
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
