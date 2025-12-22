package PrimeMinisterForADay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import PrimeMinisterForADay.BudgetData;
import PrimeMinisterForADay.BudgetManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BudgetManagerTest {

    private BudgetManager manager;
    private BudgetData data;

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        manager = new BudgetManager();
        data = manager.getBudgetData();

        // καθαρίζουμε τυχόν παλιά δεδομένα
        data.getCategories().clear();

        // πιάσουμε την κονσόλα
        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    // ---------------------------------------------------------------------
    // 1. Όλοι οι περιορισμοί περνάνε κανονικά
    // ---------------------------------------------------------------------
    @Test
    void applyChanges_allConstraintsPass() {
        Map<String, Double> categories = data.getCategories();

        // 4 πρώτες είναι δαπάνες
        categories.put("Υγεία", 100.0);
        categories.put("Παιδεία", 150.0);
        categories.put("Άμυνα", 200.0);
        categories.put("Κοινωνική Πρόνοια", 50.0);

        // τα υπόλοιπα έσοδα
        categories.put("Φόροι", 800.0);
        categories.put("Ευρωπαϊκά κονδύλια", 300.0);
        categories.put("Λοιπά έσοδα", 200.0);

        outContent.reset();
        manager.applyChanges();

        String out = outContent.toString();
        assertTrue(out.contains("All constraints passed successfully."),
                "Περίμενα να περάσουν όλοι οι περιορισμοί");
    }

    // ---------------------------------------------------------------------
    // 2. Έσοδα μικρότερα από έξοδα -> error
    // ---------------------------------------------------------------------
    @Test
    void applyChanges_incomeMustBeGreaterThanExpenses() {
        Map<String, Double> categories = data.getCategories();

        // έξοδα
        categories.put("Υγεία", 300.0);
        categories.put("Παιδεία", 200.0);
        categories.put("Άμυνα", 150.0);
        categories.put("Κοινωνική Πρόνοια", 100.0);

        // έσοδα (λίγα επίτηδες)
        categories.put("Φόροι", 200.0);
        categories.put("Ευρωπαϊκά κονδύλια", 100.0);

        outContent.reset();
        manager.applyChanges();

        String out = outContent.toString();
        assertTrue(out.contains("Error: Total income cannot be less than total expenses."),
                "Περίμενα μήνυμα ότι τα έσοδα είναι μικρότερα από τα έξοδα");
    }

    // ---------------------------------------------------------------------
    // 3. Περισσότερα από 2 δεκαδικά -> error
    // ---------------------------------------------------------------------
    @Test
    void applyChanges_incorrectDecimalsDetected() {
        Map<String, Double> categories = data.getCategories();

        categories.put("Υγεία", 10.123);    // 3 δεκαδικά
        categories.put("Φόροι", 100.0);

        outContent.reset();
        manager.applyChanges();

        String out = outContent.toString();
        assertTrue(out.contains("Error: Amounts must have at most 2 decimal places for Υγεία"),
                "Περίμενα μήνυμα για λάθος δεκαδικά στην Υγεία");
    }

    // ---------------------------------------------------------------------
    // 4. Ιδιωτική isExpenseCategory με reflection
    // ---------------------------------------------------------------------
    @Test
    void isExpenseCategory_worksCorrectly() throws Exception {
        Method m = BudgetManager.class.getDeclaredMethod("isExpenseCategory", String.class);
        m.setAccessible(true);

        boolean ygeia = (boolean) m.invoke(manager, "Υγεία");
        boolean paideia = (boolean) m.invoke(manager, "Παιδεία");
        boolean foroi = (boolean) m.invoke(manager, "Φόροι");
        boolean loipa = (boolean) m.invoke(manager, "Λοιπά έσοδα");

        assertTrue(ygeia, "Η Υγεία πρέπει να είναι δαπάνη");
        assertTrue(paideia, "Η Παιδεία πρέπει να είναι δαπάνη");
        assertFalse(foroi, "Οι φόροι είναι έσοδο, όχι δαπάνη");
        assertFalse(loipa, "Τα λοιπά έσοδα είναι έσοδο, όχι δαπάνη");
    }

    // ---------------------------------------------------------------------
    // 5. printIncomeExpenseAnalysis όταν δεν υπάρχουν κατηγορίες
    // ---------------------------------------------------------------------
    @Test
    void printIncomeExpenseAnalysis_whenNoCategories_printsMessage() {
        data.getCategories().clear();

        outContent.reset();
        manager.printIncomeExpenseAnalysis();

        String out = outContent.toString();
        assertTrue(out.contains("Δεν υπάρχουν κατηγορίες για ανάλυση."),
                "Περίμενα μήνυμα ότι δεν υπάρχουν κατηγορίες για ανάλυση");
    }

    // ---------------------------------------------------------------------
    // 6. printIncomeExpenseAnalysis σωστή ανάλυση εσόδων / εξόδων
    // ---------------------------------------------------------------------
    @Test
    void printIncomeExpenseAnalysis_printsIncomeAndExpenses() {
        Map<String, Double> categories = data.getCategories();
        categories.clear();

        // ΕΞΟΔΑ
        categories.put("Υγεία", 100.0);
        categories.put("Παιδεία", 50.0);

        // ΕΣΟΔΑ
        categories.put("Φόροι", 400.0);
        categories.put("Λοιπά έσοδα", 100.0);

        outContent.reset();
        manager.printIncomeExpenseAnalysis();

        String out = outContent.toString();

        assertTrue(out.contains("ΕΣΟΔΑ:"), "Λείπει το section ΕΣΟΔΑ");
        assertTrue(out.contains("ΕΞΟΔΑ:"), "Λείπει το section ΕΞΟΔΑ");
        assertTrue(out.contains("Σύνολο ΕΣΟΔΩΝ"),
                "Λάθος ή λείπει το σύνολο εσόδων ==>");
        assertTrue(out.contains("Σύνολο ΕΞΟΔΩΝ"),
                "Λάθος ή λείπει το σύνολο εξόδων ==>");
    }

    // ---------------------------------------------------------------------
    // 7. displayCategoryTotals εκτυπώνει συγκεντρωτικά ανά κατηγορία
    // ---------------------------------------------------------------------
    @Test
    void displayCategoryTotals_printsTotalsPerCategory() {
        Map<String, Double> categories = data.getCategories();
        categories.clear();

        categories.put("Υγεία", 10.0);
        categories.put("Παιδεία", 20.0);
        categories.put("Άμυνα", 30.0);
        categories.put("Κοινωνική Πρόνοια", 40.0);
        categories.put("Φόροι", 50.0);
        categories.put("Ευρωπαϊκά κονδύλια", 60.0);
        categories.put("Λοιπά έσοδα", 70.0);

        outContent.reset();
        manager.displayCategoryTotals();

        String out = outContent.toString();

        assertTrue(out.contains("ΣΥΓΚΕΝΤΡΩΤΙΚΑ ΑΝΑ ΚΑΤΗΓΟΡΙΑ"),
                "Πρέπει να εμφανίζεται ο τίτλος για τα συγκεντρωτικά");
        assertTrue(out.contains("Υγεία"), "Πρέπει να εμφανίζεται η κατηγορία Υγεία");
        assertTrue(out.contains("Παιδεία"), "Πρέπει να εμφανίζεται η κατηγορία Παιδεία");
        assertTrue(out.contains("Λοιπά έσοδα"), "Πρέπει να εμφανίζεται η κατηγορία Λοιπά έσοδα");
    }
}
