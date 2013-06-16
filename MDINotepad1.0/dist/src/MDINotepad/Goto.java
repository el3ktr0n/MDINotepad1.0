/*
 @(#)Goto.java	04-10-2011
 */
package MDINotepad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 *
 * @author Dhruva Bhaswar
 */
public class Goto extends JDialog implements ActionListener{
    private JEditorPane ep;
    private JLabel lineNum=new JLabel("Line Number");
    private JTextField lineNumTF=new JTextField("");
    private JButton goTo=new JButton("Go to");
    private JButton cancel=new JButton("Cancel");
    private TextEditor te;
    Goto(JEditorPane ep,TextEditor te){
        this.ep=ep;
        this.te=te;
        setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        
        c.gridx=0;
        c.gridy=0;
        c.gridwidth=1;
        c.gridheight=1;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.insets=new Insets(5,5,5,5);
        add(lineNum,c);
        
        c.gridx=0;
        c.gridy=1;
        c.gridwidth=3;
        c.gridheight=1;
        c.insets=new Insets(5,5,5,5);
        c.fill=GridBagConstraints.HORIZONTAL;
        add(lineNumTF,c);
        
        c.gridx=1;
        c.gridy=2;
        c.gridwidth=1;
        c.gridheight=1;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.insets=new Insets(5,5,5,5);
        add(goTo,c);
        goTo.addActionListener(this);
        
        c.gridx=2;
        c.gridy=2;
        c.gridwidth=1;
        c.gridheight=1;
        c.insets=new Insets(5,5,5,5);
        c.fill=GridBagConstraints.HORIZONTAL;
        add(cancel,c);
        cancel.addActionListener(this);
        
        this.setModal(true);//the Font dialog is made modal
        this.setAlwaysOnTop(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(250,150);
        setResizable(false);//the dialog is not resiazable
        setTitle("Go to");
        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==cancel){
            this.dispose();
        }
        else if(e.getSource()==goTo){
                String str=ep.getText();
                StringBuffer strBuffer=new StringBuffer(str);
                for(int i=0;;++i){
                    if(i>=strBuffer.length()){
                        break;
                    }
                    if(strBuffer.charAt(i)=='\n'){
                        strBuffer.deleteCharAt(i);//all the newline characters are deleted
                    }
                }
                str=strBuffer.toString();
                int count=1,i=0;
                int lineNo=Integer.parseInt(lineNumTF.getText());
                while(i<str.length() && lineNo>count) {
                    if(str.charAt(i)=='\r'){
                        count++;
                    }
                    i++;
                }
                ep.setCaretPosition(i);
                te.currentColVec.set(te.tp.getSelectedIndex(), 1);
                te.currentLineVec.set(te.tp.getSelectedIndex(), lineNo);
                te.lineCount.setText("ln:"+Integer.parseInt(te.currentLineVec.get(te.tp.getSelectedIndex()).toString())+" col:"+Integer.parseInt(te.currentColVec.get(te.tp.getSelectedIndex()).toString()));
                this.dispose();
        }
    }
    
}
