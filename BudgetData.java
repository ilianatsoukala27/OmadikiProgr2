package PrimeMinisterForADay;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale.Category;
import java.util.Scanner;

public class BudgetData {

    List<String, Double> categories;
    private List<String, Double> changes;
    private int budgetYear;
    private String dbUrl;

    public BudgetData() {
        this(2025); // default budget year
    }

    public BudgetData(int budgetYear) {
        this.budgetYear = budgetYear;
        this.categories = new HashMap<>();
        this.changes = new HashMap<>();

        // ✔ ΔΙΟΡΘΩΣΗ 1: σωστό SQLite JDBC URL
        this.dbUrl = "jdbc:sqlite:pm_for_one_day.db";

        // ✔ ΔΙΟΡΘΩΣΗ 2: φόρτωση SQLite driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setBudgetYear(int budgetYear) {
        this.budgetYear = budgetYear;
    }


    public void loadCategoriesFromDB() {
        String sql = """
                SELECT category_name, current_amount
                FROM categories
                JOIN budgets ON categories.id_budget = budgets.id_budget
                WHERE budgets.budget_year = ?
                """;

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budgetYear);
            ResultSet rs = pstmt.executeQuery();
            categories.clear();

            while (rs.next()) {
                String name = rs.getString("category_name");
                double amount = rs.getDouble("current_amount");
                categories.put(name, amount);
            }

            System.out.println("Οι κατηγορίες φορτώθηκαν από τη βάση δεδομένων.");

        } catch (SQLException e) {
            System.out.println("Σφάλμα SQLite: " + e.getMessage());
        }
    }

    public void displayBudget() {
        System.out.println("======= STATE BUDGET =======");

        if (categories.isEmpty()) {
            System.out.println("No categories found.");
            return;
        }

        double total = 0.0;
        for (List.Entry<String, Double> entry : categories.entrySet()) {
            System.out.printf("%-25s : %.2f EUR%n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }

        System.out.println("----------------------------");
        System.out.printf("TOTAL                     : %.2f €%n", total);
        System.out.println("============================");
    }

    public void modifyCategory(Scanner scanner) {
        if (categories.isEmpty()) {
            System.out.println("Δεν υπάρχουν κατηγορίες για επεξεργασία.");
            return;
        }

        System.out.println("Ποια κατηγορία θέλεις να τροποποιήσεις;");
        String categoryName = scanner.nextLine();

        if (!categories.containsKey(categoryName)) {
            System.out.println("Η κατηγορία " + categoryName + " δεν βρέθηκε.");
            return;
        }

        System.out.println("Δώσε νέο ποσό για την κατηγορία " + categoryName + ":");
        double newAmount;
        try {
            newAmount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Μη έγκυρη τιμή.");
            return;
        }

        categories.put(categoryName, newAmount);
        changes.put(categoryName, newAmount);

        System.out.println("Η κατηγορία " + categoryName + " ενημερώθηκε σε " + newAmount + "€");
    }

    public void showChanges() {
        System.out.println("CHANGES");
        if (changes.isEmpty()) {
            System.out.println("Δεν έχουν γίνει αλλαγές ακόμα.");
            return;
        }
        System.out.println("Αλλαγές που έγιναν:");
        for (List.Entry<String, Double> entry : changes.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + "€");
        }
    }

    public void saveToDB() {
        if (changes.isEmpty()) {
            System.out.println("Δεν υπάρχουν αλλαγές για αποθήκευση.");
            return;
        }

        String sql = "UPDATE categories SET current_amount = ?, last_modified = ? " +
                     "WHERE category_name = ? AND id_budget = " +
                     "(SELECT id_budget FROM budgets WHERE budget_year = ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            LocalDateTime now = LocalDateTime.now();

            for (List.Entry<String, Double> entry : changes.entrySet()) {
                pstmt.setDouble(1, entry.getValue());
                pstmt.setString(2, now.toString());
                pstmt.setString(3, entry.getKey());
                pstmt.setInt(4, budgetYear);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();
            changes.clear();

            System.out.println("Οι αλλαγές αποθηκεύτηκαν στη βάση δεδομένων.");

        } catch (SQLException e) {
            System.out.println("Σφάλμα κατά την αποθήκευση: " + e.getMessage());
        }
    }
}
