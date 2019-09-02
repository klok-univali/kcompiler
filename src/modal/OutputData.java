/**
 * @author Gabriel Hegler Klok
 * @since 2019/08
 */
package modal;

import java.util.ArrayList;

public class OutputData {
    private static ArrayList<String> valid = new ArrayList<>();
    private static ArrayList<String> invalid = new ArrayList<>();
    
    public static void addOutputValid(String token, int row, int column, String type, int id){
        token = token.substring(token.length()-1, token.length()).equals("\n") ? token.substring(0, token.length()-1) : token;
        valid.add(token + " in row: " + row + ", column: " + column + " of type '" + type + "' | id '" + id + "'");
    }
    
    public static void addOutputInvalid(String token, int row, int column, String type, int id){
        token = token.substring(token.length()-1, token.length()).equals("\n") ? token.substring(0, token.length()-1) : token;
        invalid.add("Error: " + token + " in row: " + row + ", column: " + column + " of type '" + type + "' | id '" + id + "'");
    }
    
    public static void removeLastOutputInvalid(){
        invalid.remove(invalid.size()-1);
    }
    
    public static ArrayList<String> getValid(){
        return valid;
    }
    
    public static ArrayList<String> getInvalid(){
        return invalid;
    }
    
    public static void clean(){
        valid.clear();
        invalid.clear();
    }
}