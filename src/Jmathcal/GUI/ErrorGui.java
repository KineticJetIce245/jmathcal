package Jmathcal.GUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridBagLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ErrorGui {
    public static void launch(String msg) {
        JFrame jf = new JFrame("There is a problem");

        JPanel msgPanel = new JPanel(new GridBagLayout());
        JLabel errMsg = new JLabel(msg);
        errMsg.setFont(new Font("Arial", Font.PLAIN, 16));
        msgPanel.add(errMsg);
        msgPanel.setPreferredSize(new Dimension(errMsg.getPreferredSize().width+10, errMsg.getPreferredSize().height));

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

        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setLayout(new BoxLayout(jf.getContentPane(), BoxLayout.PAGE_AXIS));

        jf.add(msgPanel);
        jf.add(Box.createVerticalGlue());
        jf.add(butPanel);

        jf.pack();
        jf.setMinimumSize(new Dimension(400, 200));
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}
