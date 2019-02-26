package com.example.weizhaoli.gwu_explorer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

class AlertsActivity : AppCompatActivity(){
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val alerts = generateFakeAlerts()

        recyclerView.adapter = AlertsAdapter(alerts)

        val intent: Intent = intent

    }

    private fun generateFakeAlerts(): List<Alert> {
        return listOf(
            Alert(
                linename = "Line Red",
                content = "Red Line: Expect residual delays to Glenmont due to ab earlier signal problem outside Forest Glen",
                iconUrl = "https://...."
            ),
            Alert(
                linename = "Line Blue",
                content = "Blue Line: Delays possible in both directions due to post-baseball game travel at Navy Yard station?",
                iconUrl = "https://...."
            )

            )

    }


}