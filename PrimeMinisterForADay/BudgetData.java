package PrimeMinisterForADay;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetData {

    private Map<String, Double> categories = new HashMap<>();
    private Map<String, Double> changes = new HashMap<>();

    private String dbUrl;
    private int budgetYear;

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public void setBudgetYear(int year) {
        this.budgetYear = year;
    }

    public Map<String, Double> getCategories() {
        return categories;
    }

    public void loadCategoriesFromDB() {

        String sql =
            "SELECT categories.category_name, categories.current_amount " +
            "FROM categories " +
            "JOIN budgets ON categories.id_budget = budgets.id_budget " +
            "WHERE budgets.budget_year = ?";

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

            System.out.println("✔ Οι κατηγορίες φορτώθηκαν από SQLite.");

        } catch (SQLException e) {
            System.out.println("❌ Σφάλμα SQLite: " + e.getMessage());
        }
    }

    public void displayBudget() {}
    public void modifyCategory() {}
    public void showChanges() {}
}
