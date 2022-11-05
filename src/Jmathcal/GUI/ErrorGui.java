package Jmathcal.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;

import javax.swing.Box;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorGui {
    public static void launch(String msg) {
        JFrame jf = new JFrame("There is a problem");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Box boxLayout = Box.createVerticalBox(); 
        jf.add(boxLayout);

        JPanel msgPanel = new JPanel(new GridBagLayout());
        JLabel errMsg = new JLabel(msg);
        errMsg.setFont(new Font("Arial", Font.PLAIN, 16));
        msgPanel.add(errMsg);

        JPanel butPanel = new JPanel(new GridBagLayout());
        JButton closeButton = new JButton("Close");
        closeButton.setSize(new Dimension(35,25));
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jf.dispose();
            }
        });
        butPanel.add(closeButton);

        boxLayout.setPreferredSize(msgPanel.getPreferredSize());

        boxLayout.add(msgPanel);
        boxLayout.add(Box.createVerticalGlue());
        boxLayout.add(butPanel);

        jf.pack();
        jf.setMinimumSize(new Dimension(400,250));
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
    public static void main(String[] args) {
        launch("asdfkjashdkjssssssssssssjkd");
    }
}
