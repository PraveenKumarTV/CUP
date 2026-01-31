package com.example.cup2.ui.general
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cup2.R


class QuestionsAdapter(private val questions:List<String>):RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {
    class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val text:TextView=view.findViewById(R.id.questionText)
    }
    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ViewHolder{
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_question,parent,false)
        return ViewHolder(view)

    }
    override fun onBindViewHolder(holder:ViewHolder,position:Int){
        holder.text.text="Q${position+1}.${questions[position]}"
    }
    override fun getItemCount()=questions.size
}