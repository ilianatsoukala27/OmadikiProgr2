package PrimeMinisterForADay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class BudgetManagerTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUpStreams() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void restoreStreams() {
        System.setOut(originalOut);
    }

    /**
     * Helper: φτιάχνει έναν BudgetManager και δίνει πρόσβαση στο BudgetData.
     * Δεν κάνουμε initialize() για να μην καλεί τη βάση.
     */
    private BudgetData prepareData(BudgetManager manager) {
        BudgetData data = manager.getBudgetData();
        data.getCategories().clear();   // καθαρίζουμε ό,τι έχει
        return data;
    }

    @Test
    void applyChanges_allGood_printsSuccessMessage() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = prepareData(manager);
        Map<String, Double> categories = data.getCategories();

        // 4 πρώτες = Δαπάνες
        categories.put("Υγεία", 100.0);
        categories.put("Παιδεία", 200.0);
        categories.put("Άμυνα", 300.0);
        categories.put("Κοινωνική Πρόνοια", 400.0);

        // Υπόλοιπες = Έσοδα
        categories.put("Φόροι", 1500.0);
        categories.put("Ευρωπαϊκά κονδύλια", 1000.0);
        categories.put("Λοιπά έσοδα", 500.0);

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("All constraints passed successfully."),
                "Περιμέναμε επιτυχές μήνυμα όταν τα ποσά είναι έγκυρα.");
    }

    @Test
    void applyChanges_negativeAmount_showsError() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = prepareData(manager);
        Map<String, Double> categories = data.getCategories();

        categories.put("Υγεία", -10.0); // αρνητικό ποσό

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Amount cannot be negative"),
                "Περιμέναμε μήνυμα λάθους για αρνητικό ποσό.");
    }

    @Test
    void applyChanges_tooManyDecimals_showsError() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = prepareData(manager);
        Map<String, Double> categories = data.getCategories();

        categories.put("Υγεία", 10.123); // 3 δεκαδικά

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Amounts must have at most 2 decimal places"),
                "Περιμέναμε μήνυμα λάθους για περισσότερα από 2 δεκαδικά.");
    }

    @Test
    void applyChanges_incomeLessThanExpenses_showsError() {
        BudgetManager manager = new BudgetManager();
        BudgetData data = prepareData(manager);
        Map<String, Double> categories = data.getCategories();

        // Έξοδα (πρώτες 4 κατηγορίες)
        categories.put("Υγεία", 1000.0);
        categories.put("Παιδεία", 500.0);

        // Έσοδα (υπόλοιπες)
        categories.put("Φόροι", 200.0);

        manager.applyChanges();

        String output = outContent.toString();
        assertTrue(output.contains("Total income cannot be less than total expenses."),
                "Περιμέναμε μήνυμα λάθους όταν τα έσοδα είναι λιγότερα από τις δαπάνες.");
    }
}
