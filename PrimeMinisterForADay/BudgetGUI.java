package PrimeMinisterForADay;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BudgetGUI extends JFrame {

    private JComboBox<String> categoryCombo;
    private JTextField amountField;
    private DefaultListModel<String> listModel;
    private JLabel totalLabel;
    private double total = 0.0;

    private BudgetData budgetData;

    public BudgetGUI(BudgetData budgetData) {
        this.budgetData = budgetData;

        setTitle("Budget Sphere");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color primaryBlue = new Color(17, 63, 103);   // #113f67
        Color accentBlue = new Color(27, 108, 168);   // #1b6ca8
        Color lightBlue  = new Color(167, 208, 228);  // #a7d0e4
        Color white      = Color.WHITE;

        JPanel panel = new JPanel();
        panel.setBackground(primaryBlue);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        JLabel title = new JLabel("Budget Sphere");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setForeground(white);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        panel.add(Box.createVerticalStrut(20));
        panel.add(title);

        JLabel subtitle = new JLabel("Διαχείριση Κρατικού Προϋπολογισμού");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setForeground(lightBlue);
        subtitle.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(20));

        // Input section
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBackground(primaryBlue);

        JLabel categoryLabel = new JLabel("Κατηγορία:");
        categoryLabel.setForeground(white);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(categoryLabel);

        categoryCombo = new JComboBox<>();
        for (String cat : budgetData.getCategories().keySet()) {
            categoryCombo.addItem(cat);
        }
        inputPanel.add(categoryCombo);

        JLabel amountLabel = new JLabel("Ποσό (EUR):");
        amountLabel.setForeground(white);
        amountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(amountLabel);

        amountField = new JTextField();
        inputPanel.add(amountField);

        panel.add(inputPanel);

        // Buttons
        JButton addBtn = new JButton("Προσθήκη / Τροποποίηση");
        addBtn.setBackground(accentBlue);
        addBtn.setForeground(white);
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        addBtn.addActionListener(e -> addTransaction());
        panel.add(Box.createVerticalStrut(20));
        panel.add(addBtn);

        JButton saveBtn = new JButton("Αποθήκευση στη βάση");
        saveBtn.setBackground(accentBlue);
        saveBtn.setForeground(white);
        saveBtn.setFont(new Font("Arial", Font.BOLD, 16));
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.addActionListener(e -> saveChanges());
        panel.add(Box.createVerticalStrut(10));
        panel.add(saveBtn);

        // List
        listModel = new DefaultListModel<>();
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        panel.add(Box.createVerticalStrut(20));
        panel.add(scrollPane);

        // Total
        totalLabel = new JLabel("Σύνολο: 0.00 EUR");
        totalLabel.setForeground(lightBlue);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createVerticalStrut(20));
        panel.add(totalLabel);

        refreshList();

        setVisible(true);
    }

    private void addTransaction() {
        try {
            String category = (String) categoryCombo.getSelectedItem();
            if (category == null) return;

            double amount = Double.parseDouble(amountField.getText());
            if (amount < 0) throw new NumberFormatException();

            // Update BudgetData
            budgetData.getCategories().put(category, amount);
            budgetData.changes.put(category, amount);

            refreshList();
            amountField.setText("");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Το ποσό πρέπει να είναι θετικός αριθμός!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshList() {
        listModel.clear();
        total = 0.0;
        for (Map.Entry<String, Double> entry : budgetData.getCategories().entrySet()) {
            String line = String.format("%s : %.2f EUR", entry.getKey(), entry.getValue());
            listModel.addElement(line);
            total += entry.getValue();
        }
        totalLabel.setText(String.format("Σύνολο: %.2f EUR", total));
    }

    private void saveChanges() {
        budgetData.saveToDB();
        JOptionPane.showMessageDialog(this, "Οι αλλαγές αποθηκεύτηκαν στη βάση δεδομένων.",
                "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
        refreshList();
    }
}
