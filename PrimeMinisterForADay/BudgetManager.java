package PrimeMinisterForADay;

public class BudgetManager {

    private BudgetData budgetData;

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
}
