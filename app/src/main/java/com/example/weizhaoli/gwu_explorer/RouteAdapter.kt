package com.example.weizhaoli.gwu_explorer

import android.support.v7.widget.RecyclerView
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RouteAdapter constructor(private val stations: List<Station>) : RecyclerView.Adapter<RouteAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rowpath, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = stations.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentStation = stations[position]
        holder.stationnameTextView.text = currentStation.stationname

    }


    class ViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        val stationnameTextView: TextView = view.findViewById(R.id.stationname)
    }

}


