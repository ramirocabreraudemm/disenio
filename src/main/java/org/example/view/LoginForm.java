package org.example.view;

import org.example.model.User;
import org.example.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginForm extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JButton btnOK;
    private JButton btnCancel;
    private JPanel loginPanel;
    private JLabel lblRegister;

    private UserService userService;

    private User user;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450,474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        userService = new UserService();

        btnOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = tfUsername.getText();
                String password = String.valueOf(pfPassword.getPassword());

                if (userService.isBlocked(username)) {
                    long remainingLockTime = userService.getRemainingLockTime(username);
                    JOptionPane.showMessageDialog(LoginForm.this,
                            "Usuario temporalmente bloqueado. Intente nuevamente en " + remainingLockTime / 1000 + " segundos.",
                            "Usuario Bloqueado",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                user = userService.findUserByUsernameAndPassword(username, password);

                if(user != null){
                    dispose();
                    new MainForm(user);
                }
                else{
                    int remainingAttempts = userService.getRemainingAttempts(username);

                    if (remainingAttempts > 0) {
                        JOptionPane.showMessageDialog(LoginForm.this,
                                "Usuario o contraseña inválidos. Tiene " + remainingAttempts + " intentos.",
                                "Intente nuevamente",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this,
                                "Usuario bloqueado",
                                "Intente nuevamente",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        lblRegister.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                dispose();
                RegisterForm registerForm = new RegisterForm(null);
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                userService.close();
            }
        });

        setVisible(true);
    }

}
