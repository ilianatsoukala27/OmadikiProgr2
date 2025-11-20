package PrimeMinisterForADay;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetData {

    private Map<String, Double> categories;
    private Map<String, Double> changes;
    private int budgetYear;

    public BudgetData(int budgetYear) {
        this.budgetYear = budgetYear;
        this.categories = new HashMap<>();
        this.changes = new HashMap<>();
    }


    public void loadCategoriesFromDB() {
        String sql = """
                SELECT category_name, current_amount
                FROM categories
                JOIN budgets ON categories.id_budget = budgets.id_budget
                WHERE budgets.budget_year = ?
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:pm_for_one_day.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, budgetYear); // Use the variable
            
            ResultSet rs = pstmt.executeQuery();
            categories.clear();

            while (rs.next()) {
                String name = rs.getString("category_name");
                double amount = rs.getDouble("current_amount");
                categories.put(name, amount);
            }

            System.out.println("Οι κατηγορίες φορτώθηκαν από SQLite.");

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
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String categoryName = entry.getKey();
            Double amount = entry.getValue();

            System.out.printf("%-25s : %.2f EUR%n", categoryName, amount);
            total += amount;
        }

        System.out.println("----------------------------");
        System.out.printf("TOTAL                     : %.2f €%n", total);
        System.out.println("==================================");

    }

    public void modifyCategory() {

    }

    public void showCanges() {

    }

    
}
