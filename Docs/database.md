# 📦 Store Management App - Database Documentation

This document describes the database schema, table structures, and constraints used in the Store Management App.

---

## 📂 Database Info
- **Database Name:** store_db
- **Type:** SQLite
- **SQLite Version:** 3.x
- **Created On:** YYYY-MM-DD
- **Last Updated:** YYYY-MM-DD

---

## 🗄 Tables

### 1. `products`
Stores information about products available in the store.

```sql
CREATE TABLE products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    barcode TEXT NOT NULL,
    name TEXT NOT NULL,
    variant TEXT,
    price REAL,
    UNIQUE(barcode, variant)
);
