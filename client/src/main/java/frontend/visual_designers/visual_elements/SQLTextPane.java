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
        this.setText("");
        String[] tokensPrimitive = text.split("((?<=[ \n()])|(?=[ \n()]))");

        for (String token : tokensPrimitive) {
            boolean colored = false;
            if (this.keywords.contains(token) || this.types.contains(token)) {
                StyleConstants.setForeground(this.style, new Color(79, 162, 255));
                StyleConstants.setItalic(this.style, true);
                colored = true;
            }

            // If we colored it
            if (!colored) {
                StyleConstants.setForeground(this.style, new Color(221,221, 221));
                StyleConstants.setItalic(this.style, false);
            }

            try {
                styledDocument.insertString(this.styledDocument.getLength(), token, this.style);
            } catch (BadLocationException e) {
                log.error("Bad location at SQL text!");
                return;
            }
        }
    }
}
