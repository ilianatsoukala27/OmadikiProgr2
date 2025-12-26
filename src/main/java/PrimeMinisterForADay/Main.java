package PrimeMinisterForADay;

import java.util.Scanner;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {
        BudgetManager manager = new BudgetManager();
        manager.initialize();
        final int year = java.time.Year.now().getValue();

        BudgetData data = manager.getBudgetData();

        // Εκκίνηση GUI με τα δεδομένα
        SwingUtilities.invokeLater(() -> new BudgetGUI(data));

        // --- Προαιρετικό: διατήρηση console menu ---
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.println("\n===== BUDGET MENU =====");
            System.out.println("1. Δες τον προϋπολογισμό");
            System.out.println("2. Φτίαξε κενό budget για τροποποίηση");
            System.out.println("3. Τροποποίησε κατηγορία");
            System.out.println("4. Δες αλλαγές");
            System.out.println("5. Αποθήκευση αλλαγών στη βάση");
            System.out.println("6. Ανάλυση εσόδων / εξόδων");
            System.out.println("7. Έξοδος");
            System.out.print("Επέλεξε μία επιλογή: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    data.displayBudget();
                    break;
                case "2":
                    BudgetManager.createBudgetAndCategories(year);
                    break;
                case "3":
                    data.modifyCategory(scanner);
                    break;
                case "4":
                    data.showChanges();
                    break;
                case "5":
                    data.saveToDB();
                    break;
                case "6":
                    manager.printIncomeExpenseAnalysis();
                    break;
                case "7":
                    exit = true;
                    System.out.println("Έξοδος από το πρόγραμμα.");
                    break;
                default:
                    System.out.println("Μη έγκυρη επιλογή. Δοκίμασε ξανά.");
            }
        }

        scanner.close();
    }
}
