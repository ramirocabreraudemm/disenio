package org.example.view;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.User;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.example.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainForm extends JFrame {
    private JPanel mainPanel;
    private JButton btnUpload;
    private JLabel lblFileName;
    private JTable table;
    private JScrollPane scrollPane;
    private JButton btnSave;
    private JButton btnHistory;
    private DefaultTableModel tableModel;
    private JPanel upperPanel;
    private JLabel lblPasswordRotation;
    private JButton btnUpdatePassword;

    private UserService userService;
    private User user;
    private static final String[] COLUMN_NAMES = {"Nombre", "Apellido", "Nombre Materia", "Nota"};

    public MainForm(User user) {
        this.user = user;
        userService = new UserService();

        setTitle("Principal");
        setContentPane(mainPanel);
        setMinimumSize(new Dimension(600, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        upperPanel.setLayout(new BorderLayout());

        upperPanel.add(lblPasswordRotation, BorderLayout.WEST);

        upperPanel.add(btnUpdatePassword, BorderLayout.EAST);

        btnUpdatePassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UpdatePasswordForm(MainForm.this, user);
            }
        });


        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollPane.setViewportView(table);


        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Archivos PDF, XLS, CSV", "pdf", "xls", "xlsx", "csv"));
                int option = fileChooser.showOpenDialog(MainForm.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    lblFileName.setText("Archivo: " + selectedFile.getName());
                    try {
                        loadFile(selectedFile);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainForm.this, "Error cargando el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSelectedRows();
            }
        });

        btnHistory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new HistoryDialog(MainForm.this, user);
            }
        });

        updatePasswordRotationLabel();

        setVisible(true);
    }

    private void loadFile(File file) throws Exception {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".pdf")) {
            loadPdf(file);
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            loadXls(file);
        } else if (fileName.endsWith(".csv")) {
            loadCsv(file);
        } else {
            throw new IllegalArgumentException("Tipo de archivo no soportado");
        }
    }

    private void loadPdf(File file) throws Exception {
        PDDocument document = PDDocument.load(file);
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String text = pdfStripper.getText(document);
        document.close();

        String[] lines = text.split("\\r?\\n");
        tableModel.setRowCount(0);
        for (int i = 0; i < Math.min(10, lines.length); i++) {
            String[] columns = lines[i].split("\\s+");
            if (columns.length >= 4) {
                tableModel.addRow(new Object[]{columns[0], columns[1], columns[2], Integer.parseInt(columns[3])});
            }
        }
    }

    private void loadXls(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);
        tableModel.setRowCount(0);
        int rowCount = Math.min(10, sheet.getPhysicalNumberOfRows());
        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                String[] columns = new String[4];
                for (int j = 0; j < 4; j++) {
                    Cell cell = row.getCell(j);
                    columns[j] = cell != null ? cell.toString() : "";
                }
                tableModel.addRow(columns);
            }
        }
        workbook.close();
        fis.close();
    }

    private void loadCsv(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        tableModel.setRowCount(0);
        String line;
        int rowCount = 0;
        while ((line = br.readLine()) != null && rowCount < 10) {
            String[] columns = line.split(",");
            if (columns.length >= 4) {
                tableModel.addRow(new Object[]{columns[0], columns[1], columns[2], Integer.parseInt(columns[3])});
                rowCount++;
            }
        }
        br.close();
    }

    private void saveSelectedRows() {
        int[] selectedRows = table.getSelectedRows();

        if (selectedRows.length < 3) {
            JOptionPane.showMessageDialog(this, "Por favor seleccione 3 filas.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Object[]> rowsData = new ArrayList<>();
        for (int row : selectedRows) {
            Object[] rowData = new Object[4];
            for (int col = 0; col < 4; col++) {
                rowData[col] = tableModel.getValueAt(row, col);
            }
            rowsData.add(rowData);
        }

        if (userService.saveStudents(user, rowsData)) {
            JOptionPane.showMessageDialog(this, "Filas persistidas con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error al persistir las filas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updatePasswordRotationLabel() {
        Date lastChanged = user.getPasswordChangedAt();
        long diff = 0;
        if (lastChanged != null) {
            long diffInMillies = Math.abs(new Date().getTime() - lastChanged.getTime());
            diff = 90 - TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        }
        lblPasswordRotation.setText("Días para rotar contraseña: " + diff);
    }

}
