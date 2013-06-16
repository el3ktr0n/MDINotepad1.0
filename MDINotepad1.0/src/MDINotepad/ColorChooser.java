/*
 @(#)ColorChooser.java	04-10-2011
 */
package MDINotepad;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JEditorPane;

/**
 *
 * @author Dhruva Bhaswar
 */
public class ColorChooser extends JDialog implements ActionListener{//gives the choice of setting desired color
                                                                    //to text in the JEditorPane
    private JColorChooser colorPanel;
    private JEditorPane ep;
    private JButton ok,cancel;
    
    /**
     * constructor for the class,
     * lays out the design of the window
     */
    ColorChooser(JEditorPane ep){
        this.ep=ep;
        
        setLayout(new GridBagLayout());
        GridBagConstraints c=new GridBagConstraints();
        
        colorPanel=new JColorChooser();
        c.fill=GridBagConstraints.BOTH;
        c.gridx=0;
        c.gridy=0;
        c.gridheight=10;
        c.gridwidth=10;
        this.add(colorPanel,c);
        
        cancel=new JButton("Cancel");
        c.anchor=GridBagConstraints.SOUTHEAST;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=8;
        c.gridy=10;
        c.gridwidth=1;
        c.insets=new Insets(5,5,5,5);
        this.add(cancel,c);
        cancel.addActionListener(this);
        
        ok=new JButton("OK");
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridx=9;
        c.gridy=10;
        c.gridwidth=1;
        c.insets=new Insets(5,5,5,5);
        this.add(ok,c);
        ok.addActionListener(this);
        
        this.setModal(true);//the Font dialog is made modal
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600,410);
        setResizable(false);//the dialog is not resiazable
        setTitle("Color");
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==ok){
            ep.setForeground(colorPanel.getColor());//the chosen color is set to the current JEditorPane
            this.dispose();
        }
        else if(e.getSource()==cancel){
            this.dispose();//disposes the window
        }
    }
}
