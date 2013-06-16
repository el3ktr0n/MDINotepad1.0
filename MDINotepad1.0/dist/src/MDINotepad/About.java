/*
  @(#)About.java	04-10-2011
 */
package MDINotepad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
/**
 * @author Dhruva Bhaswar
 */
public class About extends JDialog implements ActionListener{
                //the class to invoke About dialog
    private JButton close;
    
    public About() {

        initUI();
    }

    public final void initUI() {

        JPanel basic = new JPanel();
        basic.setLayout(new BoxLayout(basic, BoxLayout.Y_AXIS));
        add(basic);

        JPanel topPanel = new JPanel(new BorderLayout(0,0));
        topPanel.setMaximumSize(new Dimension(450, 0));
        JLabel hint = new JLabel("MDI Notepad 1.0");
        hint.setFont(new Font("Century",Font.BOLD,24));
        hint.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
        topPanel.add(hint);

        ImageIcon icon = new ImageIcon("Resources/Capture.PNG");
        JLabel label = new JLabel(icon);
		label.setSize(10,10);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.add(label, BorderLayout.EAST);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.gray);

        topPanel.add(separator, BorderLayout.SOUTH);

        basic.add(topPanel);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        JTextPane pane = new JTextPane();

        pane.setContentType("text/html");
        String text = "<b>MDI Notepad</b>" +
            "<br>version 1.0" +
            "<br><br>copyright (c) 2011"+
            "<br>Dhruva Bhaswar"+
             "<br> BE/1194/2010"+
             "<br>Birla Institute of Technology";
        pane.setText(text);
        pane.setEditable(false);
        textPanel.add(pane);

        basic.add(textPanel);

        JPanel boxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));

        JLabel box = new JLabel("");

        boxPanel.add(box);
        basic.add(boxPanel);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JLabel box1 = new JLabel("");
        close = new JButton("Close");

        bottom.add(close);
        close.addActionListener(this);
        bottom.add(box1);
        basic.add(bottom);

        bottom.setMaximumSize(new Dimension(450, 0));

        setTitle("About");
        setSize(new Dimension(450, 400));
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
         if(e.getSource()==close){
             this.dispose();
         }
    }
}
