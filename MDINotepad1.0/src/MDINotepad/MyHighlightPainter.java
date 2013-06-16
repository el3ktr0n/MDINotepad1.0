/*
 @(#)MyHighlight.java	04-10-2011
 */
package MDINotepad;

import java.awt.Color;
import javax.swing.text.DefaultHighlighter;

/**
 * @author Dhruva Bhaswar
 */

public class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter{// A class of the default highlight painter
     public MyHighlightPainter(Color color) {
            super(color);
        }
}
