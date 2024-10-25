package org.example.proto;

public class Product {
    private int id;
    private String name;
    private String category;

    private double purchase_price;



    private double sell_price;
    private int stock;
    private double discount;
    private double incomes;
    private double costs;
    private int size;
    private int shoe_size;

    public Product(String name, String category, double purchase_price, double sell_price, int stock, int size, int shoe_size) {
        this.name = name;
        this.category = category;
        this.purchase_price = purchase_price;
        this.sell_price = sell_price;
        this.stock = stock;
        this.discount = 0.00;
        this.incomes = 0.00;
        this.costs = 0.00;

        // Validate size if provided
        if (size != 0) {
            if (size < 34 || size > 54 || size % 2 != 0) {
                throw new IllegalArgumentException("Wrong size!");
            }
            this.size = size;
        } else {
            this.size = 0;  // Set as null if not provided
        }

        // Validate shoe_size if provided
        if (shoe_size != 0) {
            if (shoe_size < 36 || shoe_size > 50) {
                throw new IllegalArgumentException("Wrong shoe size!");
            }
            this.shoe_size = shoe_size;
        } else {
            this.shoe_size = 0;  // Set as null if not provided
        }
    }

    // Getters and setters for each field
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(double purchase_price) {
        this.purchase_price = purchase_price;
    }

    public double getSell_price() {
        return sell_price;
    }

    public void setSell_price(double sell_price) {
        this.sell_price = sell_price;
    }

    /*
    public double getPurchasePrice() {
        return purchase_price;
    }

    public void setPurchasePrice(double purchase_price) {
        this.purchase_price = purchase_price;
    }
    public double getSellPrice() {
        return sell_price;
    }

    public void setSellPrice(double price) {
        this.sell_price = price;
    }


     */

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getIncomes() {
        return incomes;
    }

    public void setIncomes(double incomes) {
        this.incomes = incomes;
    }

    public double getCosts() {
        return costs;
    }

    public void setCosts(double costs) {
        this.costs = costs;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {this.size = size;}

    public Integer getShoe_size() {
        return  shoe_size;
    }

    public void setShoe_size(int shoe_size) {this.shoe_size = shoe_size;}
}
