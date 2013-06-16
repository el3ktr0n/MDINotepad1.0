/*
 @(#)Format.java	04-10-2011
 */
package MDINotepad;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * 
 * @author Dhruva Bhaswar
 */
public class Format extends JDialog implements ActionListener{//class to create Font dialog
                                                              //giving the users option of choosing appropriate Font parameters
    private JLabel font,fontStyle,size,sample;
    private JTextField fontTF,fontStyleTF,sizeTF;
    private JList fontCB,fontStyleCB,sizeCB;
    private JButton ok,cancel;
    private String currentFont="Aharoni",currentStyle="Regular";
    private int currentSize=8;
    private JEditorPane editor;//to contain the reference of the current JEditorPane
    
    /**
     * constructor for the class
     * puts all the components at their desired position in the JDialog
     */
    Format(JEditorPane pane){
        editor=pane;
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
                                //GridBagLayout is chosen as the layout manager
                                //c contains all the GridBagConstraints for each component
                                //allowing the componenet to be placed at their respective location
        font=new JLabel("Font:");
        c.fill= GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth=2;
        add(font,c);
       
        fontStyle=new JLabel("Font Style:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth=2;
        add(fontStyle,c);
        
        fontStyle=new JLabel("Font Size:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0;
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth=2;
        add(fontStyle,c);
        
        fontTF=new JTextField(currentFont);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        c.insets=new Insets(5,5,5,5);
        add(fontTF,c);
        
        fontStyleTF=new JTextField(currentStyle);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 1;
        c.insets=new Insets(5,5,5,5);
        add(fontStyleTF,c);
        
        sizeTF=new JTextField(String.valueOf(currentSize));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 4;
        c.gridy = 1;
        c.insets=new Insets(5,5,5,5);
        add(sizeTF,c);
        
        GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                                   //gEnv an instance of GraphicsEnvironment class captures all the 
                                   //graphics properties of the system
                                   //allowing us to get the list of all the available fonts
        String envfonts[] = gEnv.getAvailableFontFamilyNames();
        Vector vector = new Vector();
        for (int i = 1; i < envfonts.length; i++) {
                vector.addElement(envfonts[i]);
          }//vector contains all the available font names
        fontCB = new JList(vector);//a JList is created out of the vector
        JScrollPane scroll = new JScrollPane(fontCB,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                                    //a JScrollPane is attached to the JList's view port
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        c.insets=new Insets(5,5,5,5);
        add(scroll,c);
        
        String styles[]={"Regular","Itallic","Bold","Bold Itallic"};
        vector=new Vector();
        for(int i=0;i<4;++i){
            vector.addElement(styles[i]);
        }
        fontStyleCB=new JList(vector);
                    //another JList is created out of the vector containing the possible font styles
        JScrollPane scroll1 = new JScrollPane(fontStyleCB,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 2;
        c.gridy = 2;
        c.insets=new Insets(5,5,5,5);
        add(scroll1,c);
        
        int arr[]={8,9,10,11,12,14,16,18,20,22,24,28,36,48};
        vector=new Vector();
        for(int i=0;i<14;++i){
            vector.addElement(arr[i]);
        }
        sizeCB=new JList(vector);
                 //JList containing font sizes
        JScrollPane scroll2 = new JScrollPane(sizeCB,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.fill=GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridx = 4;
        c.gridy = 2;
        c.insets=new Insets(5,5,5,5);
        add(scroll2,c);
        
        
        sample=new JLabel("AaBbCc");//a JLabel to represent the sample effect of the chosen font parameters
        sample.setFont(new Font (currentFont,Font.PLAIN,currentSize));
        c.weightx=0;
        c.gridwidth = 3;
        c.gridheight=3;
        c.gridx = 3;
        c.gridy = 4;
        c.insets=new Insets(10,10,10,10);
        sample.setBorder(BorderFactory.createTitledBorder("Preview"));
        add(sample,c);
        
        ok=new JButton("OK");
        c.weightx=0;
        c.gridwidth = 1;
        c.gridx = 4;
        c.gridy = 7;
        c.insets=new Insets(5,5,5,5);
        add(ok,c);
        ok.addActionListener(this);
        
        cancel=new JButton("Cancel");
        c.fill=GridBagConstraints.NONE;
        c.weightx=0;
        c.gridwidth = 1;
        c.gridx = 5;
        c.gridy = 7;
        c.insets=new Insets(5,5,5,5);
        add(cancel,c);
        cancel.addActionListener(this);
        
        fontCB.setSelectedIndex(0);
        fontStyleCB.setSelectedIndex(0);
        sizeCB.setSelectedIndex(0);
        
        /**
         * fontCB an instance of JList is registered to ListSelectionListener to listen to
         * List Selection Events 
         * applying the selected font to sample label
         * and displaying the selected font in respective JTextField fontTF
         */
        fontCB.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentFont=fontCB.getSelectedValue().toString();
                updateSampleText();
            }  
        });
        
        /**
         * fontStyleCB an instance of JList is again registered to ListSelectionListener to listen to
         * List Selection Events
         * applying the selected font style to sample label
         * and displaying the selected style in respective JTextField fontStyleTF
         */
        fontStyleCB.addListSelectionListener(new ListSelectionListener (){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentStyle=fontStyleCB.getSelectedValue().toString();
                updateSampleText();
            }  
        });
        
        /**
         * SizeCB an instance of JList is again registered to ListSelectionListener to listen to
         * List Selection Events
         * applying the selected font size to sample label
         * and displaying the selected size in respective JTextField sizeTF
         */
        sizeCB.addListSelectionListener(new ListSelectionListener (){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                currentSize=Integer.parseInt(sizeCB.getSelectedValue().toString());
                updateSampleText();
            }
        });
        
        this.setModal(true);//the Font dialog is made modal
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600,400);
        setResizable(false);//the dialog is not resiazable
        setTitle("Font");
        setVisible(true);
    }
    
    /**
     * applies the selected font parameters to sample label
     * and displays the selected values in corresponding JTextField
     */
    public void updateSampleText(){
        int style;//'style' contains the code corresponding to chosen style
        if(currentStyle.equals("Regular")){
            style=Font.PLAIN;
        }
        else if(currentStyle.equals("Itallic")){
            style=Font.ITALIC;
        }
        else if(currentStyle.equals("Bold")){
            style=Font.BOLD;
        }
        else{
            style=Font.BOLD+Font.ITALIC;
        }
        fontTF.setText(currentFont);
        fontStyleTF.setText(currentStyle);
        sizeTF.setText(String.valueOf(currentSize));
        sample.setFont(new Font(currentFont,style,currentSize));
    }
    
    /**
     * handles all the generated action events
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==ok){
            int style;
        if(currentStyle.equals("Regular")){
            style=Font.PLAIN;
        }
        else if(currentStyle.equals("Itallic")){
            style=Font.ITALIC;
        }
        else if(currentStyle.equals("Bold")){
            style=Font.BOLD;
        }
        else{
            style=Font.BOLD+Font.ITALIC;
        }
            editor.setFont(new Font(currentFont,style,currentSize));//when ok is clicked the chosen 
                                                                    //font parameters are applied to the current JEditorPane
            this.dispose();
        }
        if(e.getSource()==cancel){
            this.dispose();//disposes the JDialog instance closing the window
        }
    }
}
