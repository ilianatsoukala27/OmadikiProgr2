package PrimeMinisterForADay;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BudgetData {

    private Map<String, Double> categories;
    private Map<String, Double> changes;

    // - Jdbc credentials - τα αλλάζουμε όταν κάνουμε την σύνδεση
    private final String DB_URL = "jdbc:mysql://localhost:3306/budget_db";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "password";

    public BudgetData() {
        categories = new HashMap<>();
        changes = new HashMap<>();
    }

    // φορτωμα κατηγοριων απο την βαση
    public void loadCategoriesFromDB() {
        String sql = """
                SELECT category_name, current_amount
                FROM categories
                JOIN budgets ON categories.id_budget = budgets.id_budget
                WHERE budgets.bugdet_year = ?
                """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bugdetYear);
            
            ResultSet rs = stmt.executeQuery();

            categories.clear();

            while (rs.next()) {
                String name = rs.getString("category_name");
                double amount = rs.getDouble("current_amount");
                categories.put(name, amount);
            }

            System.out.println("Οι κατηγορίες φορτώθηκαν με επιτυχία.");

    } catch (SQLException e) {
            System.out.println("Σφάλμα στη βάση δεδομένων " + e.getMessage());
        }
    }

    public void displayBudget() {

    }

    public void modifyCategory() {

    }

    public void showCanges() {

    }

    
}


