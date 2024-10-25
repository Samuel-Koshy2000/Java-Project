package org.example.proto;

import DatabaseConnection.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.proto.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class ProductDAO {






    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT * FROM products";
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String category = resultSet.getString("category");
                double purchase_price = resultSet.getDouble("purchase_price");
                double sell_price = resultSet.getDouble("sell_price");
                int stock = resultSet.getInt("stock");

                // Get size and shoe_size from the resultSet, and handle null values
                Integer size = resultSet.getInt("size");
                if (resultSet.wasNull()) {
                    size = null;  // Handle case where size is not set (null)
                }

                Integer shoe_size = resultSet.getInt("shoe_size");
                if (resultSet.wasNull()) {
                    shoe_size = null;  // Handle case where shoe_size is not set (null)
                }

                // Create a new product using the updated constructor
                Product product = new Product(
                        name,
                        category,
                        purchase_price,
                        sell_price,
                        stock,
                        size,  // Pass the size (can be null)
                        shoe_size  // Pass the shoe_size (can be null)
                );

                // Set additional fields
                product.setId(resultSet.getInt("id"));
                product.setDiscount(resultSet.getDouble("discount"));
                product.setIncomes(resultSet.getDouble("incomes"));
                product.setCosts(resultSet.getDouble("costs"));

                // Add the product to the list
                products.add(product);
            }
        }
        return products;
    }


    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO products (name, category, purchase_price, sell_price, stock, discount, size, shoe_size) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPurchase_price());
            statement.setDouble(4, product.getSell_price());
            statement.setInt(5, product.getStock());
            statement.setDouble(6, product.getDiscount());
            statement.setDouble(7, product.getSize());
            statement.setInt(8, product.getShoe_size());

            statement.executeUpdate();
        }
    }

    public void updateProduct(Product product) throws SQLException {
        String query = "UPDATE products SET name=?, category=?, purchase_price=?, sell_price=?, stock=?, discount=?, size=?, shoe_size=? WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getCategory());
            statement.setDouble(3, product.getPurchase_price());
            statement.setDouble(4, product.getSell_price());
            statement.setInt(5, product.getStock());
            statement.setDouble(6, product.getDiscount());
            statement.setInt(7, product.getSize());
            statement.setInt(8, product.getShoe_size());
            statement.setInt(9, product.getId());
            statement.executeUpdate();
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
    }

    public void sellProduct(int productId, int quantity) throws SQLException {
        // Récupérer le prix de vente et le capital actuel
        String selectQuery = "SELECT sell_price, discount FROM products WHERE id = ?";
        double sellPrice;
        double totalIncome;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, productId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                // Récupérer le prix de vente
                sellPrice = resultSet.getDouble("sell_price");
                double discount = resultSet.getDouble("discount");

                // Calculer le total des revenus
                totalIncome = (discount == 0) ? sellPrice * quantity : discount * quantity;
            } else {
                throw new SQLException("Produit non trouvé avec l'ID: " + productId);
            }
        }

        // Mettre à jour le stock dans la table products
        String updateStockQuery = "UPDATE products SET stock = stock - ? WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStockStatement = connection.prepareStatement(updateStockQuery)) {
            updateStockStatement.setInt(1, quantity);
            updateStockStatement.setInt(2, productId);
            updateStockStatement.executeUpdate();
        }

        // Mettre à jour le capital et les revenus dans la table finances
        String updateFinancesQuery = "UPDATE finances SET capital = capital + ?, income = income + ? WHERE id = 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateFinancesStatement = connection.prepareStatement(updateFinancesQuery)) {
            updateFinancesStatement.setDouble(1, totalIncome); // Ajouter les revenus au capital
            updateFinancesStatement.setDouble(2, totalIncome); // Ajouter les revenus à income
            updateFinancesStatement.executeUpdate();
        }


    }

    public void purchaseProduct(int productId, int quantity) throws SQLException {
        // Récupérer le purchase_price du produit
        String selectQuery = "SELECT purchase_price FROM products WHERE id = ?";
        double purchasePrice;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, productId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                purchasePrice = resultSet.getDouble("purchase_price");
            } else {
                throw new SQLException("Produit non trouvé avec l'ID: " + productId);
            }
        }

        // Mettre à jour le stock dans la table products
        String updateStockQuery = "UPDATE products SET stock = stock + ? WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStockStatement = connection.prepareStatement(updateStockQuery)) {
            updateStockStatement.setInt(1, quantity);
            updateStockStatement.setInt(2, productId);
            updateStockStatement.executeUpdate();
        }

        // Calculer le coût total pour l'achat
        double totalCost = purchasePrice * quantity;

        // Mettre à jour le capital et les coûts dans la table finances
        String updateFinancesQuery = "UPDATE finances SET capital = capital - ?, costs = costs + ? WHERE id = 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateFinancesStatement = connection.prepareStatement(updateFinancesQuery)) {
            updateFinancesStatement.setDouble(1, totalCost);
            updateFinancesStatement.setDouble(2, totalCost);
            updateFinancesStatement.executeUpdate();
        }

        // Mettre à jour l'UI si nécessaire

    }
    public void applyDiscount(int productId) throws SQLException {
        // Récupérer le sell_price et la catégorie du produit
        String selectQuery = "SELECT sell_price, category FROM products WHERE id = ?";
        double sellPrice;
        String category;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, productId);
            ResultSet resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                sellPrice = resultSet.getDouble("sell_price");
                category = resultSet.getString("category");
            } else {
                throw new SQLException("Produit non trouvé avec l'ID: " + productId);
            }
        }

        // Calculer le discount en fonction de la catégorie
        double discount;
        switch (category) {
            case "clothes":
                discount = sellPrice * 0.90; // 10% de réduction
                break;
            case "accessories":
                discount = sellPrice * 0.80; // 20% de réduction
                break;
            case "shoes":
                discount = sellPrice * 0.85; // 15% de réduction
                break;
            default:
                discount = 0; // Pas de réduction pour d'autres catégories
                break;
        }

        // Mettre à jour le discount dans la base de données
        String updateQuery = "UPDATE products SET discount = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {
            updateStatement.setDouble(1, discount);
            updateStatement.setInt(2, productId);
            updateStatement.executeUpdate();
        }
    }


    public void removeDiscount(int productId) throws SQLException {
        String query = "UPDATE products SET discount = 0 WHERE id=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            statement.executeUpdate();
        }
    }
}

