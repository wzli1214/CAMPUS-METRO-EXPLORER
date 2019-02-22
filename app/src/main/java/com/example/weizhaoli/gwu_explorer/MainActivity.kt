package com.example.weizhaoli.gwu_explorer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    private lateinit var destination: EditText
    private lateinit var go: Button
    private lateinit var alert: Button

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
        destination = findViewById(R.id.destination)
        go = findViewById(R.id.go)
        alert = findViewById(R.id.alert)


        destination.addTextChangedListener(textWatcher)


        AlertDialog.Builder(this)
            .setTitle("Welcome!")
            .setMessage("Please enter a location to travel to from GWU and use the alerts button to view Metro outages. \n\n" +
                     "Note: current only works for locations which don't require a transfer."
                    ) .setPositiveButton("OK") { dialog, which ->
                // User pressed OK
            }
            .show()
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
