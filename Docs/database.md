# 📦 Spending Management App - Database Documentation

This document describes the database schema, table structures, and constraints used in the Store Management App.

---

## 📂 Database Info
- **Database Name:** SpendingTracker.db
- **Type:** SQLite
- **SQLite Version:** 3.x
- **Created On:** YYYY-MM-DD
- **Last Updated:** YYYY-MM-DD

---

## 🗄 Tables

### 1. `Products`
Stores information about products available in the store.

```sql
CREATE TABLE Products (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,   -- Unique product ID (auto-generated)
    Barcode TEXT NOT NULL,                  -- Product barcode
    Variant INTEGER NOT NULL DEFAULT 1,     -- Variant number, defaults to 1 if not given
    Type TEXT NOT NULL,                     -- Product type (e.g., "Food", "Electronics")
    Brand TEXT NOT NULL,                    -- Brand name
    Title TEXT NOT NULL,                    -- Product name/title
    Unit TEXT NOT NULL,                     -- Unit of measurement (e.g., "kg", "pcs")
    Quantity REAL NOT NULL,                 -- Stock quantity (can be fractional)
    Percentage REAL NULL,                   -- Optional percentage (e.g., discount, alcohol %)
    Manufacturer TEXT NULL,                 -- Optional manufacturer name
    Country TEXT NULL,                       -- Optional country of origin
    UNIQUE(Barcode, Variant)                -- Prevents duplicate barcode+variant combos
);
```

### 2. `Stores`
Stores information about stores that are used to buy products.

```sql
CREATE TABLE Stores (
    SCODE TEXT PRIMARY KEY,
    Name TEXT NULL,
    Longitude REAL NOT NULL,
    Latitude REAL NOT NULL
);
```

### 3. `Transaactions`
Receipts of purchases made in Stores.

```
CREATE TABLE Transactions (
    SCODE CHAR(10) NOT NULL,
    Barcode CHAR(20) NOT NULL,
    Price DECIMAL(10, 2) NOT NULL,
    discounted_price DECIMAL(10, 2) DEFAULT NULL,
    Date DATE NOT NULL,
    Time TIME NOT NULL,
    Link TEXT DEFAULT NULL,
    PRIMARY KEY (SCODE, Barcode, Date, Time),
    FOREIGN KEY (SCODE) REFERENCES Stores(SCODE),
    FOREIGN KEY (Barcode) REFERENCES Products(Barcode)
);
```
