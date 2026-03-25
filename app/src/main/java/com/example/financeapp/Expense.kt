package com.example.financeapp

enum class TransactionType {
    INCOME, EXPENSE
}

data class FinanceRecord(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String, // format: yyyy-MM-dd for easier sorting/filtering
    val type: TransactionType
)