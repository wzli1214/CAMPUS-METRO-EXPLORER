package com.example.weizhaoli.gwu_explorer

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.example.weizhaoli.gwu_explorer.Alerts.AlertsActivity
import com.example.weizhaoli.gwu_explorer.Route.RouteActivity
import org.jetbrains.anko.doAsync
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {


    private lateinit var destination: EditText
    private lateinit var go: Button
    private lateinit var alert: Button
    private lateinit var remember: CheckBox

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // We're calling .getText() here, but in Kotlin you can omit the "get" or "set"
            // on a getter / setter and "pretend" you're using an actual variable.
            //      username.getText() == username.text
            val inputtedAddress: String = destination.text.toString().trim()
            val enableButton: Boolean = inputtedAddress.isNotEmpty()

            // Like above, this is really doing login.setEnabled(enableButton) under the hood
            go.isEnabled = enableButton
            alert.isEnabled = enableButton
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("MainActivity", "onCreate called")

        AlertDialog.Builder(this)
            .setTitle("Welcome!")
            .setMessage(
                "Please enter a location to travel to from GWU and use the alerts button to view Metro outages. \n\n" +
                        "Note: current only works for locations which don't require a transfer."
            ).setPositiveButton("OK") { dialog, which ->
                // User pressed OK
            }
            .show()

        destination = findViewById(R.id.destination)
        go = findViewById(R.id.go)
        alert = findViewById(R.id.alert)
        remember = findViewById(R.id.remember)


        destination.addTextChangedListener(textWatcher)



        remember.setOnCheckedChangeListener { view, isChecked ->
            // This works like an OnClickListener, except it also gives you whether or not the checkbox is now chckeced
            Log.d("MainActivity", "Remember Clicked")
            val desSave = destination.text.toString()
            val editor = getSharedPreferences("gwu_destination", Context.MODE_PRIVATE).edit()
            editor.putString("destinationSave", desSave)
            editor.apply()
        }

//      Get the save destinationAddress
        val sp = getSharedPreferences("gwu_destination", 0)
        destination.setText(sp.getString("destinationSave", ""))

//        radio button
        go.setOnClickListener {
            Log.d("MainActivity", "GO Clicked")


//      Run the Geocoder in the background thread.
            doAsync {

                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())


                try{
                    val results: List<Address> = geocoder.getFromLocationName(
                        destination.text.toString(), 10
                    )

                    val addr: MutableList<String> = mutableListOf()

                    results.forEach { curr ->
                        addr.add(curr.getAddressLine(0))

                    }

//                if (results == null){
//                    Log.d("MainActivity", "Error during geocoding")
//
//                }



                    if (addr.isEmpty()) {
                        Log.d("MainActivity", "No address")

                        addr.add("No match address, please change the address")

                    } else {
                        Log.d("MainActivity", "1 or more addresses")

                    }


//                Display the radio button dialog in the UI thread
                    runOnUiThread {


                        // select_dialog_singlechoice is a pre-defined XML layout for a RadioButton row
                        val arrayAdapter =
                            ArrayAdapter<String>(this@MainActivity, android.R.layout.select_dialog_singlechoice)
                        arrayAdapter.addAll(addr)

                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Possible matches for destination").setAdapter(arrayAdapter) { dialog, which ->

                                if (addr[which] == "No match address, please change the address") {
                                    dialog.dismiss()
                                } else {


                                    println("chose" + " " + addr[which])
                                    println("list which" + "" + results.get(which))

                                    val latChose = results.get(which).latitude
                                    val lngChose = results.get(which).longitude



                                    Log.d("MainActivity", "Radio button Clicked")
                                    val intentRoute: Intent = Intent(this@MainActivity, RouteActivity::class.java)


//                        Pass the lat and lng to RouteActivity
                                    var bundlePass = Bundle()
                                    bundlePass.putString("addChose", addr[which])

                                    bundlePass.putDouble("latChose", latChose)
                                    bundlePass.putDouble("lngChose", lngChose)

                                    intentRoute.putExtras(bundlePass)

                                    startActivity(intentRoute)


                                    Toast.makeText(this@MainActivity, "You picked: ${addr[which]}", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            }
                            .setNegativeButton("Cancel") { dialog, which ->
                                dialog.dismiss()
                            }
                            .show()

                    }


                } catch(e: IOException){
                    runOnUiThread {
                        // Runs if we have an error
                        Toast.makeText(this@MainActivity, "Error of retrieving address", Toast.LENGTH_LONG).show()

                    }



                }




            }


        }

        alert.setOnClickListener {
            Log.d("MainActivity", "Alert Clicked")
            val intent: Intent = Intent(this, AlertsActivity::class.java)
            startActivity(intent)
        }

    }


    override fun onStart() {
        super.onStart()
        Log.d("MainActivity", "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity", "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }


}
