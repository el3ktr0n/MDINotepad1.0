/*
 @(#)Replace.java	04-10-2011
 */
package MDINotepad;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;

/**
 * @author Dhruva Bhaswar
 */
public class Replace extends JDialog implements ActionListener{//class to find the queried word and replace 
                                                              //the same with the solicited word
    private JLabel findWhat,replaceWith;
    private JTextField tFindWhat,tReplaceWith;
    private JButton findNext,replace,replaceAll,cancel;
    private JCheckBox matchCase;
    private JPanel panel1,panel2;
    
    private TextEditor te;//to contain the refernce of the main JFrame
    private JEditorPane ep;//to contain the reference of the current JEditorPane
    
    private int rpos,rpos2;//rpos keeps the track of the word to be searched
                           //rpos2 contains the value contained in pos except when pos is -1
    private String text;
    private String lowerText;
    private StringBuffer buffer;
                        //buffer contains the text contained in the current JEditorPane
    private StringBuffer lowerBuffer;
                        //lowerBuffer contains the same string but in lower case
    
    /**
     * constructor for the class 
     * puts all the components at their desired positions in the JDialog
     */
    Replace(JEditorPane ep,TextEditor te){
        this.ep=ep;
        this.te=te;
        
        findWhat=new JLabel("Find what:");
        findWhat.setHorizontalAlignment(SwingConstants.RIGHT);//text in the label is alligned right
        replaceWith=new JLabel("Replace with:");
        replaceWith.setHorizontalAlignment(SwingConstants.RIGHT);
        tFindWhat=new JTextField("");
        tReplaceWith=new JTextField("");
        findNext=new JButton("Find next");
        findNext.addActionListener(this);
        replace=new JButton("Replace");
        replace.addActionListener(this);
        replaceAll=new JButton("Replace all");
        replaceAll.addActionListener(this);
        cancel=new JButton("Cancel");
        cancel.addActionListener(this);
        matchCase=new JCheckBox("Match case");
        
        this.setLayout(new GridLayout(5,3,5,5));
        this.add(findWhat);
        this.add(tFindWhat);
        this.add(findNext);
        this.add(replaceWith);
        this.add(tReplaceWith);
        this.add(replace);
        this.add(new JLabel(""));
        this.add(new JLabel(""));
        this.add(replaceAll);
        this.add(new JLabel(""));
        this.add(new JLabel(""));
        this.add(cancel);
        this.add(matchCase);
        this.add(new JLabel(""));
        this.add(new JLabel(""));
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(rootPaneCheckingEnabled);
        setSize(400,150);
        setLocationRelativeTo(te);
        setResizable(false);
        setTitle("Replace");
        setVisible(true);
    }
    
    /**
     * setter method to set current caret position to private variable 'rpos'
     * this job is done by TextEditor Class
     */
    void rSetPos(int rpos){
        this.rpos=rpos;
    }
    
    /**
     * Creates highlights around all occurrences of pattern in editor pane
     */
    private void highlight(JEditorPane pane, String pattern) {
    try {
        Highlighter hilite = pane.getHighlighter();
        Document doc = pane.getDocument();
        text = doc.getText(0, doc.getLength());
        lowerText=text.toLowerCase(Locale.ENGLISH);
        String lowerPattern=pattern.toLowerCase(Locale.ENGLISH);
        
        buffer=new StringBuffer(text);
        lowerBuffer=new StringBuffer(lowerText);
        // Search for pattern matching the case as well
        if(rpos!=-1 && matchCase.isSelected() && (rpos = buffer.indexOf(pattern, rpos)) >= 0) {
            rpos2=rpos;
            removeHighlights(pane);
            // Create highlighter using private painter and apply around pattern
            hilite.addHighlight(rpos, rpos+pattern.length(), myHighlightPainter);
            rpos += pattern.length();
        } 
        
        // Search for pattern ignoring the case 
        else if(rpos!=-1 && !matchCase.isSelected() && (rpos = lowerBuffer.indexOf(lowerPattern, rpos)) >= 0) {
            rpos2=rpos;
            removeHighlights(pane);
            // Create highlighter using private painter and apply around pattern
            hilite.addHighlight(rpos, rpos+lowerPattern.length(), myHighlightPainter);
            rpos += lowerPattern.length();
        }
        
        else if(rpos==-1){
          rpos=rpos2;
          Toolkit.getDefaultToolkit().beep();
          JOptionPane.showMessageDialog(this, "Cannot find "+"\""+tFindWhat.getText()+"\"");
        }//when no matching occurs indexOf() returns -1
    } 
    catch (BadLocationException e) {    
    }
}

    /**
     * replaces the highlighted word with the solicited one
     * and highlights the next occurrence of the same word
     */
    private void replace(){
        Highlighter hilite=ep.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();
        if(hilites.length==0){
            highlight(ep,tFindWhat.getText());
        }
        buffer.replace(rpos2, rpos2+(tFindWhat.getText().length()), tReplaceWith.getText());
        lowerBuffer.replace(rpos2, rpos2+(tFindWhat.getText().length()), tReplaceWith.getText().toLowerCase());
            //update the two buffers
        ep.setText(buffer.toString());//copies the content of the buffer to the current JEditorPane 'ep'
        highlight(ep,tFindWhat.getText());//highlights the next occurrence
    }
    
    
    /**
     * replaces all the occurrences of the entered word with the one to be replaced with
     */
    private void allReplace(){
        try{
        int index=0;//index contains the position of the word to be found
        Document doc = ep.getDocument();
        text = doc.getText(0, doc.getLength());
        lowerText=text.toLowerCase(Locale.ENGLISH);
        buffer=new StringBuffer(text);
        lowerBuffer=new StringBuffer(lowerText);
        if(matchCase.isSelected()){
            while((index=buffer.indexOf(tFindWhat.getText(), index))>=0){
                buffer.replace(index, index+(tFindWhat.getText().length()), tReplaceWith.getText());
                ep.setText(buffer.toString());
                index+=tReplaceWith.getText().length();
            }
        }
        else{
            while((index=lowerBuffer.indexOf(tFindWhat.getText().toLowerCase(), index))>=0){
                lowerBuffer.replace(index, index+(tFindWhat.getText().length()), tReplaceWith.getText().toLowerCase());
                buffer.replace(index, index+(tFindWhat.getText().length()), tReplaceWith.getText() );
                ep.setText(buffer.toString());
                index+=tReplaceWith.getText().length();
            }
        }
        }
        catch(BadLocationException ex){   
        }
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
    
    /**
     * event handling code corresponding to each source of Action Events
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==findNext){
            highlight(ep,tFindWhat.getText());
        }
        else if(e.getSource()==replace){
            replace();
        }
        else if(e.getSource()==replaceAll){
            allReplace();
        }
        else if(e.getSource()==cancel){
            this.dispose();
        }
    }
}
