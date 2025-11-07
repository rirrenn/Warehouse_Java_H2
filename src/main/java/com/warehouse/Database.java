package com.warehouse;

import com.warehouse.models.*;
import org.h2.tools.Server;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:h2:~/warehouse_db";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static Server webServer;

    // ========== DATABASE INITIALIZATION ==========
    public static void init() {
        try {
            webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8082").start();
            System.out.println("🚀 H2 Console доступна по адресу: http://localhost:8082");

            try (Connection conn = getConnection();
                 Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("RUNSCRIPT FROM 'src/main/resources/db.sql'");
                System.out.println("✅ Таблицы созданы успешно!");
            }
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при инициализации БД:");
            e.printStackTrace();
        }
    }

    // ========== PRODUCT OPERATIONS ==========
    public static void addProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, selling_price, purchase_price, quantity, expiry_date, manufacturer_id, supplier_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getSellingPrice());
            stmt.setDouble(3, product.getPurchasePrice());
            stmt.setInt(4, product.getQuantity());
            stmt.setString(5, product.getExpiryDate());
            stmt.setLong(6, product.getManufacturerId());
            stmt.setLong(7, product.getSupplierId());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public static void deleteProduct(long id) throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    public static List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setSellingPrice(rs.getDouble("selling_price"));
                product.setPurchasePrice(rs.getDouble("purchase_price"));
                product.setQuantity(rs.getInt("quantity"));
                product.setExpiryDate(rs.getString("expiry_date"));
                product.setManufacturerId(rs.getLong("manufacturer_id"));
                product.setSupplierId(rs.getLong("supplier_id"));
                products.add(product);
            }
        }
        return products;
    }
    // Продажа товара
    public static void sellProduct(long productId, long customerId, int quantity) throws SQLException {
        Connection conn = getConnection();
        try {
            conn.setAutoCommit(false);

            // Получаем данные о товаре
            Product product = getProductById(productId);
            if (product == null) throw new SQLException("Товар не найден");
            if (product.getQuantity() < quantity) throw new SQLException("Недостаточно товара на складе");

            // Рассчитываем суммы
            double totalIncome = product.getSellingPrice() * quantity;
            double totalExpense = product.getPurchasePrice() * quantity;
            double profit = totalIncome - totalExpense;

            // Обновляем количество
            String updateSql = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setInt(1, quantity);
                stmt.setLong(2, productId);
                stmt.executeUpdate();
            }

            // Записываем финансовые операции
            String incomeSql = "INSERT INTO finances (type, amount, date, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(incomeSql)) {
                stmt.setString(1, "income");
                stmt.setDouble(2, totalIncome);
                stmt.setDate(3, new Date(System.currentTimeMillis()));
                stmt.setString(4, "Продажа товара ID: " + productId);
                stmt.executeUpdate();
            }

            String expenseSql = "INSERT INTO finances (type, amount, date, description) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(expenseSql)) {
                stmt.setString(1, "expense");
                stmt.setDouble(2, totalExpense);
                stmt.setDate(3, new Date(System.currentTimeMillis()));
                stmt.setString(4, "Закупка товара ID: " + productId);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Вспомогательные методы
    private static Product getProductById(long id) throws SQLException {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getLong("id"));
                p.setName(rs.getString("name"));
                p.setSellingPrice(rs.getDouble("selling_price"));  // Изменено с price на selling_price
                p.setPurchasePrice(rs.getDouble("purchase_price"));
                p.setQuantity(rs.getInt("quantity"));
                return p;
            }
        }
        return null;
    }

    private static Customer getCustomerById(long id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setDiscount(rs.getDouble("discount"));
                return c;
            }
        }
        return null;
    }

    // ========== MANUFACTURER OPERATIONS ==========
    public static void addManufacturer(Manufacturer manufacturer) throws SQLException {
        String sql = "INSERT INTO manufacturers (name, country, contact_phone) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, manufacturer.getName());
            stmt.setString(2, manufacturer.getCountry());
            stmt.setString(3, manufacturer.getContactPhone());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    manufacturer.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public static List<Manufacturer> getAllManufacturers() throws SQLException {
        List<Manufacturer> manufacturers = new ArrayList<>();
        String sql = "SELECT * FROM manufacturers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Manufacturer m = new Manufacturer();
                m.setId(rs.getLong("id"));
                m.setName(rs.getString("name"));
                m.setCountry(rs.getString("country"));
                m.setContactPhone(rs.getString("contact_phone"));
                manufacturers.add(m);
            }
        }
        return manufacturers;
    }

    // ========== CUSTOMER OPERATIONS ==========
    public static void addCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, phone, discount) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPhone());
            stmt.setDouble(3, customer.getDiscount());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public static List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getLong("id"));
                c.setName(rs.getString("name"));
                c.setPhone(rs.getString("phone"));
                c.setDiscount(rs.getDouble("discount"));
                customers.add(c);
            }
        }
        return customers;
    }
    // Добавление поставщика
    public static void addSupplier(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO suppliers (name, address, email, manufacturer_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getAddress());
            stmt.setString(3, supplier.getEmail());

            if (supplier.getManufacturerId() != null && supplier.getManufacturerId() > 0) {
                stmt.setLong(4, supplier.getManufacturerId());
            } else {
                stmt.setNull(4, Types.BIGINT);
            }

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplier.setId(generatedKeys.getLong(1));
                }
            }
        }
    }


    // Получение всех поставщиков
    public static List<Supplier> getAllSuppliers() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT id, name, address, email, manufacturer_id FROM suppliers";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier s = new Supplier();
                s.setId(rs.getLong("id"));
                s.setName(rs.getString("name"));
                s.setAddress(rs.getString("address"));
                s.setEmail(rs.getString("email"));
                s.setManufacturerId(rs.getObject("manufacturer_id") != null ? rs.getLong("manufacturer_id") : null);
                suppliers.add(s);
            }
        }
        return suppliers;
    }

    // ========== FINANCIAL OPERATIONS ==========
    // ========== FINANCIAL OPERATIONS ==========
    public static double getTotalIncome() throws SQLException {
        String sql = "SELECT SUM(amount) FROM finances WHERE type = 'income'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public static double getTotalExpenses() throws SQLException {
        String sql = "SELECT SUM(amount) FROM finances WHERE type = 'expense'";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    // Новый метод для расчета прибыли (добавьте этот код)
    public static double getProfit() throws SQLException {
        String sql = "SELECT SUM(CASE WHEN type = 'income' THEN amount ELSE -amount END) FROM finances";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    // ========== UTILITY METHODS ==========
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✅ Проверка подключения: Успешно!");
        } catch (SQLException e) {
            System.err.println("❌ Ошибка при проверке подключения:");
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (webServer != null) {
            webServer.stop();
            System.out.println("🛑 Сервер H2 остановлен");
        }
    }
}
