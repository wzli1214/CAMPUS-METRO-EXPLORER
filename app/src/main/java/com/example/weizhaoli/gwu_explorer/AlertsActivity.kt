package com.example.weizhaoli.gwu_explorer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import okhttp3.OkHttpClient
import org.json.JSONObject
import java.io.IOException

class AlertsActivity : AppCompatActivity(){
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alerts)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

//        Test the fake Alerts
//        val alerts = generateFakeAlerts()
//        recyclerView.adapter = AlertsAdapter(alerts)

        val intent: Intent = intent

        retrieveAlerts(
            successCallback = { alerts ->
                runOnUiThread {
                    if(alerts.size > 0 ){
                        Log.d("AlertsActivity", "Succeed to got Alerts")


                        recyclerView.adapter = AlertsAdapter(alerts)


                    } else {
                        Log.d("AlertsActivity", "No alert")
                        Toast.makeText(this@AlertsActivity, "There is no alert right now", Toast.LENGTH_LONG).show()


                    }


                }

            },
            errorCallback = {
                runOnUiThread {
                    // Runs if we have an error
                        Toast.makeText(this@AlertsActivity, "Error of retrieving alerts", Toast.LENGTH_LONG).show()

                }
            }
        )


    }


//    OkHTTP
    fun retrieveAlerts(
        apiKey: String = "6d5cd368846c478ab5f536251692546b",
        successCallback: (List<Alert>) -> Unit,
        errorCallback: (Exception) -> Unit
    ){
        val request = Request.Builder()
            .url("https://api.wmata.com/Incidents.svc/json/Incidents")
            .header("api_key", apiKey)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("AlertsActivity", "Failed to execute request")
                errorCallback(e)

            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("AlertsActivity", "Succeeded to execute request")
                val alerts = mutableListOf<Alert>()


                val responseString = response?.body()?.string()
                if(response.isSuccessful && responseString != null){
                    val incidents = JSONObject(responseString).getJSONArray("Incidents")
                    for(i in 0 until incidents.length()) {
                        val curr = incidents.getJSONObject(i)
                        val text = curr.getString("Description")
                        var name = curr.getString( "LinesAffected")

                        if (name == "SV;") {
                            name = "Silver"
                        }

                        if (name == "BL;"){
                            name = "Blue"
                        }

                        if (name == "RD;"){
                            name = "Red"
                        }

                        if (name == "GR;"){
                            name = "Green"
                        }

                        if (name == "OR;"){
                            name = "Orange"
                        }

                        if (name == "YL;"){
                            name = "Yellow"
                        }

                        alerts.add(
                            Alert(
                                linename = "Line:" + " " + name,
                                content = text
                            )
                        )

                    }
                    successCallback(alerts)

                } else {
                    errorCallback(Exception("Incident call failed"))

                }


            }


        })

    }





//    private fun generateFakeAlerts(): List<Alert> {
//        return listOf(
//            Alert(
//                linename = "Line Red",
//                content = "Red Line: Expect residual delays to Glenmont due to ab earlier signal problem outside Forest Glen"
//            ),
//            Alert(
//                linename = "Line Blue",
//                content = "Blue Line: Delays possible in both directions due to post-baseball game travel at Navy Yard station?"
//            )
//
//            )
//
//    }


}