package frontend.visual_designers.visual_elements;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

@Slf4j
public class SQLTextPane extends JTextPane {
    private final StyledDocument styledDocument;
    private final Style style;

    private final String[] RESERVED_KEYWORDS = {
            "create", "drop", "database", "table",
            "select", "from", "where", "group", "order", "by",
            "update", "set",
            "insert", "into", "values",
            "delete", "from", "on",
            "inner", "join",
            "(", ")",
            "!=", "=", ">=", "<=", ">", "<",
            "foreign", "primary", "key", "unique", "references",
            "CREATE", "DROP", "DATABASE", "TABLE",
            "SELECT", "FROM", "WHERE", "GROUP", "ORDER", "BY",
            "UPDATE", "SET",
            "INSERT", "INTO", "VALUES",
            "DELETE", "FROM", "ON",
            "FOREIGN", "PRIMARY", "KEY", "UNIQUE", "REFERENCES",
            "INNER", "JOIN",
    };
    private final String[] ATTRIBUTE_TYPES = {
            "int", "float", "bit", "date", "datetime", "char",
            "INT", "FLOAT", "BIT", "DATE", "DATETIME", "CHAR"
    };

    private final ArrayList<String> keywords, types;

    public SQLTextPane() {
        super();

        this.styledDocument = this.getStyledDocument();
        this.style = this.addStyle("Styleee", null);

        // Convert primitive to normal
        this.keywords = new ArrayList<>(Arrays.asList(this.RESERVED_KEYWORDS));
        this.types = new ArrayList<>(Arrays.asList(ATTRIBUTE_TYPES));
    }

    public void setTextSQL(String text) {
        StringTokenizer tokenizer = new StringTokenizer(text, " ");

        String[] tokensPrimitive = text.split("((?<=[ \n])|(?=[ \n]))");

        for (String token : tokensPrimitive) {
            boolean colored = false;
            for (final String keyword : this.keywords) {
                if(token.contains(keyword)) {
                    StyleConstants.setForeground(this.style, new Color(79, 162, 255));
                    colored = true;
                    break;
                }
            }

            if (!colored) {
                for (final String type : this.types) {
                    if (token.contains(type)) {
                        StyleConstants.setForeground(this.style, new Color(79, 162, 255));
                        colored = true;
                        break;
                    }
                }
            }

            // If we colored it
            if (!colored) {
                StyleConstants.setForeground(this.style, new Color(221,221, 221));
            }

            try {
                styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
            } catch (BadLocationException e) {
                log.error("Bad location at SQL text!");
                return;
            }
        }

//        boolean firstToken = true;
//        while (tokenizer.hasMoreTokens()) {
//            String token = tokenizer.nextToken();
//
//            boolean colored = false;
//            for (final String keyword : this.keywords) {
//                if(token.contains(keyword)) {
//                    StyleConstants.setForeground(this.style, new Color(79, 162, 255));
//                    colored = true;
//                    break;
//                }
//            }
//
//            if (!colored) {
//                for (final String type : this.types) {
//                    if (token.contains(type)) {
//                        StyleConstants.setForeground(this.style, new Color(79, 162, 255));
//                        colored = true;
//                        break;
//                    }
//                }
//            }
//
//            if (firstToken) {
//                firstToken = false;
//            } else {
//                token = ' ' + token;
//            }
//
//            // If we colored it
//            if (!colored) {
//                StyleConstants.setForeground(this.style, new Color(221,221, 221));
//            }
//
//            try {
//                styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
//            } catch (BadLocationException e) {
//                log.error("Bad location at SQL text!");
//                return;
//            }
//        }
    }
}
