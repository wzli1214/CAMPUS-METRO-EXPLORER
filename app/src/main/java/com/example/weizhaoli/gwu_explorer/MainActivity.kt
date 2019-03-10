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
import org.jetbrains.anko.AnkoAsyncContext
import org.jetbrains.anko.doAsync
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


                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())

                val results: List<Address> = geocoder.getFromLocationName(
                    destination.text.toString(), 5
                )

                println("results is:" + results)
                println("results size is : " + results.size)


//      Run the Geocoder in the background thread.
            doAsync {

                lateinit var addr: MutableList<String>

                if(results.size >=2){
                    Log.d("MainActivity", "Multiple Results")
                    val first = results[0]
                    val firstAdd = first.getAddressLine(0)
                    val second = results[1]
                    val secondAdd = second.getAddressLine(1)
                    addr = mutableListOf(firstAdd, secondAdd)

                }
//
               if(results.size ==1){
                    Log.d("MainActivity", "Got 1 Result")
                    val first = results[0]
                    val firstAdd = first.getAddressLine(0)
                    addr = mutableListOf(firstAdd)
                }
//
                if(results.size == 0){
                    Log.d("MainActivity","No result")
                    addr = mutableListOf("No match result")

                }


                runOnUiThread {
                    //                  val choices = listOf("A", "B", "C")

                    // select_dialog_singlechoice is a pre-defined XML layout for a RadioButton row
                    val arrayAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.select_dialog_singlechoice)
                    arrayAdapter.addAll(addr)
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Possible matches for destination") .setAdapter(arrayAdapter) { dialog, which ->
                            Toast.makeText(this@MainActivity, "You picked: ${addr[which]}", Toast.LENGTH_SHORT).show() }
                        .setNegativeButton("Cancel") { dialog, which -> dialog.dismiss()
                        }
                        .show()

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
