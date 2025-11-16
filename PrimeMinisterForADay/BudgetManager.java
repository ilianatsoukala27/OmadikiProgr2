package PrimeMinisterForADay;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class BudgetManager {

    private static final String DB_URL = "jdbc:sqlite:C:/Path_To_DB_File";

    private BudgetData budgetData;

    static String[] categoryNames = {
            "Υγεία", "Παιδεία", "Άμυνα", "Κοινωνική Πρόνοια",
            "Φόροι", "Ευρωπαϊκά κονδύλια", "Λοιπά έσοδα"
        };
    static String[] categoryDescriptions = {
        "Δαπάνες για νοσοκομεία, φάρμακα, μισθούς υγειονομικών", "Εκπαίδευση, πανεπιστήμια, σχολεία, έρευνα", "Στρατιωτικός εξοπλισμός, μισθοί, εκπαίδευση προσωπικού", "Συντάξεις, επιδόματα, κοινωνική στήριξη",
        "Φόρος εισοδήματος, ΦΠΑ, εταιρικοί φόροι", "Χρηματοδότηση από Ε.Ε.", "Άλλα έσοδα του κράτους"
    };

    public BudgetManager() {
        budgetData = new BudgetData();
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
    }

    public void saveToDB() {
    }
    
    ////
    //Create new user
    ////
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

    ////
    //Create budget and categories (empty - edit later)
    ////
    public static boolean createBudgetAndCategories(int year) {
        String budgetSql = "INSERT INTO budgets (budget_year, total_income, total_expenses, created_at, updated_at) "
                   + "VALUES (?, ?, ?, ?, ?)";
        String categorySql = "INSERT INTO categories (id_budget, category_name, category_type, initial_amount, current_amount, description, last_modified) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);
            long budgetId;
            LocalDateTime now = LocalDateTime.now();

            //Add Budget
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

            //Add Categories
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
    }
}
