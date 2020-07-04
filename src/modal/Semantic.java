/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modal;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 *
 * @author klok
 */
public class Semantic {
    private final List<Symbol> tabelaSimbolos = new ArrayList<>();
    private final List<EnumType> tabelaTipoEnumerado = new ArrayList<>();
    private final List<Instruction> instrucoes = new ArrayList();
    private final ArrayList listaAtributos = new ArrayList();
    private int ponteiro = 0;
    private int tipo;
    private String contexto = "";
    private int vi = 0;
    private int tvi = 0;
    private int vp = 0;
    private int vt = 0;
    private String saida = "";
    private boolean variavelIndexada = false;
    private final Stack pilhaDesvios = new Stack();
    private String variavelTmp = "";
    private int constanteTmp = 0;
    private String atr1 = null;
    private String atr2 = null;
    private int categoriaTmp = 0;
    private final ArrayList<String> erros = new ArrayList<>();
    
    public void Action01(String token){
        tabelaSimbolos.add( new Symbol(token,"0", "-", "-") );
    }
    
    public void Action02(String token){
        instrucoes.add( new Instruction(ponteiro, "STP", "0") );
        printTabelas();
    }
    
    public void Action03(String token){
        if ( existeSimbolo(token) || existeTipoEnumerado(token)) {
            System.out.println("Identificador já declarado [ " + token + " ]");
            erros.add("Identificador já declarado [ " + token + " ]");
        } else {
            tabelaTipoEnumerado.add( new EnumType(token, new ArrayList() ) );
        }
    }
    
    public void Action04(String token){
        if ( existeSimbolo(token) || existeTipoEnumerado(token) || existeIdentificadoresConstantesTipoEnumerado(token)  ) {
            System.out.println("Identificador já declarado [" + token + "]");
            erros.add("Identificador já declarado [" + token + "]");
        } else {
           tabelaTipoEnumerado.get(tabelaTipoEnumerado.size()-1).setConstante(token);
        }
    }
    
    public void Action05(){
        contexto = "as constant";
        vi = 0;
        tvi = 0;
    }
    
    public void Action06(){
      int n = vp + vi;
      int size = tabelaSimbolos.size();
// tem erro aqui
      for(int i = size-1; i >= size-n; i--) {
          tabelaSimbolos.get(i).setCategoria(Integer.toString(tipo));
      }
      vp = vp + tvi;
      
      switch(tipo) {
        case 1 | 5:
          instrucoes.add( new Instruction(ponteiro, "ALI", Integer.toString(vp)) );
          ponteiro++;
          break;
          
        case 2 | 6:
          instrucoes.add( new Instruction(ponteiro, "ALR", Integer.toString(vp)) );
          ponteiro++;
          break;
          
        case 3 | 7:
          instrucoes.add( new Instruction(ponteiro, "ALS", Integer.toString(vp)) );
          ponteiro++;
          break;
          
        case 4:
          instrucoes.add( new Instruction(ponteiro, "ALB", Integer.toString(vp)) );
          ponteiro++;
          break;
      }
      
      switch(tipo) {
        case 1 | 2 | 3 | 4:
          vp = 0;
          vi = 0;
          tvi = 0;
          break;
      }
    }

    public void Action07(String token){
      switch(tipo) {
        case 5:
          instrucoes.add( new Instruction(ponteiro, "LDI", token) );
          ponteiro++;
          break;
        
        case 6:
          instrucoes.add( new Instruction(ponteiro, "LDR", token) );
          ponteiro++;
          break;

        case 7:
          instrucoes.add( new Instruction(ponteiro, "LDS", token) );
          ponteiro++;
          break;
      }

      instrucoes.add( new Instruction(ponteiro, "STC", Integer.toString(vp)) );
      ponteiro++;
      vp = 0;
    }
    
    public void Action08(String token){
        contexto = "as variable";
    }
    
    public void Action09(String token){
        if ( existeSimbolo(token) || existeTipoEnumerado(token) || existeIdentificadoresConstantesTipoEnumerado(token) ) { 
            // ERRO identificador já declarado
            System.out.println("Identificador já declarado [" + token + "]");
            erros.add("Identificador já declarado [" + token + "]");
        } else {
            vt++;
            vp++;
            tabelaSimbolos.add( new Symbol(token, "0", Integer.toString(vt), "-") );
        }
    }
    
    public void Action10(String token){
      switch(contexto) {
        case "as variable":
          if ( existeSimbolo(token) || existeTipoEnumerado(token) || existeIdentificadoresConstantesTipoEnumerado(token) ) {
            System.out.println("Identificador já declarado [" + token + "]");
            erros.add("Identificador já declarado [" + token + "]");
          } else {
            variavelIndexada = false;
            variavelTmp = token;
          }
          break;

        case "atribuicao":
          variavelIndexada = false;
          variavelTmp = token;
          break;
          
        case "entrada dados":
          variavelIndexada = false;
          variavelTmp = token;
          break;
      }
    }

    public void Action11(String token){
      switch(contexto) {
        case "as variable":
          if (! variavelIndexada) {
            vt++;
            vp++;
            tabelaSimbolos.add( new Symbol(variavelTmp, "?", Integer.toString(vt),"-") );
          } else {
            vi++;
            tvi += constanteTmp;
            tabelaSimbolos.add( new Symbol(variavelTmp, "?", Integer.toString(vt + 1), Integer.toString(constanteTmp)) );
            vt += constanteTmp;
          }
          break;

        case "atribuicao":
          if ( existeSimbolo(token) && identificadorDeVariavel(token) ) {
            if (atr2.equals("-")) {
              if (! variavelIndexada) {
                listaAtributos.add(atr1);
              } else {
                System.out.println("Identificador de variável não indexada");
                erros.add("Identificador de variável não indexada");
              }
            } else {
              if (variavelIndexada) {
                listaAtributos.add( Integer.toString(Integer.parseInt(atr1) + constanteTmp - 1) );
              } else {
                System.out.println("Identificador de variável indexada exige índice");
                erros.add("Identificador de variável indexada exige índice");
              }
            }
          } else {
            System.out.println("Identificador não declarado ou identificador de programa, de constante ou de tipo enumerado");
            erros.add("Identificador não declarado ou identificador de programa, de constante ou de tipo enumerado");
          }
          break;

        case "entrada dados":
          if ( existeSimbolo(token) && identificadorDeVariavel(token) ) {
            if (atr2.equals("-")) {
              if ( ! variavelIndexada ) {
                instrucoes.add( new Instruction(ponteiro, "REA", Integer.toString(categoriaTmp)) );
                ponteiro++;
                instrucoes.add( new Instruction(ponteiro, "STR", atr1) );
                ponteiro++;
              } else {
                System.out.println("Identificador de variável não indexada");
                erros.add("Identificador de variável não indexada");
              }
            } else {
              if (variavelIndexada) {
                instrucoes.add( new Instruction(ponteiro, "REA", Integer.toString(categoriaTmp)) );
                ponteiro++;
                instrucoes.add( new Instruction(ponteiro, "STR", Integer.toString(Integer.parseInt(atr1) + constanteTmp - 1)) );
                ponteiro++;
              } else {
                System.out.println("Identificador de variável indexada exige índice");
                erros.add("Identificador de variável indexada exige índice");
              }
            }
          } else {
            System.out.println("Identificador não declarado ou identificador de programa, de constante ou de tipo enumerado");
            erros.add("Identificador não declarado ou identificador de programa, de constante ou de tipo enumerado");
          }
          break;
      }
    }

    public void Action12(String token){
        constanteTmp = Integer.parseInt(token);
        variavelIndexada = true;
    }
    
    // tipo inteiro
    public void Action13(){
      if(contexto.equals("as variable")){
        tipo = 1;
      } else {
        tipo = 5;
      }
    }
    
    // tipo real
    public void Action14(){
      if(contexto.equals("as variable")){
        tipo = 2;
      } else {
        tipo = 6;
      }
      
    }
    
    // tipo literal
    public void Action15(){      
      if(contexto.equals("as variable")){
        tipo = 3;
      } else {
        tipo = 7;
      }
    }
    
    // tipo lógico
    public void Action16(){
      if(contexto.equals("as variable")){
        tipo = 4;
      } else {
         System.out.println("Tipo invalido para constante");
         erros.add("Tipo invalido para constante");
      }
    }
    
    // tipo enumerado
    public void Action17(){
      if(contexto.equals("as variable")){
        tipo = 1;
      } else {
         System.out.println("Tipo invalido para constante");
         erros.add("Tipo invalido para constante");
      }
    }
    
    public void Action18(){
      contexto = "atribuicao";
    }
    
    public void Action19(){
      //erro
      //descobrir como contar a quantidade de atributos armazenados na ação 11
      for(int i = 0; i < listaAtributos.size(); i++) {
        instrucoes.add( new Instruction(ponteiro, "STR", (String) listaAtributos.get(i)) );
        ponteiro++;
      }
    }

    public void Action20(){
      contexto = "entrada dados";
    } 

    public void Action21(){
      saida = "write all this";
    }
    
    public void Action22(){
      saida = "write this";
    }

    public void Action23(){
      instrucoes.add( new Instruction(ponteiro, "WRT", "0") );
      ponteiro++;
    }

    public void Action24(String token){
      if ( existeSimbolo(token) && identificadorDeVariavelOuConstante(token) ) {
        variavelIndexada = false;
        variavelTmp = token;
      } else {
        System.out.println("Identificador não declarado, identificador de programa ou de tipo enumerado");
        erros.add("Identificador não declarado, identificador de programa ou de tipo enumerado");
      }
    }

    public void Action25(String token){
      for(Symbol ident : tabelaSimbolos) {
        if (ident.getIdentificador().equals(token)){
          atr1 = ident.getAtributo1();
          atr2 = ident.getAtributo2();
        }
      }
      if(!variavelIndexada){
        if(atr2.equals("-")){
          if(saida.equals("write all this")){
            instrucoes.add(new Instruction(ponteiro,"LDS", variavelTmp + " = "));
            ponteiro++;
            instrucoes.add(new Instruction(ponteiro,"WRT","0"));
            ponteiro++;
          }
          instrucoes.add(new Instruction(ponteiro,"LDV",atr1));
          ponteiro++;
        } else {
          System.out.println("Identificador era de uma variavel indexada, mas não foi mandado indice");
          erros.add("Identificador era de uma variavel indexada, mas não foi mandado indice");
        }        
      } else if (atr2.equals("-")) {
        if(saida.equals("write all this")){
            instrucoes.add(new Instruction(ponteiro,"LDS", variavelTmp + " = "));
            ponteiro++;
            instrucoes.add(new Instruction(ponteiro,"WRT","0"));
            ponteiro++;
          }
          instrucoes.add(new Instruction(ponteiro,"LDV",Integer.toString(Integer.parseInt(atr1) + constanteTmp - 1)));
          ponteiro++;
        } else {
          System.out.println("Identificador era de uma variavel nao indexada, ou de uma constante");
          erros.add("Identificador era de uma variavel nao indexada, ou de uma constante");
        }        
    }
    
    public void Action26(String token){
        instrucoes.add(new Instruction(ponteiro, "LDI", token));
        ponteiro++;
    }
    
    public void Action27(String token){
        instrucoes.add(new Instruction(ponteiro, "LDR", token));
        ponteiro++;
    }
    
    public void Action28(String token){
        instrucoes.add(new Instruction(ponteiro, "LDS", token));
        ponteiro++;
    }
    
    public void Action29(){
      int n = (int) pilhaDesvios.pop();
      int indice = recuperaIndiceInstrucao(n);
      instrucoes.get(indice).setEndereco(Integer.toString(ponteiro));
      //atualiza endereço da instrução
      //temp.set(2, Integer.toString(ponteiro));
      //atualizaInstrucao(temp);

    }
    
    public void Action30(){
      instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
      ponteiro++;
      pilhaDesvios.push(ponteiro - 1);
    }
    
    public void Action31(){
      instrucoes.add(new Instruction(ponteiro, "JMT", "?"));
      ponteiro++;
      pilhaDesvios.push(ponteiro - 1);
    }
    
    public void Action32(String token){
      int n = (int) pilhaDesvios.pop();
      int indice = recuperaIndiceInstrucao(n);
      instrucoes.get(indice).setEndereco(Integer.toString(ponteiro+1));
      instrucoes.add(new Instruction(ponteiro, "JMP", "?"));
      ponteiro++;
      pilhaDesvios.push(ponteiro - 1);
    }
    
    public void Action33(String token){
      pilhaDesvios.push(ponteiro);
    }
    
    public void Action34(String token){
      instrucoes.add(new Instruction(ponteiro, "JMF", "?"));
      ponteiro++;
      pilhaDesvios.push(ponteiro - 1);
    }
    
    public void Action35(String token){
      int n = (int) pilhaDesvios.pop();
      int indice = recuperaIndiceInstrucao(n);
      instrucoes.get(indice).setEndereco(Integer.toString(ponteiro+1));
      n = (int) pilhaDesvios.pop();
      instrucoes.add(new Instruction(ponteiro, "JMP", Integer.toString(n)));
      ponteiro++;    
    }
    
    public void Action36(){
        instrucoes.add(new Instruction(ponteiro, "EQL", "0"));
        ponteiro++;
    }
    
    public void Action37(){
        instrucoes.add(new Instruction(ponteiro, "DIF", "0"));
        ponteiro++;
    }
    
    public void Action38(){
        instrucoes.add(new Instruction(ponteiro, "SMR", "0"));
        ponteiro++;
    }
    
    public void Action39(){
        instrucoes.add(new Instruction(ponteiro, "BGR", "0"));
        ponteiro++;
    }
    
    public void Action40(){
        instrucoes.add(new Instruction(ponteiro, "SME", "0"));
        ponteiro++;
    }
    
    public void Action41(){
        instrucoes.add(new Instruction(ponteiro, "BGE", "0"));
        ponteiro++;
    }
    
    public void Action42(){
        instrucoes.add(new Instruction(ponteiro, "ADD", "0"));
        ponteiro++;
    }
    
    public void Action43(){
        instrucoes.add(new Instruction(ponteiro, "SUB", "0"));
        ponteiro++;
    }
    
    public void Action44(){
      instrucoes.add(new Instruction(ponteiro, "OR", "0"));
      ponteiro++;
    }
    
    public void Action45(){
        instrucoes.add(new Instruction(ponteiro, "MUL", "0"));
        ponteiro++;
    }
    
    public void Action46(){
        instrucoes.add(new Instruction(ponteiro, "DIV", "0"));
        ponteiro++;
    }
    
    public void Action47(){
        instrucoes.add(new Instruction(ponteiro, "DIV", "0"));
        ponteiro++;
    }
    
    public void Action48(){
        instrucoes.add(new Instruction(ponteiro, "MOD", "0"));
        ponteiro++;
    }
    
    public void Action49(){
      instrucoes.add(new Instruction(ponteiro, "AND", "0"));
        ponteiro++;
    }
    
    public void Action50(){
      instrucoes.add(new Instruction(ponteiro, "POW", "0"));
      ponteiro++;
  }
    
  public void Action51(){
    for(Symbol ident : tabelaSimbolos) {
      if (ident.getIdentificador().equals(variavelTmp)){
        atr1 = ident.getAtributo1();
        atr2 = ident.getAtributo2();
      }
    }
    if(!variavelIndexada){
      if(atr2.equals("-")){
        instrucoes.add(new Instruction(ponteiro,"LDV",atr1));
        ponteiro++;
      } else {
        System.out.println("Identificador de variável indexada exige índice");
        erros.add("Identificador de variável indexada exige índice");
      }
    } else if (atr2.equals("-")) {
        instrucoes.add(new Instruction(ponteiro,"LDV",Integer.toString(Integer.parseInt(atr1) + constanteTmp - 1)));
        ponteiro++;
    } else {
        System.out.println("Identificador de constante ou de variável não indexada ");
        erros.add("Identificador de constante ou de variável não indexada");
    }        
  }
    
  public void Action52(){
      instrucoes.add(new Instruction(ponteiro, "LDB", "TRUE"));
      ponteiro++;
  }
  
  public void Action53(){
      instrucoes.add(new Instruction(ponteiro, "LDB", "FALSE"));
      ponteiro++;
  }
  
  public void Action54(){
      instrucoes.add(new Instruction(ponteiro, "NOT", "0"));
      ponteiro++;
  }
  /*
  private ArrayList montaTupla(String token, int categoria, String atributo1, String atributo2){
      ArrayList tupla = new ArrayList();
      tupla.add(token);
      tupla.add(categoria);
      tupla.add(atributo1);
      tupla.add(atributo2);
      
      return tupla;
  }
  
  private ArrayList montaInstrucao(int ponteiro, String instrucao, String param){
      ArrayList cmd = new ArrayList();
      cmd.add(ponteiro);
      cmd.add(instrucao);
      cmd.add(param);
      
      return cmd;
  }
*/
  
  private int recuperaIndiceInstrucao(int ponteiro){
    int n = 0;
    for(Instruction inst : instrucoes){
      if ( inst.getPonteiro() == ponteiro ){
        return n;
      }
    }
    return -1;
  }

 /* private void atualizaInstrucao(ArrayList instAtualizada){
    int n = (int) instAtualizada.get(0);
    for(ArrayList inst : instrucoes){
      int pt = (int) inst.get(0);
      if( pt == n ){
        inst = instAtualizada;
      }
    }
  }

  private ArrayList newTipoEnumerado(String token, ArrayList identificadores) {
      ArrayList tmp = new ArrayList();
      tmp.add(token);
      tmp.add(identificadores);
      return tmp;
  }

  */
  private boolean existeSimbolo(String token) {
    if (! tabelaSimbolos.isEmpty()) {
        for (Symbol simbolo : tabelaSimbolos) {
            if (simbolo.getIdentificador().equals(token)) {
                return true;
            }
        }
    }
    return false;
  }
  
  private boolean existeTipoEnumerado(String token) {
      if (! tabelaTipoEnumerado.isEmpty()) {
        for (EnumType tipoEnumerado : tabelaTipoEnumerado) {
            if (tipoEnumerado.getIdentificador().equals(token)) {
                return true;
            }
        }
      }
      return false;
  }
  
  private boolean existeIdentificadoresConstantesTipoEnumerado(String token) {
      if (! tabelaTipoEnumerado.isEmpty()) {
        EnumType tipoEnum = tabelaTipoEnumerado.get(tabelaTipoEnumerado.size()-1);
        ArrayList idEnum = tipoEnum.getConstantes();

        if (idEnum.size() > 0) {
            for (Object object : idEnum) {
                if (object.equals(token) ) {
                    return true;
                }
            }
        }
     }
      
     return false;
  }
/*
  private void setCategoriaSimbolo(int tipo, int i) {
    ArrayList tmp = tabelaSimbolos.get(i);
    tmp.set(1, tipo);
    tabelaSimbolos.set(i, tmp);
  }
*/
  private boolean identificadorDeVariavel(String token) {
      int n;
    for(Symbol simbolo : tabelaSimbolos) {
        n = Integer.parseInt(simbolo.getCategoria());
      if (simbolo.getIdentificador().equals(token) && (n >= 1 || n <= 4)) {
        atr1 =  simbolo.getAtributo1();//(String) ident.get(2);
        atr2 = simbolo.getAtributo2();//(String) ident.get(3);
        categoriaTmp = n;
        return true;
      }
    }
    return false;
  }

  private boolean identificadorDeVariavelOuConstante(String token) {
      int n;
    for(Symbol ident : tabelaSimbolos) {
        n = Integer.parseInt(ident.getCategoria());
      if (ident.getIdentificador().equals(token) && n != 0 ) {
        atr1 = ident.getAtributo1();
        atr2 = ident.getAtributo1();
        categoriaTmp = n;
        return true;
      }
    }
    return false;
  }
  
  public ArrayList<String> getErros(){
      return erros;
  }
  public List<Symbol> getSimbolos(){
      return tabelaSimbolos;
  }
  public List<Instruction> getInstructions(){
      return instrucoes;
  }
  public List<EnumType> getEnums(){
      return tabelaTipoEnumerado;
  }

    private void printTabelas() {
        for(Symbol s : getSimbolos()){
            System.out.println(s.getIdentificador() +" | "+ s.getCategoria() +
                    " | "+s.getAtributo1() +" | "+ s.getAtributo2());
            }
        for(Instruction i : getInstructions()){
            System.out.println(i.getPonteiro() +" | "+ i.getInstrucao() +
                    " | "+i.getEndereco());
        }
    }
}