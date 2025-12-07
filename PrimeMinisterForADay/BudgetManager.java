package PrimeMinisterForADay;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Map;

public class BudgetManager {

    private static final String DB_URL = "jdbc:sqlite:pm_for_one_day.db";

    private BudgetData budgetData;

    static String[] categoryNames = {
        "Υγεία", "Παιδεία", "Άμυνα", "Κοινωνική Πρόνοια",
        "Φόροι", "Ευρωπαϊκά κονδύλια", "Λοιπά έσοδα"
    };
    static String[] categoryDescriptions = {
        "Δαπάνες για νοσοκομεία, φάρμακα, μισθούς υγειονομικών",
        "Εκπαίδευση, πανεπιστήμια, σχολεία, έρευνα",
        "Στρατιωτικός εξοπλισμός, μισθοί, εκπαίδευση προσωπικού",
        "Συντάξεις, επιδόματα, κοινωνική στήριξη",
        "Φόρος εισοδήματος, ΦΠΑ, εταιρικοί φόροι",
        "Χρηματοδότηση από Ε.Ε.",
        "Άλλα έσοδα του κράτους"
    };

    public BudgetManager() {
        budgetData = new BudgetData();
        budgetData.setDbUrl(DB_URL);
        budgetData.setBudgetYear(2025); 
    }

    public void initialize() {
        System.out.println("Initializing budget system...");
        budgetData.loadCategoriesFromDB();

        if (budgetData.getCategories().isEmpty()) {
            System.out.println("Warning: No categories loaded from the database.");
        } else {
            System.out.println("Budget categories loaded successfully.");
        }

        System.out.println("Initialization completed.\n");
    }

    public BudgetData getBudgetData() {
        return budgetData;
    }

    public void applyChanges() {
        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        Map<String, Double> categories = budgetData.getCategories();
        if (categories.isEmpty()) {
            System.out.println("Error: Budget must have at least one category.");
            return;
        }

        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String name = entry.getKey();
            double amount = entry.getValue();

            if (amount < 0) {
                System.out.println("Error: Amount cannot be negative for " + name);
                return;
            }

            if (Math.round(amount * 100) / 100.0 != amount) {
                System.out.println("Error: Amounts must have at most 2 decimal places for " + name);
                return;
            }

            if (isExpenseCategory(name)) {
                totalExpenses += amount;
            } else {
                totalIncome += amount;
            }
        }

        if (totalIncome < totalExpenses) {
            System.out.println("Error: Total income cannot be less than total expenses.");
            return;
        }

        if (totalExpenses > budgetData.getTotalBudgetAmount()) {
            System.out.println("Error: Total expenses cannot exceed total budget.");
            return;
        }

        System.out.println("All constraints passed successfully.");
    }

    // ======= Ανάλυση ΕΣΟΔΩΝ / ΕΞΟΔΩΝ σε πολλαπλά επίπεδα =======
    public void printIncomeExpenseAnalysis() {
        Map<String, Double> categories = budgetData.getCategories();

        if (categories.isEmpty()) {
            System.out.println("Δεν υπάρχουν κατηγορίες για ανάλυση.");
            return;
        }

        double totalIncome = 0.0;
        double totalExpenses = 0.0;

        System.out.println("=========== ΑΝΑΛΥΣΗ ΕΣΟΔΩΝ / ΕΞΟΔΩΝ ===========");

        // 1ο επίπεδο: ΕΣΟΔΑ
        System.out.println("\nΕΣΟΔΑ:");
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String name = entry.getKey();
            double amount = entry.getValue();

            // ό,τι ΔΕΝ είναι δαπάνη θεωρείται ΕΣΟΔΟ
            if (!isExpenseCategory(name)) {
                System.out.printf("  %-25s : %.2f €%n", name, amount);
                totalIncome += amount;
            }
        }
        System.out.printf("  >> Σύνολο ΕΣΟΔΩΝ: %.2f €%n", totalIncome);

        // 2ο επίπεδο: ΕΞΟΔΑ
        System.out.println("\nΕΞΟΔΑ:");
        for (Map.Entry<String, Double> entry : categories.entrySet()) {
            String name = entry.getKey();
            double amount = entry.getValue();

            if (isExpenseCategory(name)) {   // τα 4 πρώτα: Υγεία, Παιδεία, Άμυνα, Κοιν. Πρόνοια
                System.out.printf("  %-25s : %.2f €%n", name, amount);
                totalExpenses += amount;
            }
        }
        System.out.printf("  >> Σύνολο ΕΞΟΔΩΝ: %.2f €%n", totalExpenses);

        System.out.println("\nΣυνολικό αποτέλεσμα (Έσοδα - Έξοδα): "
                + String.format("%.2f €", (totalIncome - totalExpenses)));
        System.out.println("===============================================\n");

     }

    private boolean isExpenseCategory(String name) {
        for (int i = 0; i < 4; i++) {
            if (categoryNames[i].equals(name)) return true;
        }
        return false;
    }

    public void saveToDB() {
        budgetData.saveToDB();
    }


    public static boolean createUser(String userName, String userPassword) {
        String sql = "INSERT INTO users (user_name, user_password, created_at, last_login) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            stmt.setString(1, userName);
            stmt.setString(2, userPassword);
            stmt.setString(3, now.toString());
            stmt.setString(4, null);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createBudgetAndCategories(int year) {
        String budgetSql = "INSERT INTO budgets (budget_year, total_income, total_expenses, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?)";
        String categorySql = "INSERT INTO categories (id_budget, category_name, category_type, initial_amount, current_amount, description, last_modified) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            long budgetId;
            LocalDateTime now = LocalDateTime.now();

            try (PreparedStatement stmt = conn.prepareStatement(budgetSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, year);
                stmt.setDouble(2, 0.0);
                stmt.setDouble(3, 0.0);
                stmt.setString(4, now.toString());
                stmt.setString(5, null);
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        budgetId = rs.getLong(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(categorySql)) {
                for (int i = 0; i < categoryNames.length; i++) {
                    stmt.setLong(1, budgetId);
                    stmt.setString(2, categoryNames[i]);
                    stmt.setString(3, i < 4 ? "Δαπάνη": "Έσοδο");
                    stmt.setDouble(4, 0);
                    stmt.setDouble(5, 0);
                    stmt.setString(6, categoryDescriptions[i]);
                    stmt.setString(7, now.toString());
                    stmt.addBatch();
                }

                stmt.executeBatch();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        public void displayCategoryTotals(){
            Map<String, Double> cayegories = budgetData.getCategories();
           if (categories.isEmpty()) {
        System.out.println("Δεν υπάρχουν δεδομένα προϋπολογισμού.");
        return; 
           }
              System.out.println("ΣΥΓΚΕΝΤΡΩΤΙΚΑ ΑΝΑ ΚΑΤΗΓΟΡΙΑ");

             double ygeia = categories.getOrDefault("Υγεία", 0.0);
             double paideia = categories.getOrDefault("Παιδεία", 0.0);
             double amyna = categories.getOrDefault("Άμυνα", 0.0);
             double koinPronoia = categories.getOrDefault("Κοινωνική Πρόνοια", 0.0);
             double foroi = categories.getOrDefault("Φόροι", 0.0);
             double ee = categories.getOrDefault("Ευρωπαϊκά κονδύλια", 0.0);
             double loipa = categories.getOrDefault("Λοιπά έσοδα", 0.0);

            System.out.println("Υγεία               : " + ygeia + " €");
            System.out.println("Παιδεία             : " + paideia + " €");
            System.out.println("Άμυνα               : " + amyna + " €");
            System.out.println("Κοινωνική Πρόνοια   : " + koinPronoia + " €");
            System.out.println("Φόροι               : " + foroi + " €");
            System.out.println("Ευρωπαϊκά κονδύλια  : " + ee + " €");
            System.out.println("Λοιπά έσοδα         : " + loipa + " €");

            
        }

      

    }
}
