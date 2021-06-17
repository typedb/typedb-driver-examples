package com.vaticle.typedb.example.xcom;

public class InventoryItem {
    public InventoryItem(String name, long quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public String name;
    public long quantity;
}
