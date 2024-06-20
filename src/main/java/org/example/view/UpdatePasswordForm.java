package org.example.view;

import org.example.model.User;
import org.example.service.UserService;
import org.example.util.PasswordUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdatePasswordForm extends JDialog {
    private JPasswordField pfNewPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnUpdate;
    private JButton btnCancel;
    private JPanel updatePasswordPanel;
    private UserService userService;
    private User user;

    public UpdatePasswordForm(JFrame parent, User user) {
        super(parent, "Actualizar contraseña", true);
        this.user = user;
        userService = new UserService();

        setTitle("Actualizar contraseña");
        setContentPane(updatePasswordPanel);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePassword();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void updatePassword() {
        String newPassword = String.valueOf(pfNewPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userService.updatePassword(user, newPassword)) {
            JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar la contraseña", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
