package com.example.weizhaoli.gwu_explorer.Alerts

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.weizhaoli.gwu_explorer.R

class AlertsAdapter constructor(private val alerts: List<Alert>) : RecyclerView.Adapter<AlertsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = alerts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentAlert = alerts[position]

        holder.linenameTextView.text = currentAlert.linename
        holder.contentTextView.text = currentAlert.content


    }


    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        val linenameTextView: TextView = view.findViewById(R.id.linename)
        val contentTextView: TextView = view.findViewById(R.id.line_content)
        val iconImageView: ImageView = view.findViewById(R.id.icon)

    }
}