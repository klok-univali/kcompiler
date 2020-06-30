/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modal;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author klok
 */
public class Semantic {
    private ArrayList<ArrayList> tabelaSimbolos = new ArrayList<>();
    private ArrayList<ArrayList> tabelaTipoEnumerados = new ArrayList<>();
    private ArrayList<ArrayList> instrucoes = new ArrayList();
    private int ponteiro = 0;
    private String contexto = "";
    private int vi = 0;
    private int tvi = 0;
    private int vp = 0;
    private int vt = 0;
    private boolean variavelIndexada = false;
    private Stack pilhaDesvios = new Stack();
    
    public void Action01(String token){
        tabelaSimbolos.add( getTupla(token, 0, "-", "-") );
    }
    
    public void Action02(String token){
        instrucoes.add( getInstrucao(ponteiro, "STP", "0") );
    }
    
    public void Action03(String token){
        if ( existeTabelaSimbolos(token) || existeTabelaTipoEnumerado(token) ) {
            // ERRO identificador já declarado
        } else {
            tabelaTipoEnumerados.add( newTipoEnumerado(token, new ArrayList<>() ) );
        }
    }
    
    public void Action04(String token){
        if ( existeTabelaSimbolos(token) || existeTabelaTipoEnumerado(token) ) {
            // ERRO identificador já declarado
        } else {
            ArrayList tmp = tabelaTipoEnumerados.get(tabelaTipoEnumerados.size()-1);
            ArrayList tmp2 = (ArrayList) tmp.get(1);
            tmp2.add(token);
            tmp.set(1, tmp2);
            tabelaTipoEnumerados.set(tabelaTipoEnumerados.size()-1, tmp);
        }
    }
    
    public void Action05(String token){
        contexto = "as constant";
        vi = 0;
        tvi = 0;
    }
    
    public void Action06(String token){}
    public void Action07(String token){}
    
    public void Action08(String token){
        contexto = "as variable";
    }
    
    public void Action09(String token){
        if ( existeTabelaSimbolos(token) || existeTabelaTipoEnumerado(token) ) { //implementar busca em lista de identificadores de constantes de tipo enumerado
            // ERRO identificador já declarado
        } else {
            vt++;
            vp++;
            tabelaSimbolos.add( getTupla(token, 0, Integer.toString(vt), "-") );
        }
    }
    
    public void Action10(String token){}
    public void Action11(String token){}
    public void Action12(String token){}
    public void Action13(String token){}
    public void Action14(String token){}
    public void Action15(String token){}
    public void Action16(String token){}
    public void Action17(String token){}
    public void Action18(String token){}
    public void Action19(String token){}
    public void Action20(String token){}
    public void Action21(String token){}
    public void Action22(String token){}
    public void Action23(String token){}
    public void Action24(String token){}
    public void Action25(String token){}
    
    public void Action26(String token){
        instrucoes.add(getInstrucao(ponteiro, "LDI", token));
        ponteiro++;
    }
    
    public void Action27(String token){
        instrucoes.add(getInstrucao(ponteiro, "LDR", token));
        ponteiro++;
    }
    
    public void Action28(String token){
        instrucoes.add(getInstrucao(ponteiro, "LDS", token));
        ponteiro++;
    }
    
    public void Action29(String token){}
    public void Action30(String token){}
    public void Action31(String token){}
    public void Action32(String token){}
    public void Action33(String token){}
    public void Action34(String token){}
    public void Action35(String token){}
    
    public void Action36(){
        instrucoes.add(getInstrucao(ponteiro, "EQL", "0"));
        ponteiro++;
    }
    
    public void Action37(){
        instrucoes.add(getInstrucao(ponteiro, "DIF", "0"));
        ponteiro++;
    }
    
    public void Action38(){
        instrucoes.add(getInstrucao(ponteiro, "SMR", "0"));
        ponteiro++;
    }
    
    public void Action39(){
        instrucoes.add(getInstrucao(ponteiro, "BGR", "0"));
        ponteiro++;
    }
    
    public void Action40(){
        instrucoes.add(getInstrucao(ponteiro, "SME", "0"));
        ponteiro++;
    }
    
    public void Action41(){
        instrucoes.add(getInstrucao(ponteiro, "BGE", "0"));
        ponteiro++;
    }
    
    public void Action42(){
        instrucoes.add(getInstrucao(ponteiro, "ADD", "0"));
        ponteiro++;
    }
    
    public void Action43(){
        instrucoes.add(getInstrucao(ponteiro, "SUB", "0"));
        ponteiro++;
    }
    
    public void Action44(){}
    
    public void Action45(){
        instrucoes.add(getInstrucao(ponteiro, "MUL", "0"));
        ponteiro++;
    }
    
    public void Action46(){
        instrucoes.add(getInstrucao(ponteiro, "DIV", "0"));
        ponteiro++;
    }
    
    public void Action47(){
        instrucoes.add(getInstrucao(ponteiro, "DIV", "0"));
        ponteiro++;
    }
    
    public void Action48(){
        instrucoes.add(getInstrucao(ponteiro, "MOD", "0"));
        ponteiro++;
    }
    
    public void Action49(){}
    
    public void Action50(){
        instrucoes.add(getInstrucao(ponteiro, "POW", "0"));
        ponteiro++;
    }
    
    public void Action51(){}
    
    public void Action52(){
        instrucoes.add(getInstrucao(ponteiro, "LDB", "TRUE"));
        ponteiro++;
    }
    
    public void Action53(){
        instrucoes.add(getInstrucao(ponteiro, "LDB", "FALSE"));
        ponteiro++;
    }
    
    public void Action54(){
        instrucoes.add(getInstrucao(ponteiro, "NOT", "0"));
        ponteiro++;
    }
    
    private ArrayList getTupla(String token, int cmd, String atributo1, String atributo2){
        ArrayList tupla = new ArrayList();
        tupla.add(token);
        tupla.add(cmd);
        tupla.add(atributo1);
        tupla.add(atributo2);
        
        return tupla;
    }
    
    private ArrayList getInstrucao(int ponteiro, String instrucao, String param){
        ArrayList cmd = new ArrayList();
        cmd.add(ponteiro);
        cmd.add(instrucao);
        cmd.add(param);
        
        return cmd;
    }
    
    private ArrayList newTipoEnumerado(String token, ArrayList identificadores) {
        ArrayList tmp = new ArrayList();
        tmp.add(token);
        tmp.add(identificadores);
        return tmp;
    }
    
    private boolean existeTabelaSimbolos(String token) {
        for (ArrayList tabelaSimbolo : tabelaSimbolos) {
            if (tabelaSimbolo.get(0).equals(token)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean existeTabelaTipoEnumerado(String token) {
        for (ArrayList tabelaTipoEnumerado : tabelaTipoEnumerados) {
            if (tabelaTipoEnumerado.get(0).equals(token)) {
                return true;
            }
        }
        return false;
    }
}
