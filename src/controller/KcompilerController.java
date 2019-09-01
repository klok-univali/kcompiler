/**
 * @author Gabriel Hegler Klok
 * @since 2019/08
 */
package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import modal.OutputData;
import modal.Parser;
import view.KcompilerView;

public class KcompilerController {
    private String hash;
    private String fullpathFile;
    
    public KcompilerController() {
        this.hash = generateHash("");
        this.fullpathFile = "";
    }
    
    public String getHash(){
        return this.hash;
    }
    
    public String getFullpathFile(){
        return this.fullpathFile;
    }
    
    public boolean functionNewDocument(JTextArea inputArea){
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile) ){
            this.hash = generateHash("");
            this.fullpathFile = "";
            return true;
        } 
        return false;
    }
    
    public void functionOpenDocument(JTextArea inputArea){
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile) ) {
            findDocument(inputArea);
        }
    }
    
    private void findDocument(JTextArea inputArea){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter extensao = new FileNameExtensionFilter("TXT (*.txt)", "txt");
        fileChooser.setFileFilter(extensao);
        
        if ( fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION ) {
            File file = fileChooser.getSelectedFile();

            inputArea.setText(readFile(file.getAbsolutePath()));
            this.hash = generateHash(inputArea.getText());
            this.fullpathFile = file.getAbsolutePath();
        }
    }
    
    public void functionSave(JTextArea inputArea){
        writeFile(inputArea.getText(), this.fullpathFile);
    }
    
    public void functionSaveAs(JTextArea inputArea){
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter extensao = new FileNameExtensionFilter("TXT (*.txt)", "txt");
        fileChooser.setFileFilter(extensao);
        
        if (! this.fullpathFile.isEmpty()) {
            fileChooser.setSelectedFile(new File(this.fullpathFile));
        }
        
        try {
            
            if ( fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                this.fullpathFile = file.getAbsolutePath();
                
                if (! this.fullpathFile.contains(".txt")) {
                    this.fullpathFile += ".txt";
                }
                
                if ( new File(this.fullpathFile).exists() ) {
                    if ( KcompilerView.replaceFile() == 0 ) {
                        writeFile(inputArea.getText(), this.fullpathFile);
                    } else {
                        this.fullpathFile = "";
                    }
                } else {
                    writeFile(inputArea.getText(), this.fullpathFile);
                }
            }
            
        } catch (Exception e) {
        }
    }
    
    public void functionExit(JTextArea inputArea){     
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile) ) {
            System.exit(0);
        }
    }
        
    public String functionCompile(JTextArea inputArea, JTextPane outputArea){
        String outputTmp = outputArea.getText();
        
        if ("".equals(inputArea.getText())) {
            outputTmp += "Unable to compile an empty file.\n";
        } else {
            Parser parser = new Parser(new StringReader(inputArea.getText()));
            ArrayList<String> tokensValid = new ArrayList<>();
            ArrayList<String> tokensInvalid = new ArrayList<>();
            
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
                
                outputTmp += "---------------------------------------------------------------------------------------------------\n";
                outputTmp += "Compilation started - " + dateFormat.format(new Date()) + "\n\n";
                
                parser.execute();
                
                tokensValid = OutputData.getValid();
                tokensInvalid = OutputData.getInvalid();
                
                if (tokensInvalid.isEmpty()) {
                    outputTmp += "Compilation success! See tokens:\n";
                    for (String string : tokensValid) {
                        outputTmp += string + '\n';
                    }
                } else { 
                    outputTmp += "Compilation failed! See errors:\n";
                    for (String string : tokensInvalid) {
                        outputTmp += string + '\n';
                    }
                }
                
                OutputData.clean();
            } catch (Exception e) {
            }
        }
        
        return outputTmp;
    }
    
    public static String generateHash(String s){
        String hashTmp = "";
        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hashTmp = new BigInteger(1,m.digest()).toString(16);
        } catch (Exception e) {
        }
        
        return hashTmp;
    }
    
    
    private void writeFile(String input, String fullpathFile){
        try {
            BufferedWriter buff_escrita = new BufferedWriter(new FileWriter(fullpathFile));
            Scanner leitor = new Scanner(input);
            while (leitor.hasNextLine()) {
                buff_escrita.write(leitor.nextLine());
                buff_escrita.newLine();
            }
            buff_escrita.close();
        } catch (Exception e) {
        }  
    }
    
    private String readFile(String filename){
        String content = "";
        
        try {
            BufferedReader buff_Leitor = new BufferedReader(new FileReader(filename));
        
            StringBuilder string_builder = new StringBuilder();
            String linha = buff_Leitor.readLine();

            while (linha != null) {
                string_builder.append(linha);
                string_builder.append("\n");
                linha = buff_Leitor.readLine();
            }
            string_builder.delete(string_builder.length()-1, string_builder.length());
            content = string_builder.toString();

        } catch (Exception e) {
        }
        
        return content;
    }
    
    private boolean verifyModifications(JTextArea inputArea, String hash, String fullpathFile){
        boolean response = false;
        
        if ( hash.equals(generateHash(inputArea.getText())) ) {
            response = true;
        } else {
            switch( KcompilerView.modalSaveOrCancel() ){
                case 0: //Yes
                    if ( fullpathFile.isEmpty() ) {
                        functionSaveAs(inputArea);
                    } else {
                        functionSave(inputArea);
                    }
                    response = true;
                    break;
                    
                case 1: //No
                    response = true;
                    break;
            } 
        }
        
        return response;
    }
    
}
