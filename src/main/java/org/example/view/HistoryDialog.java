package org.example.view;

import org.example.model.Student;
import org.example.model.User;
import org.example.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HistoryDialog extends JDialog {
    private JPanel historyPanel;
    private JTable historyTable;
    private JScrollPane scrollPane;
    private DefaultTableModel tableModel;
    private UserService userService;

    private static final String[] COLUMN_NAMES = {"Nombre", "Apellido", "Nombre Materia", "Nota"};

    public HistoryDialog(JFrame parent, User user) {
        super(parent, "Historial", true);
        userService = new UserService();

        setTitle("Historial persistido");
        setContentPane(historyPanel);
        setMinimumSize(new Dimension(500, 300));
        setLocationRelativeTo(parent);

        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        historyTable.setModel(tableModel);
        scrollPane.setViewportView(historyTable);

        loadHistory(user);

        setVisible(true);
    }

    private void loadHistory(User user) {
        List<Student> students = userService.getStudentsByUser(user);
        for (Student student : students) {
            tableModel.addRow(new Object[]{student.getName(), student.getSurname(), student.getSubjectName(), student.getGrade()});
        }
    }
}
