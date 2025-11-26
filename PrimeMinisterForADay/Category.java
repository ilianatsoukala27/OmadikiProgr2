package PrimeMinisterForADay;

import java.time.LocalDateTime;

public class Category {

    private long budgetId;
    private String name;
    private String type; // "Έσοδο" ή "Δαπάνη"
    private double initialAmount;
    private double currentAmount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    public Category(long budgetId, String name, String type,
                    double initialAmount, double currentAmount,
                    String description,
                    LocalDateTime createdAt,
                    LocalDateTime lastModified) {

        this.budgetId = budgetId;
        this.name = name;
        this.type = type;
        this.initialAmount = initialAmount;
        this.currentAmount = currentAmount;
        this.description = description;
        this.createdAt = createdAt;
        this.lastModified = lastModified;
    }

    public long getBudgetId() { return budgetId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public double getInitialAmount() { return initialAmount; }
    public double getCurrentAmount() { return currentAmount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastModified() { return lastModified; }
}
