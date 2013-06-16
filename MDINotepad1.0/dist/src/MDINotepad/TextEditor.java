/*
 @(#)TextEditor.java	04-10-2011
 */
package MDINotepad;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Highlighter;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.event.CaretListener;

/**
 *
 * @author Dhruva Bhaswar
 */
public class TextEditor extends JFrame implements ActionListener{
    private UndoAction undoAction = null;
    private RedoAction redoAction = null;
    
    JEditorPane currentEP;
    private JTextArea textarea;
    JEditorPane ep;
    JTabbedPane tp=new JTabbedPane();//to implement multiple document 
                                               //interface
    UndoManager m_undo;//to implement undo and redo
    private JFileChooser fc=new JFileChooser();
    
    String currentFile="Untitled";
    
    Vector modifiedVec;//to keep the track of whether file in particular tab has been 
                        //modified or not
    boolean modified=false;//to keep the track whether current file
                                     //has been modified or not
    Vector saveStateVec,saveAsStateVec,undoStateVec,redoStateVec,findStateVec;
                                    //to keep the track of whether the respective
                                    //menu items corresponding to each open 
                                    //document is enabled or not
    private JMenuBar mb=new JMenuBar();
    
    private JPopupMenu pop=new JPopupMenu("");
    private JMenuItem closeAll=new JMenuItem("Close all documents");
    private JMenu file=new JMenu("File");
    private JMenuItem nnew=new JMenuItem("New");
    private JMenuItem open=new JMenuItem("Open...");
    JMenuItem save=new JMenuItem("Save");
    JMenuItem saveAs=new JMenuItem("Save as...");
    private JMenuItem print=new JMenuItem("Print...");
    private JMenuItem exit=new JMenuItem("Exit");
    
    private JMenu edit=new JMenu("Edit");
    private JMenuItem undo;
    private JMenuItem redo;
    private JMenuItem cut=new JMenuItem("Cut");
    private JMenuItem copy=new JMenuItem("Copy");
    private JMenuItem paste=new JMenuItem("Paste");
    private JMenuItem delete=new JMenuItem("Delete");
    private JMenuItem find=new JMenuItem("Find...");
    private JMenuItem findNext=new JMenuItem("Find Next");
    private JMenuItem replace=new JMenuItem("Replace...");
    private JMenuItem ggoto=new JMenuItem("Go To...");
    private JMenuItem selectAll=new JMenuItem("Select All");
    private JMenuItem timeDate=new JMenuItem("Time/Date");
        
    private JMenu format=new JMenu("Format");
    private JMenuItem wordWrap=new JMenuItem("Word Wrap");
    private JMenuItem font=new JMenuItem("Font...");
    private JMenuItem color=new JMenuItem("Color...");
    
    private JMenu view=new JMenu("View");
    private JCheckBoxMenuItem statusBar=new JCheckBoxMenuItem("StatusBar");
    
    private JMenu help=new JMenu("Help");
    private JMenuItem viewHelp=new JMenuItem("View Help...");
    private JMenuItem about=new JMenuItem("About...");
    
    private JToolBar status=new JToolBar();
    JLabel lineCount=new JLabel("ln:1 col:1");
    
    private int currentCarretPos,latterCarretPos;//to keep the track of current caret position
                                //in the current file
    Vector currentLineVec=new Vector();//to keep track of current line number for each tab
    Vector currentColVec=new Vector();//to keep tack of current column number for each tab
    private Vector lineCountVec=new Vector();//to keep track of total number of lines for each tab
    
    private Find f;
    private Replace r;
    private Vector m_undoVec;
    
    /**
     *constructor for the class,
     *sets all the components at their desirable position in the frame 
     */
    TextEditor(){
        undoAction=new UndoAction();
        redoAction=new RedoAction();
        undo=new JMenuItem(undoAction);
        redo=new JMenuItem(redoAction);
        
        setLayout(new BorderLayout());
        
        status.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        status.add(new JLabel(""));
        status.add(new JLabel(""));
        status.add(lineCount);
        this.add(status,BorderLayout.SOUTH);
        status.setVisible(false);
        
        modifiedVec=new Vector();
        
        saveStateVec=new Vector();
        saveAsStateVec=new Vector();
        undoStateVec=new Vector();
        redoStateVec=new Vector();
        findStateVec=new Vector();
        m_undoVec=new Vector();
        createEditor();
        add(tp);
        
        pop.add(closeAll);
        closeAll.addActionListener(this);
        closeAll.setFocusable(true);
        pop.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        
        setJMenuBar(mb);
        file.setMnemonic(KeyEvent.VK_F);
        file.add(nnew);
        nnew.addActionListener(this);//registers the menu item to ActionListener,
                                    //which captures Action Events and handles
                                    //them appropriately
        nnew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK, true));
                                        //sets the shorcut for the menu item
                                        //Ctrl+N for this case
        file.add(open);
        open.addActionListener(this);
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK, true));
        file.add(save);
        save.addActionListener(this);
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, true));
        file.add(saveAs);
        saveAs.addActionListener(this);
        file.addSeparator();
        file.add(print);
        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_DOWN_MASK, true));
        file.addSeparator();
        file.add(exit);
        exit.addActionListener(this);
        
        edit.setMnemonic(KeyEvent.VK_E);
        
        edit.add(undo);
        edit.add(redo);
        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK, true));
        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK, true));
        edit.addSeparator();
        edit.add(cut);
        cut.addActionListener(this);
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(copy);
        copy.addActionListener(this);
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(paste);
        paste.addActionListener(this);
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(delete);
        delete.addActionListener(this);
        delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, KeyEvent.VK_UNDEFINED, true));
        edit.addSeparator();
        edit.add(find);
        find.addActionListener(this);
        find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(findNext);
        findNext.addActionListener(this);
        findNext.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, KeyEvent.VK_UNDEFINED, true));
        edit.add(replace);
        replace.addActionListener(this);
        replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(ggoto);
        ggoto.addActionListener(this);
        ggoto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, KeyEvent.CTRL_DOWN_MASK, true));
        edit.addSeparator();
        edit.add(selectAll);
        selectAll.addActionListener(this);
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK, true));
        edit.add(timeDate);
        timeDate.addActionListener(this);
        timeDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, KeyEvent.VK_UNDEFINED, true));
        
        format.setMnemonic(KeyEvent.VK_O);
        //format.add(wordWrap);
        format.add(font);
        font.addActionListener(this);
        format.add(color);
        color.addActionListener(this);
        
        view.setMnemonic(KeyEvent.VK_V);
        view.add(statusBar);
        statusBar.addActionListener(this);
        
        help.setMnemonic(KeyEvent.VK_H);
        help.add(about);
        about.addActionListener(this);
        help.add(viewHelp);
        viewHelp.addActionListener(this);
        
        mb.add(file);
        mb.add(edit);
        mb.add(format);
        mb.add(view);
        mb.add(help);
        
        initTabComponent(0);//to create close button for respective tab
                            //in the JTabbedPane 'tp',
                            //in this case for the tab at index 0
        
        /**
         * registers the JTabbedPane tp to ChangeListener
         * to handle the Change Event,
         * in this case to change the state of the respective menu items
         * whenever new tab gets focus according to the current state values 
         * stored in the respective vectors
         * and to update the 'currentFile'
         */
        tp.addChangeListener(new ChangeListener(){

            @Override
            public void stateChanged(ChangeEvent e) {
                try{
                lineCount.setText("ln:"+Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+" col:"+Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString()));
                m_undo=(UndoManager)m_undoVec.elementAt(tp.getSelectedIndex());
                currentFile=tp.getTitleAt(tp.getSelectedIndex());
                modified=Boolean.parseBoolean(modifiedVec.get(tp.getSelectedIndex()).toString());
                save.setEnabled(Boolean.parseBoolean(saveStateVec.get(tp.getSelectedIndex()).toString()));
                saveAs.setEnabled(Boolean.parseBoolean(saveAsStateVec.get(tp.getSelectedIndex()).toString()));
                undoAction.setEnabled(Boolean.parseBoolean(undoStateVec.get(tp.getSelectedIndex()).toString()));
                redoAction.setEnabled(Boolean.parseBoolean(redoStateVec.get(tp.getSelectedIndex()).toString()));
                find.setEnabled(Boolean.parseBoolean(findStateVec.get(tp.getSelectedIndex()).toString()));
                findNext.setEnabled(Boolean.parseBoolean(findStateVec.get(tp.getSelectedIndex()).toString()));
                currentEP=(JEditorPane)((JScrollPane)tp.getSelectedComponent()).getViewport().getView();
                }
                catch(Exception ex){
                    
                }
            }
            
        });
        
        
        /**
         * registers the current JFrame to WindowListener,
         * to check whether all the open documents need to be saved before
         * application closes
         */
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                while(tp.getTabCount()>0){
                    currentFile=tp.getTitleAt(0);
                    modified=Boolean.parseBoolean(modifiedVec.get(0).toString());
                    if(savePrev()){
                        tp.remove(0);
                        modifiedVec.remove(0);
                    }
                }
            }
        });
        
        /**
         * registers the current JFrame to WindowStateListener
         */
        this.addWindowStateListener(new WindowStateListener(){

            @Override
            public void windowStateChanged(WindowEvent e) {
                if(e.getOldState()==NORMAL || e.getOldState()==MAXIMIZED_HORIZ || e.getOldState()==MAXIMIZED_VERT || e.getOldState()==MAXIMIZED_BOTH){
                    if(f!=null)
                        f.setVisible(false);
                    if(r!=null)
                        r.setVisible(false);//makes the find and replace dialog to disappear when the main window in minimised
                }
                else{
                    if(f!=null)
                        f.setVisible(true);
                    if(r!=null)
                        r.setVisible(true);//makes the find and replace dialog to reappear when window regains its origininal state
                }
            }
            
        });
        
        tp.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getButton()==MouseEvent.BUTTON2 || e.getButton()==MouseEvent.BUTTON3){
                    pop.setLocation(e.getPoint());
                    pop.setVisible(true);
                }
            }
        });
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600,700);
        setTitle("MDI Notepad 1.0");
        setVisible(true);
    }
    
    /**
     * creates the close button for each tab at index i
     */
    void initTabComponent(int i) {
        tp.setTabComponentAt(i,
                 new ButtonTabComponent(tp,this));
    }
    
    /**
     * creates a new JEditorPane and attaches it to a new tab in JTabbedPane
     */
    void createEditor(){
        ep=new JEditorPane();
        ep.setSize(200000, 200000);
        ep.setFont(new Font("Monospaced",Font.PLAIN,12));
        JScrollPane scroll = new JScrollPane(ep,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                                //attaches the JScrollPane to the current
                                //JEditorPane
        currentEP=ep;
        currentFile="Untitled";
        tp.addTab("Untitled", scroll);
        
        /**
         * registers the created EditorPane to CaretListener to listen to CaretEvents
         * so that cut,delete and copy menu items are enabled only when something is selected
         * in the current EditorPane
         */
        ep.addCaretListener(new CaretListener(){
            @Override
            public void caretUpdate(CaretEvent e) {
                if(currentEP.getSelectedText()==null){
                    cut.setEnabled(false);
                    copy.setEnabled(false);
                    delete.setEnabled(false);
                }
                else{
                    cut.setEnabled(true);
                    copy.setEnabled(true);
                    delete.setEnabled(true);
                }
            }
            
        });
        /**
         * registers the JEditorPane to KeyListener to handle Key Events
         */
        ep.addKeyListener(new KeyAdapter(){
                                            @Override
                                            public void keyPressed(KeyEvent e){
                                                
                                                removeHighlights();
                                                    //removes all the currents highlights
                                                    //as soon as any key is pressed
                                                currentCarretPos=currentEP.getCaretPosition();
                                                
                                                    //currentCarretPos is updated 
                                                    //while text is being typed
                                                int code=e.getKeyCode();
                                                int docLength=currentEP.getDocument().getLength();
                                                
                                                /**
                                                 * the following if-else block displays current line and column position
                                                 * on the status bar
                                                 */
                                                if(code==KeyEvent.VK_ENTER){
                                                    currentLineVec.set(tp.getSelectedIndex(),(Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+1));
                                                    currentColVec.set(tp.getSelectedIndex(),1);
                                                    lineCountVec.set(tp.getSelectedIndex(), Integer.parseInt(lineCountVec.get(tp.getSelectedIndex()).toString())+1);
                                                }
                                                else if(code==KeyEvent.VK_BACK_SPACE){
                                                    if(Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())>1)
                                                    currentColVec.set(tp.getSelectedIndex(),Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())-1);
                                                }
                                                else if(code==KeyEvent.VK_UP){
                                                    if(Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())>1)
                                                    currentLineVec.set(tp.getSelectedIndex(),Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())-1);
                                                }
                                                else if(code==KeyEvent.VK_DOWN){
                                                    if(Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())<Integer.parseInt(lineCountVec.get(tp.getSelectedIndex()).toString()))
                                                    currentLineVec.set(tp.getSelectedIndex(),Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+1);
                                                }
                                                else if(code==KeyEvent.VK_LEFT){
                                                    currentColVec.set(tp.getSelectedIndex(),Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())-1);
                                                }
                                                else if(code==KeyEvent.VK_RIGHT){
                                                    if(docLength>currentCarretPos)
                                                     currentColVec.set(tp.getSelectedIndex(),Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())+1);
                                                }
                                                else if(code==KeyEvent.VK_HOME){
                                                    currentColVec.set(tp.getSelectedIndex(),1);
                                                }
                                                else if(code==KeyEvent.VK_END){
                                                }
                                                else if(code!=KeyEvent.VK_ALT && code!=KeyEvent.VK_CAPS_LOCK && code!=KeyEvent.VK_WINDOWS && code!=KeyEvent.VK_ESCAPE && code!=KeyEvent.VK_CONTROL && code!=KeyEvent.VK_PAGE_UP && code!=KeyEvent.VK_PAGE_DOWN && code!=KeyEvent.VK_DELETE && code!=KeyEvent.VK_F1 && code!=KeyEvent.VK_F2 && code!=KeyEvent.VK_F3 && code!=KeyEvent.VK_F4 && code!=KeyEvent.VK_F5 && code!=KeyEvent.VK_F6 && code!=KeyEvent.VK_F7 && code!=KeyEvent.VK_F8 && code!=KeyEvent.VK_F9 && code!=KeyEvent.VK_F10 && code!=KeyEvent.VK_F11 && code!=KeyEvent.VK_F12){
                                                    currentColVec.set(tp.getSelectedIndex(),Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())+1);
                                                }
                                                lineCount.setText("ln:"+Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+" col:"+Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString()));
                                                modifiedVec.set(tp.getSelectedIndex(), true); 
                                                modified=true;
                                                   
                                                saveStateVec.set(tp.getSelectedIndex(), true);
                                                saveAsStateVec.set(tp.getSelectedIndex(), true);
                                                undoStateVec.set(tp.getSelectedIndex(), true);
                                                redoStateVec.set(tp.getSelectedIndex(), true);
                                                findStateVec.set(tp.getSelectedIndex(), true);
                                                    
                                                save.setEnabled(true);
                                                saveAs.setEnabled(true);
                                                undoAction.setEnabled(true);
                                                redoAction.setEnabled(true);
                                                find.setEnabled(true);
                                                findNext.setEnabled(true);
                                                        //menu item states are updated in the vector
                                                        //for the current tab
                                                        //and simultaneously applied to eah of them
                                            }
                                            
                                            /**
                                             * the following code works in association with keyPressedEvent to get the correct column number
                                             * to be displayed on the status bar when END key is pressed and released
                                             */
                                            @Override
                                            public void keyReleased(KeyEvent e){
                                                latterCarretPos=currentEP.getCaretPosition();//the variable now contains the new caret position after the end key has been released
                                                if(e.getKeyCode()==KeyEvent.VK_END){
                                                    currentColVec.set(tp.getSelectedIndex(), Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString())+latterCarretPos-currentCarretPos);
                                                    lineCount.setText("ln:"+Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+" col:"+Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString()));
                                                }
                                            }
                                            });
        
        /**
         * registers the JEditorPane to MouseListener as well to handle Mouse
         * Events
         */
        ep.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                removeHighlights();
                
                currentCarretPos=currentEP.getCaretPosition();
                String str=currentEP.getText();
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
                int l=0,c=0;
                for(int i=0;i<currentCarretPos;++i){//this for loop traverses the String str and updates the l and c variables whenever return character is found.
                                                    //l is to contain the new line no. and c the column no.
                    
                    c++;
                    if(str.charAt(i)=='\r'){
                        l++;
                        c=0;
                    }
                    
                }
                
                currentLineVec.set(tp.getSelectedIndex(), l+1);
                currentColVec.set(tp.getSelectedIndex(), c+1);
                lineCount.setText("ln:"+Integer.parseInt(currentLineVec.get(tp.getSelectedIndex()).toString())+" col:"+Integer.parseInt(currentColVec.get(tp.getSelectedIndex()).toString()));
                try{
                f.setPos(currentCarretPos);//sets the currentCarretPos to private variable
                                          //pos in Find class
                }
                catch(Exception ex){
                }
                try{
                r.rSetPos(currentCarretPos);
                }
                catch(Exception ex){   
                }
            }
        });
        
        m_undoVec.addElement(new UndoManager());
        m_undo=(UndoManager)m_undoVec.elementAt(tp.getTabCount()-1);
        /**
         * registers the respective Document object of the JEditorPane to UndoableEditListener
         * to implement undo/redo feature
         */
        ep.getDocument().addUndoableEditListener(new UndoableEditListener(){
            @Override
            public void undoableEditHappened(UndoableEditEvent e){
              m_undo.addEdit(e.getEdit());
              undoAction.update();
              redoAction.update();
              undoStateVec.setElementAt(undoAction.isEnabled(), tp.getSelectedIndex());
              redoStateVec.setElementAt(redoAction.isEnabled(), tp.getSelectedIndex());
            }
        });
        
        modifiedVec.addElement(false);
        
        currentLineVec.addElement(1);
        currentColVec.addElement(1);
        lineCountVec.addElement(1);
        
        saveStateVec.addElement(false);
        saveAsStateVec.addElement(false);
        undoStateVec.addElement(false);
        redoStateVec.addElement(false);
        findStateVec.addElement(false);
        
        save.setEnabled(false);
        saveAs.setEnabled(false);

        undoAction.setEnabled(false);
        redoAction.setEnabled(false);
        
        find.setEnabled(false);
        findNext.setEnabled(false);
        
        cut.setEnabled(false);
        copy.setEnabled(false);
        delete.setEnabled(false);
                    //whenever a new tab is created a new state variable with false value 
                    //is pushed into each state vector representing the disabled current state
                    //of the menu items
    }
    
    /**
     * gateway to access currentCarretPos
     */
    int getCurrentcaretPosition(){
        return currentCarretPos;
    }
    
    /*
     * reads the respective file and displays in the current JEditorPane
     */
    void readFile(String fileName){
        try{
            FileReader r=new FileReader(fileName);
            currentEP.read(r, null);
            r.close();
            currentFile=fileName; 
            modified=false;
            //setTitle(fileName);
        }
        catch (IOException e){
            Toolkit.getDefaultToolkit().beep();//plays a beep
            JOptionPane.showMessageDialog(this, "The editor cannot find the chosen file:"+fileName);//shows the respective message
        }
    }
    
    /**
     * writes the current file to disk 
     */
    void saveFile(String fileName){
        try{
            FileWriter w=new FileWriter(fileName);
            currentEP.write(w);
            w.close();
            modifiedVec.set(tp.getSelectedIndex(), false); 
            modified=false;
            currentFile=fileName;
            //setTitle(fileName);
        }
        catch(IOException e){
            
        }
    }
    
    /**
     * asks the user whether the file being closed needs to be saved or not
     */
    boolean savePrev(){
        int flag;
        if(modified==true){
            flag=JOptionPane.showConfirmDialog(null, "Do you want to save the previous document"+" '"+currentFile+"' "+"?");
            if(flag==JOptionPane.OK_OPTION){
                if(!currentFile.equals("Untitled") && modified==true){
                    saveFile(currentFile);
                } 
                else if(currentFile.equals("Untitled") && fc.showSaveDialog(null) ==JFileChooser.APPROVE_OPTION){
                saveFile(fc.getSelectedFile().getAbsolutePath());
                }
            
            }
            if(flag==JOptionPane.CANCEL_OPTION){
                return false;
            }
        }
        return true;
    }
    
    /**
     * main() method to run the application
     */
    public static void main(String args[]){
        new TextEditor();
    }
    
    /**
     * removes the highlights from the current JEditorPane
     */
    private void removeHighlights() {
        Highlighter hilite = currentEP.getHighlighter();
        Highlighter.Highlight[] hilites = hilite.getHighlights();

        for (int i=0; i<hilites.length; i++) {
            if (hilites[i].getPainter() instanceof MyHighlightPainter) {
                hilite.removeHighlight(hilites[i]);
            }
        }
    }
    
    /**
     * handles all the generated Action Events
     */
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource()==open){
            if(fc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION){
                createEditor();
                readFile(fc.getSelectedFile().getAbsolutePath());
                tp.setTitleAt(tp.getTabCount()-1, currentFile);
                
                saveStateVec.set(tp.getTabCount()-1, false);
                saveAsStateVec.set(tp.getTabCount()-1, true);
                undoStateVec.set(tp.getTabCount()-1, false);
                redoStateVec.set(tp.getTabCount()-1, true);
                findStateVec.set(tp.getTabCount()-1, true);
                
                save.setEnabled(false);
                saveAs.setEnabled(true);
                find.setEnabled(true);
                findNext.setEnabled(true);
                                        //all the menu items except saveAs is disabled
                
                currentEP.getDocument().addUndoableEditListener(new UndoableEditListener(){
                @Override
                public void undoableEditHappened(UndoableEditEvent e){
                    m_undo.addEdit(e.getEdit());
                    undoAction.update();
                    redoAction.update();
                    undoStateVec.setElementAt(undoAction.isEnabled(), tp.getSelectedIndex());
                    redoStateVec.setElementAt(redoAction.isEnabled(), tp.getSelectedIndex());
                    }
                });
                    //current editor pane 'currentEP' is registered to UndoableEditListener
                
                tp.setSelectedIndex(tp.getTabCount()-1);//the tab containing the opened document is activated
                initTabComponent(tp.getTabCount()-1);//close button is created in the new tab
            }
        }
        
        else if(e.getSource()==save){
            if(!currentFile.equals("Untitled") && modified==true){
                    saveFile(currentFile);//saves the file with the existing name only
                } 
                else if(currentFile.equals("Untitled") && fc.showSaveDialog(null) ==JFileChooser.APPROVE_OPTION){
                saveFile(fc.getSelectedFile().getAbsolutePath());
                                    //since current filename is 'Untitled' save the file
                                    //with the name solicited from the user
               saveStateVec.set(tp.getSelectedIndex(), false);
               saveAsStateVec.set(tp.getSelectedIndex(), true);
        
                save.setEnabled(false);
                saveAs.setEnabled(true);
                }
            tp.setTitleAt(tp.getSelectedIndex(), currentFile);
        }
        
        else if(e.getSource()==saveAs){
            if(fc.showSaveDialog(null)==JFileChooser.APPROVE_OPTION){
                saveFile(fc.getSelectedFile().getAbsolutePath());
            }
            tp.setTitleAt(tp.getSelectedIndex(), currentFile);
        }
        
        else if(e.getSource()==nnew){
                createEditor();
                currentFile="Untitled";
                modified=false;
               
                tp.setSelectedIndex(tp.getTabCount()-1);
                //to make the new Untitled tab activated
                
                currentEP.setText("");
               
               initTabComponent(tp.getTabCount()-1);
        }
        
                
        else if(e.getSource()==exit){
            while(tp.getTabCount()>0){
                    currentFile=tp.getTitleAt(0);
                    modified=Boolean.parseBoolean(modifiedVec.get(0).toString());
                    if(savePrev()){
                        tp.remove(0);
                        modifiedVec.remove(0);
                    }
                }
            System.exit(0);
                    //destroys and closes the current application
        }
        
        else if(e.getSource()==print){
            MessageFormat header=new MessageFormat("copyright: Dhruva Bhaswar");//appears at the top of the
                                                                                //printed doc.
            MessageFormat footer=new MessageFormat("http:\\www.dhruvabhaswar.co.in");//appears at the bottom
                                                                                    //of the printed doc.
            try{
            currentEP.print(header, footer);//opens up the print dialog
            }
            catch (PrinterException ex){
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,"Error occurred during printing request processing...");
            }
        }
        
        else if(e.getSource()==cut){
            currentEP.cut();//cuts the selected text and transfers the string to clipboard
        }
        
        else if(e.getSource()==copy){
            currentEP.copy();//copies the selected text to the clipboard
        }
        
        else if(e.getSource()==paste){
            currentEP.paste();//pastes the current content of the clipboard at the current
                              //caret position
        }
        
        else if(e.getSource()==delete){
            currentEP.replaceSelection("");//deletes the selected text
        }
        
        else if(e.getSource()==find){
            f=new Find(currentEP,this);
                        //instantiate the Find class which implements the find feature
                        //Find dialog is shown on the screen
        }
                
        else if(e.getSource()==selectAll){
            currentEP.selectAll();//selects all the content of the current JEditorPane
        }
                
        else if(e.getSource()==timeDate){
            Date d=new Date();
            currentEP.setText(d.toString());
                                //prints the system date
        }
                
        else if(e.getSource()==ggoto){
            new Goto(currentEP,this);
        }
        
        else if(e.getSource()==findNext){
            if(f!=null){
                f.highlight(f.ep, f.enterWhat.getText());
            }
            else{
                f=new Find(currentEP,this);
            }
                //implements the 'find next' feature
        }
        
        else if(e.getSource()==replace){
            r=new Replace(currentEP,this);
                            //instantiate the Replace class which implements the 'replace' feature
                            //Replace dialog is displayed on the screen
        }
                
        else if(e.getSource()==font){
            new Format(currentEP);
                            //Font dialog is displayed on the screen 
                            //appropriate Font parameters can be chosen from the dialog
        }
        
        else if(e.getSource()==color){
            new ColorChooser(currentEP);
        }
        
        else if(e.getSource()==statusBar){
            if(status.isVisible()){
                status.setVisible(false);
            }
            else{
                status.setVisible(true);
            }
        }
                
        else if(e.getSource()==about){
            new About();//invokes About dialog
        }
        
        else if(e.getSource()==viewHelp){
            
            try{
            Desktop.getDesktop().open(new File("javadoc/index.html"));//to invoke javadoc
            }
            catch(Exception ex){
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "No help file available!\nEither javadoc is unavailable or java6 is not installed.");
            }
            
        }
        
        else if(e.getSource()==closeAll){
            while(tp.getTabCount()>0){
                    currentFile=tp.getTitleAt(0);
                    modified=Boolean.parseBoolean(modifiedVec.get(0).toString());
                    if(savePrev()){
                        tp.remove(0);
                        modifiedVec.remove(0);
                    }
                }
            pop.setVisible(false);
            createEditor();
        }
    }

/**
* a class for handling undo action
*/
class UndoAction extends AbstractAction
{
  public UndoAction()
  {
    super("Undo");
    setEnabled(false);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  { 
    try
    {
      m_undo.undo();
    }
    catch (CannotUndoException ex)
    {
        Toolkit.getDefaultToolkit().beep();
    }
    update();
    redoAction.update();
  }

  protected void update()
  {
    if (m_undo.canUndo())
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }
}

/**
 * a class for handling redo action
 */
class RedoAction extends AbstractAction
{
  public RedoAction()
  {
    super("Redo");
    setEnabled(false);
  }

  @Override
  public void actionPerformed(ActionEvent e)
  {
    try
    {
      m_undo.redo();
    }
    catch (CannotRedoException ex)
    {
        Toolkit.getDefaultToolkit().beep();
    }
    update();
    undoAction.update();
  }

  protected void update()
  {
    if (m_undo.canRedo())
    {
      setEnabled(true);
    }
    else
    {
      setEnabled(false);
    }
  }
}

}
