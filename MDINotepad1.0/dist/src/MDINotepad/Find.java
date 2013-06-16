/*
 @(#)Find.java	04-10-2011
 */
package MDINotepad;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
/**
 * @author Dhruva Bhaswar
 */
public class Find extends JDialog implements ActionListener{//class to find and highlight the queried word
    JEditorPane ep;//to contain the reference of the current JEditorPane
    private JLabel findWhat;
    JTextField enterWhat;
    private JButton findNext,cancel;
    private JCheckBox matchCase;
    private JRadioButton up,down;
    private JPanel panel;
    private int pos,pos2;//pos keeps the track of the word to be searched
                        //pos2 keeps the track of pos except the case when pos is set to -1
    private TextEditor te;//to contain the reference of the main JFrame
    private String text,lowerText;
                    //text contains the String contained in current JEditorPane
                    //lowerText conts the same string in lower case
    
    /**
     * constructor for the class which puts all the components at their respective positions
     */
    Find(final JEditorPane ep,TextEditor te){
        this.ep=ep;
        this.te=te;
        this.pos=te.getCurrentcaretPosition();//pos is set to the current caret position in the current JEditorPane
        setLayout(new GridLayout(3,3,10,10));//GridLayout is chosen as the layout manager
        
        findWhat=new JLabel("Find what:");
        findWhat.setHorizontalAlignment(SwingConstants.RIGHT);//text in the label is alligned right
        this.add(findWhat);
        enterWhat=new JTextField("");
        this.add(enterWhat);
        this.add(new JLabel(""));
        this.add(new JLabel(""));
        this.add(new JLabel(""));
        findNext=new JButton("Find Next");
        findNext.addActionListener(this);
        this.add(findNext);
        matchCase=new JCheckBox("Match case");
        this.add(matchCase);
        panel=new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Direction"));//border is added to the panel
                                                                       //the border also shows the title at the left corner
        this.add(panel);
        panel.setLayout(new GridLayout(1,2));
                      //the panel has GridLayout as its layout manager with one row and one column
                      //'up' and 'down' buttons are added to the two cells
        up=new JRadioButton("Up");
        
        /**
         * the following event handling codes make only one of the two JRadioButtons
         * to be selected at a time
         */
        up.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(up.isSelected()){
                    down.setSelected(false);
                }
                else{
                    down.setSelected(true);
                }
            }
            
        });
        panel.add(up);
        down=new JRadioButton("Down");
        down.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if(down.isSelected()){
                    up.setSelected(false);
                }
                else{
                    up.setSelected(true);
                }
            }
            
        });
        
        down.setSelected(true);//down is selected by default
        panel.add(down);
        cancel=new JButton("Cancel");
        cancel.addActionListener(this);
        this.add(cancel);
        
        
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setLocationRelativeTo(te);
        setAlwaysOnTop(rootPaneCheckingEnabled);
        setSize(450,175);
        setResizable(false);
        setTitle("Find");
        if(!this.isVisible())
            setVisible(true);
    }
    
    /**
     * setter method to set the value to 'pos'
     * the method has been called in TextEditor class to set current caret position to 'pos'
     */
    void setPos(int pos){
            this.pos=pos;
    }
    
    /*
     * Creates highlights around all occurrences of pattern in editor pane
     */
    void highlight(JEditorPane pane, String pattern) {
        try {
            Highlighter hilite = pane.getHighlighter();
            Document doc = pane.getDocument();
            text = doc.getText(0, doc.getLength());
            lowerText=text.toLowerCase(Locale.ENGLISH);
            String lowerPattern=pattern.toLowerCase(Locale.ENGLISH);
        
            // Search for pattern in downward direction matching the case as well
            if(pos!=-1 && matchCase.isSelected() && down.isSelected() && (pos = text.indexOf(pattern, pos)) >= 0) {
                pos2=pos;
                removeHighlights(pane);
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos+pattern.length(), myHighlightPainter);
                pos += pattern.length();
            } 
            
            // Search for pattern in downward direction ignoring the case     
            else if(pos!=-1 && !matchCase.isSelected() && down.isSelected() && (pos = lowerText.indexOf(lowerPattern, pos)) >= 0) {
                pos2=pos;
                removeHighlights(pane);
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos, pos+lowerPattern.length(), myHighlightPainter);
                pos += lowerPattern.length();
            }
            
            // Search for pattern in upward direction matching the case as well
            else if(pos!=-1 && matchCase.isSelected() && up.isSelected() && (pos=myRevIndexOf(text,pattern, pos))>=0 ){
                pos2=pos;
                removeHighlights(pane);
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos-pattern.length()+1, pos+1, myHighlightPainter);
                pos-=pattern.length();
            }
            
            // Search for pattern in upward direction ignoring the case 
            else if(pos!=-1 && !matchCase.isSelected() && up.isSelected() && (pos=myRevIndexOf(lowerText,lowerPattern,pos))>=0){
                pos2=pos;
                removeHighlights(pane);
                // Create highlighter using private painter and apply around pattern
                hilite.addHighlight(pos-lowerPattern.length()+1, pos+1, myHighlightPainter);
                pos-=lowerPattern.length();
            }
        
            else if(pos==-1){
                pos=pos2;
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Cannot find "+"\""+enterWhat.getText()+"\"");
            }//when no matching occurs indexOf() returns -1
        } 
        catch (BadLocationException e) {    
        }
    }
    
    /**
     * the method to search for the 'pattern' in 'text' starting from 'pos' in upward direction
     */
    private int myRevIndexOf(String text,String pattern,int pos){
        int len=pattern.length();
        if(pos==text.length()){
            pos--;
        }
        for(int k=pos;k>=len-1;--k){
            int flag=0;
            for(int i=k,j=len-1;j>=0;--i,--j){
                if(text.charAt(i)!=pattern.charAt(j)){
                    flag=1;
                    break;
                }
            }
            if(flag==0){
                return k;
            }
        }
        return -1; 
    }
    
    /**
     * Removes only our private highlights
     */
    private void removeHighlights(JEditorPane pane) {
        Highlighter hilite = pane.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        
        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }

    // An instance of the private subclass of the default highlight painter
    private Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(Color.red);

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==findNext){
            highlight(ep,enterWhat.getText());//when findNext button is clicked 
                                              //the respective word is highlighted ,if found
        }
        
        else if(e.getSource()==cancel){
            this.dispose();//closes the window
        }
    }

// A private subclass of the default highlight painter
    /*class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
        public MyHighlightPainter(Color color) {
            super(color);
        }
    }*/
}
