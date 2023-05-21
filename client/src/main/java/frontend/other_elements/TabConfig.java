package frontend.other_elements;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

public class TabConfig {
    public static final int TAB_LENGTH = 25;
    public static final int TAB_COUNT = 100;

    public static SimpleAttributeSet getTabAttributeSet() {
        // Setting the number of tabs and their length
        TabStop[] tabs = new TabStop[TAB_COUNT];
        for (int j = 0; j < tabs.length; j++) {
            tabs[j] = new TabStop((j + 1) * TAB_LENGTH);
        }

        TabSet tabSet = new TabSet(tabs);

        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);

        return attributes;
    }
}
