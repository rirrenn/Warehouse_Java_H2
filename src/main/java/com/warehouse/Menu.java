package com.warehouse;

import com.warehouse.models.Product;
import com.warehouse.models.Manufacturer;
import com.warehouse.models.Supplier;
import com.warehouse.models.Customer;
import java.util.Scanner;
import java.sql.SQLException;
import java.util.List;

public class Menu {
    private static final Scanner scanner = new Scanner(System.in);

    public static void showMainMenu() {
        while (true) {
            System.out.println("\n=== 🏭 УПРАВЛЕНИЕ СКЛАДОМ ===");
            System.out.println("1. 📦 Управление товарами");
            System.out.println("2. 🏭 Управление производителями");
            System.out.println("3. 🚚 Управление поставщиками");
            System.out.println("4. 👥 Управление клиентами");
            System.out.println("5. 💰 Финансовая отчетность");
            System.out.println("0. 🚪 Выход");

            int choice = readIntInput("Выберите пункт: ");
            switch (choice) {
                case 1 -> manageProducts();
                case 2 -> manageManufacturers();
                case 3 -> manageSuppliers();
                case 4 -> manageCustomers();
                case 5 -> showFinancialReport();
                case 0 -> { return; }
                default -> System.out.println("❌ Неверный ввод!");
            }
        }
    }

    private static void manageProducts() {
        while (true) {
            System.out.println("\n=== 📦 УПРАВЛЕНИЕ ТОВАРАМИ ===");
            System.out.println("1. 📥 Добавить товар");
            System.out.println("2. 📤 Удалить товар");
            System.out.println("3. 📋 Список всех товаров");
            System.out.println("4. 💵 Продать товар");
            System.out.println("0. ↩ Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> addProduct();
                case 2 -> deleteProduct();
                case 3 -> listProducts();
                case 4 -> sellProduct();
                case 0 -> { return; }
                default -> System.out.println("❌ Неверный ввод!");
            }
        }
    }

    private static void addProduct() {
        try {
            Product product = new Product();
            System.out.print("Введите название товара: ");
            product.setName(scanner.nextLine());

            product.setSellingPrice(readDoubleInput("Введите цену продажи: "));
            product.setPurchasePrice(readDoubleInput("Введите цену закупки: "));
            product.setQuantity(readIntInput("Введите количество: "));

            System.out.print("Введите срок годности (YYYY-MM-DD): ");
            product.setExpiryDate(scanner.nextLine());

            System.out.println("\nДоступные производители:");
            Database.getAllManufacturers().forEach(m ->
                    System.out.printf("%d. %s (%s)%n", m.getId(), m.getName(), m.getCountry()));
            product.setManufacturerId((long)readIntInput("Введите ID производителя: "));

            System.out.println("\nДоступные поставщики:");
            Database.getAllSuppliers().forEach(s ->
                    System.out.printf("%d. %s (%s)%n", s.getId(), s.getName(), s.getEmail()));
            product.setSupplierId((long)readIntInput("Введите ID поставщика: "));

            Database.addProduct(product);
            System.out.println("✅ Товар успешно добавлен!");
        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
        }
    }

    private static void sellProduct() {
        try {
            System.out.println("\nДоступные товары:");
            List<Product> products = Database.getAllProducts();
            products.forEach(p ->
                    System.out.printf("%d. %s (Цена: %.2f, Остаток: %d)%n",
                            p.getId(), p.getName(), p.getSellingPrice(), p.getQuantity()));

            long productId = readIntInput("Введите ID товара: ");
            int quantity = readIntInput("Введите количество для продажи: ");

            System.out.println("\nДоступные клиенты:");
            Database.getAllCustomers().forEach(c ->
                    System.out.printf("%d. %s (Скидка: %.2f%%)%n",
                            c.getId(), c.getName(), c.getDiscount()));
            long customerId = readIntInput("Введите ID клиента: ");

            Database.sellProduct(productId, customerId, quantity);
            System.out.println("✅ Продажа успешно оформлена!");

        } catch (Exception e) {
            System.out.println("❌ Ошибка при продаже: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        try {
            long id = readIntInput("Введите ID товара для удаления: ");
            Database.deleteProduct(id);
            System.out.println("✅ Товар успешно удален!");
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при удалении товара: " + e.getMessage());
        }
    }

    private static void listProducts() {
        try {
            System.out.println("\n📋 СПИСОК ТОВАРОВ:");
            Database.getAllProducts().forEach(p ->
                    System.out.printf("%d. %s - %.2f сом. (Остаток: %d)%n",
                            p.getId(), p.getName(), p.getSellingPrice(), p.getQuantity()));
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при загрузке товаров: " + e.getMessage());
        }
    }

    private static void manageManufacturers() {
        while (true) {
            System.out.println("\n=== 🏭 УПРАВЛЕНИЕ ПРОИЗВОДИТЕЛЯМИ ===");
            System.out.println("1. 📥 Добавить производителя");
            System.out.println("2. 📋 Список производителей");
            System.out.println("0. ↩ Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> addManufacturer();
                case 2 -> listManufacturers();
                case 0 -> { return; }
                default -> System.out.println("❌ Неверный ввод!");
            }
        }
    }

    private static void addManufacturer() {
        try {
            Manufacturer m = new Manufacturer();
            System.out.print("Введите название производителя: ");
            m.setName(scanner.nextLine());

            System.out.print("Введите страну: ");
            m.setCountry(scanner.nextLine());

            System.out.print("Введите контактный телефон: ");
            m.setContactPhone(scanner.nextLine());

            Database.addManufacturer(m);
            System.out.println("✅ Производитель успешно добавлен!");
        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
        }
    }

    private static void listManufacturers() {
        try {
            System.out.println("\n📋 СПИСОК ПРОИЗВОДИТЕЛЕЙ:");
            Database.getAllManufacturers().forEach(m ->
                    System.out.printf("%d. %s (%s, тел.: %s)%n",
                            m.getId(), m.getName(), m.getCountry(), m.getContactPhone()));
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при загрузке производителей: " + e.getMessage());
        }
    }

    private static void manageSuppliers() {
        while (true) {
            System.out.println("\n=== 🚚 УПРАВЛЕНИЕ ПОСТАВЩИКАМИ ===");
            System.out.println("1. 📥 Добавить поставщика");
            System.out.println("2. 📋 Список поставщиков");
            System.out.println("0. ↩ Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> addSupplier();
                case 2 -> listSuppliers();
                case 0 -> { return; }
                default -> System.out.println("❌ Неверный ввод!");
            }
        }
    }

    private static void addSupplier() {
        try {
            Supplier s = new Supplier();
            System.out.print("Введите название поставщика: ");
            s.setName(scanner.nextLine());

            System.out.print("Введите адрес: ");
            s.setAddress(scanner.nextLine());

            System.out.print("Введите email: ");
            s.setEmail(scanner.nextLine());

            // Добавляем выбор производителя
            System.out.println("\nДоступные производители:");
            Database.getAllManufacturers().forEach(m ->
                    System.out.printf("%d. %s (%s)%n", m.getId(), m.getName(), m.getCountry()));
            s.setManufacturerId((long)readIntInput("Введите ID производителя (0 если нет): "));

            Database.addSupplier(s);
            System.out.println("✅ Поставщик успешно добавлен!");
        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
        }
    }

    private static void listSuppliers() {
        try {
            System.out.println("\n📋 СПИСОК ПОСТАВЩИКОВ:");
            List<Supplier> suppliers = Database.getAllSuppliers();
            List<Manufacturer> manufacturers = Database.getAllManufacturers();

            suppliers.forEach(s -> {
                String manufacturerName = manufacturers.stream()
                        .filter(m -> m.getId().equals(s.getManufacturerId()))
                        .findFirst()
                        .map(m -> m.getName())
                        .orElse("нет производителя");

                System.out.printf("%d. %s (%s, email: %s, производитель: %s)%n",
                        s.getId(), s.getName(), s.getAddress(), s.getEmail(), manufacturerName);
            });
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при загрузке поставщиков: " + e.getMessage());
        }
    }

    private static void manageCustomers() {
        while (true) {
            System.out.println("\n=== 👥 УПРАВЛЕНИЕ КЛИЕНТАМИ ===");
            System.out.println("1. 📥 Добавить клиента");
            System.out.println("2. 📋 Список клиентов");
            System.out.println("0. ↩ Назад");

            int choice = readIntInput("Выберите действие: ");
            switch (choice) {
                case 1 -> addCustomer();
                case 2 -> listCustomers();
                case 0 -> { return; }
                default -> System.out.println("❌ Неверный ввод!");
            }
        }
    }

    private static void addCustomer() {
        try {
            Customer c = new Customer();
            System.out.print("Введите имя клиента: ");
            c.setName(scanner.nextLine());

            System.out.print("Введите телефон: ");
            c.setPhone(scanner.nextLine());

            c.setDiscount(readDoubleInput("Введите скидку (%): "));

            Database.addCustomer(c);
            System.out.println("✅ Клиент успешно добавлен!");
        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
        }
    }

    private static void listCustomers() {
        try {
            System.out.println("\n📋 СПИСОК КЛИЕНТОВ:");
            Database.getAllCustomers().forEach(c ->
                    System.out.printf("%d. %s (тел.: %s, скидка: %.2f%%)%n",
                            c.getId(), c.getName(), c.getPhone(), c.getDiscount()));
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при загрузке клиентов: " + e.getMessage());
        }
    }

    private static void showFinancialReport() {
        try {
            System.out.println("\n=== 💰 ФИНАНСОВАЯ ОТЧЕТНОСТЬ ===");
            System.out.printf("Общий доход: %.2f сом.%n", Database.getTotalIncome());
            System.out.printf("Общие расходы: %.2f сом.%n", Database.getTotalExpenses());
            System.out.printf("Прибыль: %.2f сом.%n",
                    Database.getTotalIncome() - Database.getTotalExpenses());
        } catch (SQLException e) {
            System.out.println("❌ Ошибка при получении финансовых данных: " + e.getMessage());
        }
    }

    private static int readIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Введите целое число!");
            }
        }
    }

    private static double readDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Введите число!");
            }
        }
    }
}