package PrimeMinisterForADay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class BudgetDataTest {

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
    void displayBudget_whenNoCategories_printsNoCategoriesFound() {
        BudgetData data = new BudgetData(2025);

        data.displayBudget();

        String output = outContent.toString();
        assertTrue(output.contains("No categories found."));
    }

    @Test
    void modifyCategory_whenNoCategories_printsMessage() {
        BudgetData data = new BudgetData(2025);
        Scanner scanner = new Scanner("Υγεία\n");

        data.modifyCategory(scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Δεν υπάρχουν κατηγορίες για επεξεργασία."));
    }
}
