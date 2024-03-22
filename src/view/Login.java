package view;

import db.User;
import db.Users;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static db.FileIO.readUsers;
import static db.FileIO.writeUsers;
import static java.lang.System.exit;

public class Login {
    private JFrame frame;
    private JPasswordField passwordField1;
    private JTextField textField1;
    private JPanel loginPanel;
    private JButton exitButton;
    private JButton loginButton;

    public Login() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                Users users = readUsers();
                if(users == null){
                    users = new Users();
                    writeUsers(users);
                }
                String username = textField1.getText();
                String password = String.valueOf(passwordField1.getPassword());
                User user = users.getUserByLogin(username,password);
                if(user == null){
                    JOptionPane.showMessageDialog(null, "用户名或密码错误");
                }else{
                    JFrame mainFrame = new JFrame("DBMS");
                    MainForm mainForm = new MainForm(user,mainFrame);
                    mainFrame.setContentPane(mainForm.getRoot());
                    mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    mainFrame.pack();
                    mainFrame.setLocationRelativeTo(null);
                    mainFrame.setVisible(true);
                    frame.dispose();
                }


            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit(0);
            }
        });
    }

    public static void main(String[] args) {
        Login login = new Login();
        login.frame = new JFrame("Login");
        login.frame.setContentPane(login.loginPanel);
        login.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        login.frame.pack();
        login.frame.setLocationRelativeTo(null); // 窗口居中
        login.frame.setVisible(true);
    }
}
