/**
 * @author Gabriel Hegler Klok
 * @since 2019/10
 */
package modal;

import java.io.StringReader;

public class SyntacticParser {
    private static SyntacticParser instance = null;
    
    private SyntacticParser(){}
    
    public static SyntacticParser getInstance(){
        if (instance == null) {
            instance = new SyntacticParser();
        }
        return instance;
    }
    
    public String analyze(String text) {
        Parser parser = new Parser(new StringReader(text));
        StringBuilder output = new StringBuilder();
        try {
            parser.syntaxAnalisys();
            output.append("Success !");
        } catch (Exception e) {
            output.append(e.getMessage());
        }
        return output.toString();
    }
}
