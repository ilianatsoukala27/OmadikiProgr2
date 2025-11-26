package PrimeMinisterForADay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class BudgetManagerTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void applyChanges_whenNoCategories_printsError() {
        BudgetManager manager = new BudgetManager();

        // σιγουρευόμαστε ότι δεν έχει κατηγορίες
        manager.getBudgetData().getCategories().clear();

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Error: Budget must have at least one category."));
    }

    @Test
    void applyChanges_whenNegativeAmount_printsErrorForThatCategory() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = manager.getBudgetData();

        data.getCategories().clear();
        data.getCategories().put("Υγεία", -10.0);

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Error: Amount cannot be negative for Υγεία"));
    }

    @Test
    void applyChanges_whenMoreThanTwoDecimals_printsError() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = manager.getBudgetData();

        data.getCategories().clear();
        // ποσό με 3 δεκαδικά -> δεν επιτρέπεται
        data.getCategories().put("Παιδεία", 10.123);

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Error: Amounts must have at most 2 decimal places for Παιδεία"));
    }

    @Test
    void applyChanges_whenIncomeLessThanExpenses_printsError() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = manager.getBudgetData();

        data.getCategories().clear();
        // ΠΡΩΤΕΣ 4 ΚΑΤΗΓΟΡΙΕΣ = ΔΑΠΑΝΕΣ (Άρα expenses)
        data.getCategories().put("Υγεία", 200.0);            // expense
        data.getCategories().put("Παιδεία", 100.0);          // expense
        // ΥΠΟΛΟΙΠΕΣ = ΕΣΟΔΑ
        data.getCategories().put("Φόροι", 50.0);             // income
        data.getCategories().put("Λοιπά έσοδα", 20.0);       // income

        // expenses = 300, income = 70 -> πρέπει να βγάλει σφάλμα
        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Error: Total income cannot be less than total expenses."));
    }

    @Test
    void applyChanges_whenAllValid_printsSuccessMessage() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = manager.getBudgetData();

        data.getCategories().clear();
        // Δαπάνες (πρώτες 4)
        data.getCategories().put("Υγεία", 100.0);               // expense
        data.getCategories().put("Παιδεία", 50.0);              // expense
        // Έσοδα (υπόλοιπες)
        data.getCategories().put("Φόροι", 200.0);               // income
        data.getCategories().put("Λοιπά έσοδα", 200.0);         // income

        // income = 400, expenses = 150
        // Για να περάσει και το check "totalExpenses <= totalBudget",
        // φρόντισε στο BudgetData.getTotalBudgetAmount() να επιστρέφει
        // τουλάχιστον 150 (π.χ. το άθροισμα των κατηγοριών).

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("All constraints passed successfully."));
    }

    @Test
    void isExpenseCategory_firstFourAreExpenses_restAreIncome() throws Exception {
        // Η isExpenseCategory είναι private, οπότε την ελέγχουμε "έμμεσα"
        BudgetManager manager = new BudgetManager();
        BudgetData data = manager.getBudgetData();

        data.getCategories().clear();
        data.getCategories().put("Υγεία", 10.0);                  // expense
        data.getCategories().put("Παιδεία", 10.0);                // expense
        data.getCategories().put("Άμυνα", 10.0);                  // expense
        data.getCategories().put("Κοινωνική Πρόνοια", 10.0);      // expense
        data.getCategories().put("Φόροι", 10.0);                  // income

        manager.applyChanges();

        String output = outContent.toString();
        // Σε αυτήν την περίπτωση income = 10, expenses = 40 => error total income < total expenses
        assertTrue(output.contains("Error: Total income cannot be less than total expenses."));
    }
}
