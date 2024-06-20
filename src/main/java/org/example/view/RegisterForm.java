package org.example.view;

import org.example.model.User;
import org.example.service.UserService;
import org.example.validation.CommonPasswordValidation;
import org.example.validation.ComplexityValidation;
import org.example.validation.LengthValidation;
import org.example.validation.PasswordValidator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterForm extends JDialog {
    private JTextField tfUsername;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JPanel registerPanel;
    private JLabel lblLogin;

    private UserService userService;

    public RegisterForm(JFrame parent) {
        super(parent, "Registrarse", true);
        setTitle("Registrarse");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        userService = new UserService();

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        lblLogin.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                LoginForm loginForm = new LoginForm(null);
            }
        });
        setVisible(true);
    }

    private void registerUser() {
        String username = tfUsername.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PasswordValidator passwordValidator = new PasswordValidator();
        passwordValidator.addStrategy(new LengthValidation(12));
        passwordValidator.addStrategy(new ComplexityValidation());
        passwordValidator.addStrategy(new CommonPasswordValidation("common-passwords.txt"));
        passwordValidator.addStrategy(new CommonPasswordValidation("top-10000-passwords.txt"));

        if (!passwordValidator.validate(password)) {
            JOptionPane.showMessageDialog(this, "La contraseña no cumple con los criterios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);

        if (userService.createUser(newUser)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado con éxito", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            LoginForm loginForm = new LoginForm(null);
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
