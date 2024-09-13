package com.example.autopark;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

public class AutoparkApp extends JFrame {
    private static final String SAVE_FILE = "cars.txt";

    private JTable carTable;
    private DefaultTableModel carTableModel;
    private JTextField makeInput;
    private JTextField modelInput;
    private JTextField yearInput;
    private JTextField colorInput;
    private List<String[]> cars;

    public AutoparkApp() {
        super("Автопарк");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1020);
        setLocationRelativeTo(null);

        cars = loadCars();

        carTableModel = new DefaultTableModel(new String[] {"Марка", "Модель", "Год", "Цвет"}, 0);
        updateCarTable();
        carTable = new JTable(carTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true; // Разрешаем редактирование ячеек
            }
        };
        carTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        makeInput = new JTextField(20);
        modelInput = new JTextField(20);
        yearInput = new JTextField(4);
        colorInput = new JTextField(20);
        JButton addButton = new JButton("Добавить автомобиль");
        JButton deleteButton = new JButton("Удалить автомобиль");
        JButton clearButton = new JButton("Очистить список");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String make = makeInput.getText().trim();
                String model = modelInput.getText().trim();
                String year = yearInput.getText().trim();
                String color = colorInput.getText().trim();
                if (!make.isEmpty() && !model.isEmpty() && !year.isEmpty() && !color.isEmpty()) {
                    cars.add(new String[] {make, model, year, color});
                    updateCarTable();
                    makeInput.setText("");
                    modelInput.setText("");
                    yearInput.setText("");
                    colorInput.setText("");
                } else {
                    JOptionPane.showMessageDialog(AutoparkApp.this, "Введите все данные об автомобиле.");
                }
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = carTable.getSelectedRow();
                if (selectedRow != -1) {
                    cars.remove(selectedRow);
                    updateCarTable();
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cars.clear();
                updateCarTable();
            }
        });

        JPanel panel = new JPanel();
        panel.add(makeInput);
        panel.add(modelInput);
        panel.add(yearInput);
        panel.add(colorInput);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(clearButton);

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(carTable), BorderLayout.CENTER);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveCars();
            }
        });
    }

    private void updateCarTable() {
        carTableModel.setRowCount(0);
        for (String[] car : cars) {
            carTableModel.addRow(car);
        }
    }

    private void saveCars() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            for (String[] car : cars) {
                writer.println(String.join(",", car));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String[]> loadCars() {
        List<String[]> cars = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] carData = line.split(",");
                if (carData.length == 4) {
                    cars.add(carData);
                } else {
                    // Если в строке меньше 4 элементов, добавляем пустой цвет
                    cars.add(new String[] {carData[0], carData[1], carData[2], ""});
                }
            }
        } catch (IOException e) {
            // Если файл не существует, это нормально, просто возвращаем пустой список
        }
        return cars;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AutoparkApp().setVisible(true);
            }
        });
    }
}
