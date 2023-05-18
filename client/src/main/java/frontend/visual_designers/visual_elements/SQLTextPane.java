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
            "!=", "=", ">=", "<=", ">", "<",
    };

    private final String[] ATTRIBUTE_TYPES = {
            "int", "float", "bit", "date", "datetime", "char",
            "INT", "FLOAT", "BIT", "DATE", "DATETIME", "CHAR"
    };

    private final ArrayList<String> keywords, types, operators;

    public SQLTextPane() {
        super();

        this.styledDocument = this.getStyledDocument();
        this.style = this.addStyle("Styleee", null);

        // Convert primitive to normal
        this.keywords = new ArrayList<>(Arrays.asList(this.RESERVED_KEYWORDS));
        this.types = new ArrayList<>(Arrays.asList(ATTRIBUTE_TYPES));
        this.operators = new ArrayList<>(Arrays.asList(OPERATORS));
    }

    public void setTextSQL(String text) {
        this.setText("");
        System.out.println(text);
        String[] tokensPrimitive = text.split("((?<=[ \n\r()])|(?=[ \n\r()]))");

        for (String token : tokensPrimitive) {
            if (this.keywords.contains(token)) {
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

            if (this.types.contains(token)) {
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

            if (this.operators.contains(token)) {
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
