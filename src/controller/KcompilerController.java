/**
 * Class KcompilerController
 * 
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
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import modal.OutputData;
import modal.Parser;
import modal.SyntacticParser;
import view.KcompilerView;

public class KcompilerController {
    private String hash;
    private String fullpathFile;
    
    static int BTN_OK_NO = 0;
    static int BTN_OK_NO_CANCEL = 1;
    
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
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile, BTN_OK_NO_CANCEL) ){
            this.hash = generateHash("");
            this.fullpathFile = "";
            return true;
        } 
        return false;
    }
    
    public void functionOpenDocument(JTextArea inputArea){
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile, BTN_OK_NO_CANCEL) ) {
            findDocument(inputArea);
        }
    }
    
    private void findDocument(JTextArea inputArea){
        JFileChooser fileChooser = new JFileChooser(this.fullpathFile);
        FileNameExtensionFilter extensao = new FileNameExtensionFilter("TXT (*.txt)", "txt");
        fileChooser.setFileFilter(extensao);
        boolean ok = true;
        
        while(ok){
            if ( fileChooser.showOpenDialog(fileChooser) == JFileChooser.APPROVE_OPTION ) {
                File file = fileChooser.getSelectedFile();
                String filename = file.getAbsolutePath();
                
                if ( filename.substring(filename.length()-4).equalsIgnoreCase(".txt") ) {
                    inputArea.setText(readFile(file.getAbsolutePath()));
                    this.hash = generateHash(inputArea.getText());
                    this.fullpathFile = file.getAbsolutePath();
                    ok = false;
                } else {
                    KcompilerView.alertFileExtension();
                }
            } else {
                ok = false;
            }
        }
    }
    
    public void functionSave(JTextArea inputArea){
        writeFile(inputArea.getText(), this.fullpathFile);
        this.hash = generateHash(inputArea.getText());
    }
    
    public boolean functionSaveAs(JTextArea inputArea){
        boolean salved = true;
        boolean ok = true;
        JFileChooser fileChooser = new JFileChooser(this.fullpathFile);
        FileNameExtensionFilter extensao = new FileNameExtensionFilter("TXT (*.txt)", "txt");
        fileChooser.setFileFilter(extensao);
        
        if (! this.fullpathFile.isEmpty()) {
            fileChooser.setSelectedFile(new File(this.fullpathFile));
        }
         
        try {
            while (ok) {   
                if ( fileChooser.showSaveDialog(fileChooser) == JFileChooser.APPROVE_OPTION ) {
                    File file = fileChooser.getSelectedFile();
                    this.fullpathFile = file.getAbsolutePath();

                    if ( ! this.fullpathFile.substring(this.fullpathFile.length()-4).equalsIgnoreCase(".txt") ) {
                        this.fullpathFile += ".txt";
                    }

                    if ( new File(this.fullpathFile).exists() ) {
                        switch(KcompilerView.replaceFile()){
                            case 0: //Yes replace
                                writeFile(inputArea.getText(), this.fullpathFile);
                                this.hash = generateHash(inputArea.getText());
                                ok = false;
                                break;
                            
                            case 1: //No replace
                                break;
                                
                            case 2: //Cancel replace
                                ok = false;
                                salved = false;
                                break;
                        }
                    } else {
                        writeFile(inputArea.getText(), this.fullpathFile);
                        this.hash = generateHash(inputArea.getText());
                        ok = false;
                    }
                } else { //Cancel modal files
                    ok = false;
                    salved = false;
                }
            }
        } catch (Exception e) {}
        return salved;
    }
    
    public void functionExit(JTextArea inputArea, int typeBtn){     
        if ( verifyModifications(inputArea, this.hash, this.fullpathFile, typeBtn) ) {
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
                    SyntacticParser syntactic = SyntacticParser.getInstance();
                    outputTmp += syntactic.analyze(inputArea.getText()) + '\n';
                } else {
                    outputTmp += "Compilation failed! See errors:\n";
                    for (String string : tokensInvalid) {
                        outputTmp += string + '\n';
                    }
                }
            } catch (Exception e) {}
        }
        OutputData.clean();
        return outputTmp;
    }
    
    public static String generateHash(String s){
        String hashTmp = "";
        
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(),0,s.length());
            hashTmp = new BigInteger(1,m.digest()).toString(16);
        } catch (Exception e) {}
        
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
        } catch (Exception e) {}  
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

        } catch (Exception e) {}
        return content;
    }
    
    private boolean verifyModifications(JTextArea inputArea, String hash, String fullpathFile, int typeBtn){
        boolean response = false;
        
        if ( hash.equals(generateHash(inputArea.getText())) ) {
            response = true;
        } else {
            switch( KcompilerView.modalSaveOrCancel(typeBtn) ){
                case 0: //Yes
                    if ( fullpathFile.isEmpty() ) {
                        response = functionSaveAs(inputArea);
                    } else {
                        functionSave(inputArea);
                        response = true;
                    }
                    break;
                    
                case 1: //No
                    response = true;
                    break;
            } 
        }
        return response;
    }   
}