package frontend3.object_explorer;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

public class ObjectExplorerJTree extends DefaultMutableTreeNode {

    public ObjectExplorerJTree(ArrayList<String> databaseNames) {
        super("Databases");

        // Add all database names to database node
        for (final String databaseName : databaseNames) {
            this.add(new DefaultMutableTreeNode(databaseName));
        }
    }
}
