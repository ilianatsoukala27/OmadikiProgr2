package PrimeMinisterForADay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class BudgetGUI {

    private BudgetManager manager;
    private BudgetData data;

    public BudgetGUI(BudgetManager manager) {
        this.manager = manager;
        this.data = manager.getBudgetData();
        createWindow();
    }

    private void createWindow() {
        JFrame frame = new JFrame("Διαχείριση Προϋπολογισμού");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(6, 1, 10, 10));

        JButton btnShowBudget = new JButton("Δες τον προϋπολογισμό");
        JButton btnModify = new JButton("Τροποποίησε κατηγορία");
        JButton btnShowChanges = new JButton("Δες αλλαγές");
        JButton btnSave = new JButton("Αποθήκευση στη βάση");
        JButton btnExit = new JButton("Έξοδος");

        btnShowBudget.addActionListener(e -> {
            JTextArea text = new JTextArea(20, 40);
            text.setText(getBudgetAsText());
            text.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(text);
            JOptionPane.showMessageDialog(null, scrollPane, "Προϋπολογισμός", JOptionPane.INFORMATION_MESSAGE);
        });

        btnModify.addActionListener(e -> modifyCategoryGUI());

        btnShowChanges.addActionListener(e -> {
            JTextArea text = new JTextArea(20, 40);
            text.setText(getChangesAsText());
            text.setEditable(false);

            JScrollPane scrollPane = new JScrollPane(text);
            JOptionPane.showMessageDialog(null, scrollPane, "Αλλαγές", JOptionPane.INFORMATION_MESSAGE);
        });

        btnSave.addActionListener(e -> {
            data.saveToDB();
            JOptionPane.showMessageDialog(null, "Οι αλλαγές αποθηκεύτηκαν!");
        });

    
        btnExit.addActionListener((ActionEvent e) -> System.exit(0));

    
        frame.add(btnShowBudget);
        frame.add(btnModify);
        frame.add(btnShowChanges);
        frame.add(btnSave);
        frame.add(btnExit);

        frame.setVisible(true);
    }

    private String getBudgetAsText() {
        StringBuilder sb = new StringBuilder("======= ΚΡΑΤΙΚΟΣ ΠΡΟΫΠΟΛΟΓΙΣΜΟΣ =======\n");
        for (var entry : data.getCategories().entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" €\n");
        }
        return sb.toString();
    }

    private String getChangesAsText() {
        StringBuilder sb = new StringBuilder("======= ΑΛΛΑΓΕΣ =======\n");
        if (data.getCategories().isEmpty()) {
            sb.append("Δεν υπάρχουν αλλαγές.");
            return sb.toString();
        }
        for (var entry : data.getCategories().entrySet()) {
            sb.append(entry.getKey()).append(" : ").append(entry.getValue()).append(" €\n");
        }
        return sb.toString();
    }

    private void modifyCategoryGUI() {
        String category = JOptionPane.showInputDialog("Γράψε την κατηγορία που θέλεις να αλλάξεις:");

        if (category == null || category.isBlank()) return;

        if (!data.getCategories().containsKey(category)) {
            JOptionPane.showMessageDialog(null, "Η κατηγορία δεν βρέθηκε.");
            return;
        }

        String newAmountStr = JOptionPane.showInputDialog("Δώσε νέο ποσό για " + category + ":");

        try {
            double newAmount = Double.parseDouble(newAmountStr);
            data.getCategories().put(category, newAmount);

            JOptionPane.showMessageDialog(null,
                    "Η κατηγορία ενημερώθηκε σε " + newAmount + "€");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Μη έγκυρο ποσό.");
        }
    }
}
