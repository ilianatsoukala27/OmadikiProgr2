public void applyChanges() {



        double totalIncome = 0;


        double totalExpenses = 0;





        List<Category> categories = budgetData.getCategories();


        if (categories.isEmpty()) {


            System.out.println("Error: Budget must have at least one category.");


            return;


        }





        LocalDateTime now = LocalDateTime.now();





        for (Category c : categories) {


            if (c.getInitialAmount() > c.getCurrentAmount()) {


                System.out.println("Error: Initial amount cannot be greater than current amount for " + c.getName());


                return;


            }





            if (Math.round(c.getInitialAmount() * 100) / 100.0 != c.getInitialAmount() ||


                Math.round(c.getCurrentAmount() * 100) / 100.0 != c.getCurrentAmount()) {


                System.out.println("Error: Amounts must have at most 2 decimal places for " + c.getName());


                return;


            }





            if (c.getCurrentAmount() < 0) {


                System.out.println("Error: Amount cannot be negative for " + c.getName());


                return;


            }





            if (c.getType().equals("Έσοδο")) {


                totalIncome += c.getCurrentAmount();


            } else {


                totalExpenses += c.getCurrentAmount();


            }





            if (c.getLastModified().isBefore(c.getCreatedAt())) {


                System.out.println("Error: updated_at must be >= created_at for " + c.getName());


                return;


            }





            if (c.getCreatedAt().isAfter(now) || c.getLastModified().isAfter(now)) {


                System.out.println("Error: Timestamps cannot be in the future for " + c.getName());


                return;


            }





            if (!budgetData.isValidBudgetId(c.getBudgetId())) {


                System.out.println("Error: Category " + c.getName() + " refers to a non-existent budget.");


                return;


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