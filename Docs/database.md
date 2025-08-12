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
