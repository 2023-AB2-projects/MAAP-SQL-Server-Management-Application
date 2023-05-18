package frontend.visual_designers.visual_elements;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.HashMap;

@Slf4j
public class SQLTextPane extends JTextPane {
    private final StyledDocument styledDocument;
    private final Style style;

    private final String[] RESERVED_KEYWORDS = {
            "create", "drop", "database", "table",
            "select", "from", "where", "group", "order", "by",
            "update", "set",
            "insert", "into", "values",
            "delete", "from", "on", "use", "as",
            "inner", "join", "alter", "add", "having", "constraint",
            "foreign", "primary", "key", "unique", "references",
            "CREATE", "DROP", "DATABASE", "TABLE",
            "SELECT", "FROM", "WHERE", "GROUP", "ORDER", "BY",
            "UPDATE", "SET",
            "INSERT", "INTO", "VALUES", "AS",
            "DELETE", "FROM", "ON", "USE", "HAVING", "CONSTRAINT",
            "FOREIGN", "PRIMARY", "KEY", "UNIQUE", "REFERENCES",
            "INNER", "JOIN", "ALTER", "ADD"
    };

    private final String[] OPERATORS = {
            "(", ")", "*",
            "==", "!=", "=", ">=", "<=", ">", "<",
    };

    private final String[] ATTRIBUTE_TYPES = {
            "int", "float", "bit", "date", "datetime", "char",
            "INT", "FLOAT", "BIT", "DATE", "DATETIME", "CHAR"
    };

    private final HashMap<String, Boolean> keywordsMap, typesMap, operatorsMap;

    public SQLTextPane() {
        super();

        this.styledDocument = this.getStyledDocument();
        this.style = this.addStyle("Styleee", null);

        this.keywordsMap = new HashMap<>();
        for (final String keyword : this.RESERVED_KEYWORDS) {
            keywordsMap.put(keyword, true);
        }
        this.typesMap = new HashMap<>();
        for (final String types : this.ATTRIBUTE_TYPES) {
            typesMap.put(types, true);
        }
        this.operatorsMap = new HashMap<>();
        for (final String operator : this.OPERATORS) {
            operatorsMap.put(operator, true);
        }
    }

    public void setTextSQL(String text) {
        this.setText("");
        System.out.println(text);
        String[] tokensPrimitive = text.split("((?<=[ \n\r()])|(?=[ \n\r()]))");

        for (final String token : tokensPrimitive) {
            if (this.keywordsMap.containsKey(token)) {
                StyleConstants.setForeground(this.style, new Color(79, 162, 255));
                StyleConstants.setItalic(this.style, true);

                try {
                    styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
                } catch (BadLocationException e) {
                    log.error("Bad location at SQL text!");
                    return;
                }
                continue;
            }

            if (this.typesMap.containsKey(token)) {
                StyleConstants.setForeground(this.style, new Color(11, 180, 204));
                StyleConstants.setItalic(this.style, true);

                try {
                    styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
                } catch (BadLocationException e) {
                    log.error("Bad location at SQL text!");
                    return;
                }
                continue;
            }

            if (this.operatorsMap.containsKey(token)) {
                StyleConstants.setForeground(this.style, new Color(252, 85, 165));
                StyleConstants.setItalic(this.style, true);

                try {
                    styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
                } catch (BadLocationException e) {
                    log.error("Bad location at SQL text!");
                    return;
                }
                continue;
            }

            StyleConstants.setForeground(this.style, new Color(221,221, 221));
            StyleConstants.setItalic(this.style, false);

            try {
                styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
            } catch (BadLocationException e) {
                log.error("Bad location at SQL text!");
                return;
            }
        }
    }
}
