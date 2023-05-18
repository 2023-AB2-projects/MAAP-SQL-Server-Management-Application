package frontend.visual_designers.visual_elements;

import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.text.Style;
import javax.swing.text.StyledDocument;
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

        this.setStyledDocument(new SQLDocument());
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

        JEditorPane pane = new JEditorPane();

    }

    public void setTextSQL(String text) {

    }
}
