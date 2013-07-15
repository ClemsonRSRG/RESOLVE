package edu.clemson.cs.r2jt.translation;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Little class that automatically supplements a user supplied string 
 * of translated Java or C code with:
 * 
 * <ul>
 * <li> Newlines after semicolons and left/right curly braces.</li>
 * <li> Appropriate 'tabification' for each level of left/right curly 
 * braces.</li>
 * </ul>
 * 
 * @author Welch D
 */
public class Formatter {

    /**
     * <p>Given a string representing Java or C code without any
     * tabs or newlines, <code>formatCode</code> returns the same
     * string with newlines and indentation added.</p>
     * 
     * @param code A string of Java or C code.
     */
    public static String formatCode(String code) {

        StringBuilder formattedCode = new StringBuilder();
        code = normalizeWhitespace(code);

        // You could also use positive regex lookaheads: (?=;)
        String[] curSplit = code.split("(?<=;)|(?<=\\{)|(?<=\\})");
        Deque<String> tabStack = new LinkedList<String>();

        for (String s : curSplit) {
            if (s.endsWith("}")) {
                tabStack.pop();
            }
            formattedCode.append(writeTabs(tabStack).toString());
            formattedCode.append(s).append("\n");
            if (s.endsWith("{")) {
                tabStack.push("\t");
            }
        }
        return formattedCode.toString();
    }

    /** Helper Methods */

    /**
     * <p>Given a container of tab strings, <code>writeTabs</code> 
     * returns the stack of tabs as a string of consecutive tabs.</p>
     * 
     * @param tabs A container of tab strings.
     */
    private static String writeTabs(Deque<String> tabs) {
        StringBuilder tabStr = new StringBuilder();
        for (String t : tabs) {
            tabStr.append(t);
        }
        return tabStr.toString();
    }

    /**
     * <p>Uses regular expressions to: 
     * <ul><li>Remove all trailing whitespace on ";" and "{".</li><li>
     * Remove all preceding whitespace on "}".</p>
     * 
     * @param code The code string.
     */
    private static String normalizeWhitespace(String code) {
        // I'm sure all of this and more could be accomplished in
        // one expression, but I'm not an expert in Java regular
        // expressions -- and this seems to be working so fine so..	
        code = code.replaceAll("; +", ";"); // remove trailing
        code = code.replaceAll("\\{ +", "{"); // remove trailing
        code = code.replaceAll("\\s+\\}", "}"); // remove preceding
        return code.toString();
    }

}
