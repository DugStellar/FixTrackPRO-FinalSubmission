package com.capeitassist.fixtrackpro

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReportAdapter(private val reportList: List<ReportModel>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reportList[position]
        holder.tvTitle.text = report.title
        holder.tvCategory.text = "Category: ${report.category}"
        holder.tvDate.text = report.createdAt
        holder.tvStatus.text = report.status

        // Dynamic status badge styling matching custom workflows
        when (report.status) {
            "Being Fixed", "Working On It" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#38BDF8")) // Sky Blue
            }
            "Progressed" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#A78BFA")) // Purple
            }
            "Repaired", "Fixed", "Resolved" -> {
                holder.tvStatus.setTextColor(Color.parseColor("#34D399")) // Emerald Green
            }
            else -> { // Pending
                holder.tvStatus.setTextColor(Color.parseColor("#FBBF24")) // Amber Yellow
            }
        }
    }

    override fun getItemCount(): Int = reportList.size
}