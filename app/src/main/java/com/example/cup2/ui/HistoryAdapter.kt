package com.example.cup2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cup2.R
import com.example.cup2.model.SavedResponse

class HistoryAdapter(private val historyList: List<SavedResponse>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val questionText: TextView = view.findViewById(R.id.historyQuestionText)
        val answerText: TextView = view.findViewById(R.id.historyAnswerText)
        val aiResponseText: TextView = view.findViewById(R.id.historyAiResponseText)
        val timestampText: TextView = view.findViewById(R.id.historyTimestampText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_response, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        holder.questionText.text = "Q: ${item.question}"
        holder.answerText.text = "Your Answer: ${item.answer}"
        holder.aiResponseText.text = "AI Response: ${item.aiResponse}"
        holder.timestampText.text = item.timestamp
    }

    override fun getItemCount() = historyList.size
}
