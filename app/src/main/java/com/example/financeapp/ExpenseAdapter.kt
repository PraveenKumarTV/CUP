package com.example.financeapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(
    private var records: List<FinanceRecord>,
    private val onItemClick: (FinanceRecord) -> Unit,
    private val onItemLongClick: (FinanceRecord) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewTitle: TextView = view.findViewById(R.id.textViewTitle)
        val textViewCategory: TextView = view.findViewById(R.id.textViewCategory)
        val textViewAmount: TextView = view.findViewById(R.id.textViewAmount)
        val textViewDate: TextView = view.findViewById(R.id.textViewDate)
        val textViewType: TextView = view.findViewById(R.id.textViewType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.textViewTitle.text = record.title
        holder.textViewCategory.text = record.category
        holder.textViewAmount.text = "$ ${String.format("%.2f", record.amount)}"
        holder.textViewDate.text = record.date
        holder.textViewType.text = record.type.name

        if (record.type == TransactionType.INCOME) {
            holder.textViewAmount.setTextColor(Color.parseColor("#4CAF50")) // Green
            holder.textViewType.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            holder.textViewAmount.setTextColor(Color.parseColor("#F44336")) // Red
            holder.textViewType.setTextColor(Color.parseColor("#F44336"))
        }

        holder.itemView.setOnClickListener { onItemClick(record) }
        holder.itemView.setOnLongClickListener {
            onItemLongClick(record)
            true
        }
    }

    override fun getItemCount(): Int = records.size

    fun updateData(newRecords: List<FinanceRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}