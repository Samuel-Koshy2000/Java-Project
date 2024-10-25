package org.example.proto;


import DatabaseConnection.DatabaseConnection;
import javafx.scene.control.*;
import org.example.proto.ProductDAO;
import org.example.proto.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductController {
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> nameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Double> purchasePriceColumn;
    @FXML private TableColumn<Product, Integer> sellPriceColumn;
    @FXML private TableColumn<Product, Integer> stockColumn;
    @FXML private TableColumn<Product, Integer> discountColumn;
    @FXML private TableColumn<Product, Integer> sizeColumn;
    @FXML private TableColumn<Product, Integer> shoesizeColumn;

    @FXML private TextField nameField;
    @FXML private TextField categoryField;
    @FXML private TextField purchasePriceField;
    @FXML private TextField sellPriceField;
    @FXML private TextField stockField;
    @FXML private TextField sizeField;
    @FXML private TextField shoeSizeField;

    @FXML private Label capitalLabel;
    @FXML private Label incomeLabel;
    @FXML private Label costLabel;


    private ProductDAO productDAO = new ProductDAO();

    // Méthode pour mettre à jour l'interface utilisateur
    public void updateUI() {
        // Récupérer les nouvelles valeurs de capital, income et costs depuis la base de données
        String selectFinancesQuery = "SELECT capital, income, costs FROM finances WHERE id = 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement selectFinancesStatement = connection.prepareStatement(selectFinancesQuery);
             ResultSet resultSet = selectFinancesStatement.executeQuery()) {

            if (resultSet.next()) {
                double capital = resultSet.getDouble("capital");
                double income = resultSet.getDouble("income");
                double costs = resultSet.getDouble("costs");

                // Mettre à jour les labels de l'UI
                capitalLabel.setText("Capital: $" + String.format("%.2f", capital));
                incomeLabel.setText("Income: $" + String.format("%.2f", income));
                costLabel.setText("Cost: $" + String.format("%.2f", costs));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer les exceptions
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private ObservableList<Product> productList = FXCollections.observableArrayList();

    /*
    private void updateFinancialSummary() {
        double totalCapital = 1000.0;
        double totalIncome = 0.0;
        double totalCost = 0.0;

        // Loop through the product list and calculate financials
        for (Product product : productList) {
            totalCapital -= product.getPurchase_price() * product.getStock();
            totalIncome += product.getIncomes();
            totalCost += product.getCosts();
        }

        // Update the UI labels
        capitalLabel.setText("Capital: $" + String.format("%.2f", totalCapital));
        incomeLabel.setText("Income: $" + String.format("%.2f", totalIncome));
        costLabel.setText("Cost: $" + String.format("%.2f", totalCost));
    }
*/

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchase_price"));
        sellPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sell_price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        sizeColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
        shoesizeColumn.setCellValueFactory(new PropertyValueFactory<>("shoe_size"));


        updateUI();
        loadProducts();
    }

    private boolean validateInput() {
        if (nameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a product name.");
            return false;
        }
        if (categoryField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a product category.");
            return false;
        }
        if (purchasePriceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a product price.");
            return false;
        }
        if (sellPriceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter a product price.");
            return false;
        }
        if (stockField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Please enter the stock quantity.");
            return false;
        }

        try {
            Double.parseDouble(purchasePriceField.getText());  // Validate price as a double
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid price format. Please enter a valid number.");
            return false;
        }
        try {
            Double.parseDouble(sellPriceField.getText());  // Validate price as a double
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid price format. Please enter a valid number.");
            return false;
        }

        try {
            Integer.parseInt(stockField.getText());  // Validate stock as an integer
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Form Error!", "Invalid stock format. Please enter a valid number.");
            return false;
        }

        return true;  // Return true if all validations pass
    }



    private void loadProducts() {
        try {
            productList.setAll(productDAO.getAllProducts());
            productTable.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly in production code
        }
    }

    @FXML
    public void handleAddProduct() {
        // Validate input before attempting to add a product
        if (validateInput()) {
            String name = nameField.getText();
            String category = categoryField.getText();
            double purchase_price = Double.parseDouble(purchasePriceField.getText());
            double sell_price = Double.parseDouble(sellPriceField.getText());
            int stock = Integer.parseInt(stockField.getText());

            Integer size = null;  // Initialize size as null
            Integer shoe_size = null;  // Initialize shoe_size as null

            // Check if sizeField exists and is not empty
            if (!sizeField.getText().isEmpty()) {
                try {
                    size = Integer.parseInt(sizeField.getText());  // Parse size input
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Size must be a valid number.");
                    return;  // Exit the method if the size input is invalid
                }
            }

            // Check if shoeSizeField exists and is not empty
            if (!shoeSizeField.getText().isEmpty()) {
                try {
                    shoe_size = Integer.parseInt(shoeSizeField.getText());  // Parse shoe size input
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Shoe size must be a valid number.");
                    return;  // Exit the method if the shoe size input is invalid
                }
            }

            try {
                // Create a new product with optional size and shoe_size
                Product newProduct = new Product(name, category, purchase_price, sell_price, stock, size, shoe_size);

                // Add product to database
                productDAO.addProduct(newProduct);

                // Refresh product list
                loadProducts();

            } catch (IllegalArgumentException e) {
                // Handle exceptions due to size or shoe size validation
                showAlert(Alert.AlertType.ERROR, "Invalid Input", e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();  // Handle SQL exceptions properly
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add the product.");
            }
        }
    }

    @FXML
    public void handleSaveUpdatedProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            // Validate input before updating the product
            if (validateInput()) {
                selectedProduct.setName(nameField.getText());
                selectedProduct.setCategory(categoryField.getText());
                selectedProduct.setPurchase_price(Double.parseDouble(purchasePriceField.getText()));
                selectedProduct.setSell_price(Double.parseDouble(sellPriceField.getText()));
                selectedProduct.setStock(Integer.parseInt(stockField.getText()));
                selectedProduct.setSize(Integer.parseInt(sizeField.getText()));
                selectedProduct.setShoe_size(Integer.parseInt(shoeSizeField.getText()));

                try {
                    productDAO.updateProduct(selectedProduct);  // Update product in the database
                    loadProducts();  // Refresh product list
                    clearFields();   // Clear input fields after saving
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Update Error", "No product selected for update.");
        }
    }

    @FXML
    public void handleDeleteProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                productDAO.deleteProduct(selectedProduct.getId());
                loadProducts();
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exceptions properly in production code
            }
        }
    }

    @FXML
    public void handleSellProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int quantity = getQuantityFromUser("Sell Product", "Enter quantity to sell:");
            if (quantity > 0 && quantity <= selectedProduct.getStock()) {
                try {
                    productDAO.sellProduct(selectedProduct.getId(), quantity);  // Update stock and income
                    loadProducts();  // Refresh product list and update financials
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Quantity must be between 1 and the current stock.");
            }
            updateUI();
        }
    }

    @FXML
    public void handlePurchaseProduct() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            int quantity = getQuantityFromUser("Purchase Product", "Enter quantity to purchase:");
            if (quantity > 0) {
                try {
                    productDAO.purchaseProduct(selectedProduct.getId(), quantity);  // Update stock and cost
                    loadProducts();  // Refresh product list and update financials
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                updateUI();
            }
        }
    }

    private int getQuantityFromUser(String title, String message) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                return Integer.parseInt(result.get());  // Parse the user input to an integer
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            }
        }
        return 0;  // Default value if input is invalid
    }

    @FXML
    public void handleApplyDiscount() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                productDAO.applyDiscount(selectedProduct.getId());
                loadProducts(); // Refresh product list
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void handleRemoveDiscount() {
        Product selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                productDAO.removeDiscount(selectedProduct.getId());
                loadProducts();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        nameField.clear();
        categoryField.clear();
        purchasePriceField.clear();
        sellPriceField.clear();
        stockField.clear();
    }

}
