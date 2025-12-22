package PrimeMinisterForADay;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import PrimeMinisterForADay.BudgetData;
import PrimeMinisterForADay.BudgetGUI;

import javax.swing.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class BudgetGUITest {

    private BudgetData data;
    private BudgetGUI gui;

    @BeforeEach
    void setup() throws Exception {
        // Δημιουργούμε "καθαρά" δεδομένα για το GUI
        data = new BudgetData(2025);
        data.getCategories().clear();
        data.getCategories().put("Υγεία", 100.0);
        data.getCategories().put("Παιδεία", 200.0);

        // Φτιάχνουμε το GUI στο EDT
        SwingUtilities.invokeAndWait(() -> gui = new BudgetGUI(data));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (gui != null) {
            SwingUtilities.invokeAndWait(() -> gui.dispose());
        }
    }

    @Test
    void guiLoadsCategoriesIntoComboBox() throws Exception {
        // Μικρή καθυστέρηση για να προλάβει να φορτωθεί το GUI
        Thread.sleep(200);

        Field comboField = BudgetGUI.class.getDeclaredField("categoryCombo");
        comboField.setAccessible(true);
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = (JComboBox<String>) comboField.get(gui);

        // Δεν βασιζόμαστε σε συγκεκριμένη σειρά, μόνο στο περιεχόμενο
        assertEquals(2, combo.getItemCount());
        boolean hasYgeia = false;
        boolean hasPaideia = false;
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if ("Υγεία".equals(item)) hasYgeia = true;
            if ("Παιδεία".equals(item)) hasPaideia = true;
        }
        assertTrue(hasYgeia);
        assertTrue(hasPaideia);
    }

    @Test
    void addTransaction_updatesData() throws Exception {
        // Παίρνουμε τα components από το GUI με reflection
        Field amountFieldF = BudgetGUI.class.getDeclaredField("amountField");
        amountFieldF.setAccessible(true);
        JTextField amountField = (JTextField) amountFieldF.get(gui);

        Field comboField = BudgetGUI.class.getDeclaredField("categoryCombo");
        comboField.setAccessible(true);
        @SuppressWarnings("unchecked")
        JComboBox<String> combo = (JComboBox<String>) comboField.get(gui);

        Field listModelField = BudgetGUI.class.getDeclaredField("listModel");
        listModelField.setAccessible(true);
        @SuppressWarnings("unchecked")
        DefaultListModel<String> listModel =
                (DefaultListModel<String>) listModelField.get(gui);

        int beforeSize = listModel.getSize();

        // Αρχική τιμή Υγείας πριν την αλλαγή
        double beforeValue = data.getCategories().get("Υγεία");
        assertEquals(100.0, beforeValue, 0.001);

        // Προσομοίωση input χρήστη
        combo.setSelectedItem("Υγεία");
        amountField.setText("150.0");

        // Κλήση της private addTransaction()
        Method addTransaction = BudgetGUI.class.getDeclaredMethod("addTransaction");
        addTransaction.setAccessible(true);
        addTransaction.invoke(gui);

        int afterSize = listModel.getSize();
        double afterValue = data.getCategories().get("Υγεία");

        // 1) Το μέγεθος της λίστας παραμένει ίδιο (κατηγορίες ίδιες)
        assertEquals(beforeSize, afterSize);

        // 2) Το ποσό της "Υγεία" ενημερώθηκε
        assertEquals(150.0, afterValue, 0.001);

        // 3) Υπάρχει κάποια γραμμή στη λίστα που να αφορά την "Υγεία"
        boolean foundLineForYgeia = false;
        for (int i = 0; i < listModel.getSize(); i++) {
            String line = listModel.getElementAt(i);
            if (line.contains("Υγεία")) {
                foundLineForYgeia = true;
                break;
            }
        }
        assertTrue(foundLineForYgeia);
    }
}
