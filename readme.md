# Reconciliation System (Java Spring Boot)

## 📌 Overview
This system is designed to handle **financial reconciliation workflows**, ensuring settlement matching, transaction updates, and enforcement of business rules. It provides REST APIs for transaction details, reconciliation summaries, and settlement processing.

---

## 🚀 Features
- Transaction ingestion and reconciliation
- Settlement matching with atomic updates
- Business rule enforcement:
- No settlement allowed after 7 days from transaction date
- REST APIs for transaction details and reconciliation summary
- Robust date parsing with microsecond handling
- SQL workflow integration for reconciliation
- High test coverage with TDD/BDD

---

## 🛠 Tech Stack
- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **NamedParameterJdbcTemplate**
- **H2 Database** (for testing)
- **Apache Commons CSV** (for ingestion)

---

## ⚙️ Architecture
- **Controller Layer**: REST endpoints for reconciliation and transaction details
- **Service Layer**: Business logic and rule enforcement
- **Repository Layer**: JDBC integration for persistence
- **Database**: H2DB for testing, extendable to production RDBMS

---

## 📡 API Endpoints

### 1. Transaction Details
`GET /transactions/{id}`  
Returns transaction details by ID.

**Response Example:**
```json
{
    "accountId": "ACC123",
    "createdAt": "2026-05-01T22:29:02.404701",
    "currency": "USD",
    "lastSettlementDate": null,
    "lifecycleId": "LC001",
    "merchantName": "Amazon.com",
    "settlementStatus": "PENDING",
    "settlements": [
        {
            "currency": "USD",
            "lifecycleId": "LC001",
            "processedAt": "2026-05-01T22:29:42.233502",
            "settlementAmount": 125.99,
            "settlementDate": "2025-08-15",
            "settlementId": "SET001",
            "settlementType": "DEBIT"
        }
    ],
    "status": "COMPLETED",
    "totalSettledAmount": 0,
    "transactionAmount": 126,
    "transactionDate": "2025-08-14",
    "transactionId": "TXN001"
}
```

### 2. Transactions
`GET /transactions/{id}`  
Returns transaction details by ID.

**Response Example:**
```json
{
    "redIssues": [
        {
            "accountId": "ACC123",
            "createdAt": "2026-05-01T22:29:02.404701",
            "currency": "USD",
            "lastSettlementDate": null,
            "lifecycleId": "LC001",
            "merchantName": "Amazon.com",
            "settlementHistories": null,
            "settlementStatus": "PENDING",
            "status": "COMPLETED",
            "totalSettledAmount": 0,
            "transactionAmount": 126,
            "transactionDate": "2025-08-14",
            "transactionId": "TXN001"
        }
    ],
    "yellowIssues": [
        {
            "accountId": "ACC123",
            "createdAt": "2026-05-01T22:29:02.404701",
            "currency": "USD",
            "lastSettlementDate": null,
            "lifecycleId": "LC001",
            "merchantName": "Amazon.com",
            "settlementHistories": null,
            "settlementStatus": "PENDING",
            "status": "COMPLETED",
            "totalSettledAmount": 0,
            "transactionAmount": 126,
            "transactionDate": "2025-08-14",
            "transactionId": "TXN001"
        }
    ]
}
```
### 3. Dashboard
`GET /dashboard`  
Returns summary detail.

**Response Example:**
```json
{
    "fullySettledTransactions": 0,
    "lastUpdated": "2026-05-01T22:31:14.0961152",
    "noSettlementAfter7Days": 14,
    "notApplicableTransactions": 2,
    "overSettledTransactions": 0,
    "partialSettledTransactions": 0,
    "pendingTransactions": 12,
    "refundedTransactions": 0,
    "totalSettlementAmount": 0.00,
    "totalTransactions": 14
}
```

**Setup Instructions**

Clone the repository:

Build and run the application:

./mvnw spring-boot:run

**Future Enhancements**

Support for multiple file formats (Excel, JSON)
Enhanced reporting and dashboards
Role-based access control (RBAC)

