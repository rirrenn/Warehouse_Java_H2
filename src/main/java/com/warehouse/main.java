package com.warehouse;

public class main {
    public static void main(String[] args) {
        try {
            Database.init();
            System.out.println("✔ Система управления складом запущена");
            System.out.println("💡 H2 Console: http://localhost:8082");

            Menu.showMainMenu();

        } catch (Exception e) {
            System.err.println("❌ Ошибка: " + e.getMessage());
        } finally {
            Database.stop();
            System.out.println("🛑 Система завершила работу");
        }
    }
}