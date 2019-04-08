package com.example.weizhaoli.gwu_explorer.Route

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.weizhaoli.gwu_explorer.R
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class RouteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var share: Button
    private lateinit var direct: Button

    override fun onCreate(savedInstannceState: Bundle?) {
        super.onCreate(savedInstannceState)
        setContentView(R.layout.activity_route)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

////      Test the fake Stations
//        val stations = generateFakeStations()
//        recyclerView.adapter = RouteAdapter(stations)

        share = findViewById(R.id.sharebutton)
        direct = findViewById(R.id.drivingbutton)


//        retrieve the lag and lng from MainActivity
        val bundle = getIntent().extras

        val addPass = bundle.getString("addChose")
        val latPass = bundle.getDouble("latChose")
        val lngPass = bundle.getDouble("lngChose")


//        retrieve the station
        retrieveStation(
            apiKey = getString(R.string.wmata_key3),
            latChose = latPass,
            lngChose = lngPass,
            successCallback = { stations ->

                if (stations.size > 0) {
                    Log.d("RouteActivity", "Succeed to got stations")
                    println("the station name :" + stations[0].stationname)
                    println("station code is" + " " + stations[0].code)

                    val endStationCode = stations[0].code

                    retrievePath(
                        apiKey = getString(R.string.wmata_key3),
                        endCode = endStationCode,
                        successCallback = { retrieveStation ->
                            runOnUiThread {
                                if (retrieveStation.size > 0) {
                                    Log.d("RouteAcitivity", "Succeeded to got path")
                                    recyclerView.adapter =
                                        RouteAdapter(retrieveStation)

                                    share.isEnabled = true
                                    direct.isEnabled = true


                                    //implicit intent of Sharing content
                                    val shareEnd: String = retrieveStation.last().stationname.substring(5)
                                    val shareLine: String = retrieveStation.last().linename

                                    share.setOnClickListener {
                                        Log.d("RouteActivity", "Share clicked")
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "text/plain"
                                            putExtra(
                                                Intent.EXTRA_TEXT, "I am going " +
                                                        "from Foggy Bottom to " + shareEnd + " by taking the "
                                                        + shareLine + " line!"
                                            )
                                        }

                                        val compatibleApps = packageManager.queryIntentActivities(
                                            intent,
                                            PackageManager.MATCH_DEFAULT_ONLY
                                        )

                                        startActivity(sendIntent)

                                    }


                                    //Explicit intent: Google Map
                                    direct.setOnClickListener {
                                        Log.d("RouteActivity", "Driving directions clicked")
                                        // Create a Uri from an intent string. Use the result to create an Intent.
                                        val navigationUri = Uri.parse("google.navigation:q=$addPass")
                                        // Create an Intent from navigationUri. Set the action to ACTION_VIEW
                                        val mapIntent = Intent(Intent.ACTION_VIEW, navigationUri)
                                        // Make the Intent explicit by setting the Google Maps package
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        startActivity(mapIntent)


                                    }




                                } else {
                                    Log.d("RouteActivity", "No path")
                                    share.isEnabled = false
                                    direct.isEnabled = false

                                    Toast.makeText(
                                        this@RouteActivity,
                                        "There is no direct path, please change the address",
                                        Toast.LENGTH_LONG
                                    ).show()

                                }


                            }


                        },
                        errorCallback = {
                            runOnUiThread {
                                Toast.makeText(this@RouteActivity, "Error of retrieving path", Toast.LENGTH_LONG).show()

                            }

                        }


                    )


                } else {
                    runOnUiThread {

                        Log.d("RouteActivity", "No station")
                        Toast.makeText(
                            this@RouteActivity,
                            "There is no station, please change the address",
                            Toast.LENGTH_LONG
                        ).show()

                    }


                }

            },


            errorCallback = {
                runOnUiThread {
                    Toast.makeText(this@RouteActivity, "Error of retrieving entrance station", Toast.LENGTH_LONG).show()

                }
            }


        )


    }


    fun retrieveStation(
        apiKey: String,
        latChose: Double,
        lngChose: Double,
        successCallback: (List<Station>) -> Unit,
        errorCallback: (Exception) -> Unit
    ) {
        val lat = latChose
        val lng = lngChose
        val radius = 2500

//        Building request
        val request = Request.Builder()
            .url("https://api.wmata.com/Rail.svc/json/jStationEntrances?Lat=$lat&Lon=$lng&Radius=$radius")
            .header("api_key", apiKey)
            .build()

        val client = OkHttpClient()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                errorCallback(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val stations = mutableListOf<Station>()
                val responseString = response.body()?.string()

                if (response.isSuccessful && responseString != null) {
                    val entrances = JSONObject(responseString).getJSONArray("Entrances")
                    for (i in 0 until entrances.length()) {
                        val curr = entrances.getJSONObject(i)
                        val stationFind = curr.getString("StationCode1")
                        val stationId = curr.getString("Name")

                        stations.add(
                            Station(
                                stationname = stationId,
                                code = stationFind,
                                linename = "fake linename"

                            )


                        )
                    }
                    successCallback(stations)
                } else {
                    errorCallback(Exception("Search stations call failed"))

                }


            }

        })


    }


    fun retrievePath(
        apiKey: String,
        endCode: String,
        successCallback: (List<Station>) -> Unit,
        errorCallback: (Exception) -> Unit

    ) {
        val end = endCode
        val request = Request.Builder()
            .url("https://api.wmata.com/Rail.svc/json/jPath?FromStationCode=C04&ToStationCode=$end")
            .header("api_key", apiKey)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("RouteActivity", "Failed to execute request")
                errorCallback(e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("RouteActivity", "Succeeded to execute request")
                val retrieveStations = mutableListOf<Station>()

                val responseString = response?.body()?.string()
                if (response.isSuccessful && responseString != null) {
                    val path = JSONObject(responseString).getJSONArray("Path")
                    for (i in 0 until path.length()) {
                        val curr = path.getJSONObject(i)
                        val currStaName = curr.getString("StationName")
                        val currStaCode = curr.getString("StationCode")
                        var currLineName = curr.getString("LineCode")

//                        Convert the name to the full name
                        if (currLineName == "SV") {
                            currLineName = "Silver"
                        }

                        if (currLineName == "BL") {
                            currLineName = "Blue"
                        }

                        if (currLineName == "RD") {
                            currLineName = "Red"
                        }

                        if (currLineName == "GR") {
                            currLineName = "Green"
                        }

                        if (currLineName == "OR") {
                            currLineName = "Orange"
                        }

                        if (currLineName == "YL") {
                            currLineName = "Yellow"
                        }


                        retrieveStations.add(

                            if (currStaCode == "C04") {
                                Station(
                                    stationname = "Take the " + currLineName + " Line:" + "\n" + currStaName,
                                    code = currStaCode,
                                    linename = currLineName
                                )

                            } else if (currStaCode == end) {
                                Station(
                                    stationname = "End:" + "\n" + currStaName,
                                    code = currStaCode,
                                    linename = currLineName

                                )
                            } else {
                                Station(
                                    stationname = currStaName,
                                    code = currStaCode,
                                    linename = currLineName

                                )
                            }


                        )
                    }

                    successCallback(retrieveStations)
                } else {
                    errorCallback(Exception("Path call failed"))

                }


            }


        })
    }


//        private fun generateFakeStations(): List<Station> {
//        return listOf(
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "WellFargo station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Bottom station"
//            ),
//
//            Station(
//                stationname = "Botdwqdqwdtom station"
//            ),
//
//            Station(
//                stationname = "sadasd station"
//            ),
//
//            Station(
//                stationname = "end station"
//            )
//
//            )
//
//    }


}