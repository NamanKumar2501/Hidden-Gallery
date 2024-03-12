package com.example.calculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import android.widget.Toolbar

class SettingActivity : AppCompatActivity() {
    private var isFeatureEnabled = false // Default: feature disabled
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        val toolBarBackIcon = findViewById<androidx.appcompat.widget.Toolbar>(R.id.setting_toolbar)

        toolBarBackIcon.setNavigationOnClickListener {
            // Code to be executed when the navigation icon is clicked
            onBackPressed() // or any other action you want to perform
        }


        sharedPreferences = getSharedPreferences("SwitchState", Context.MODE_PRIVATE)
        val switchButton = findViewById<Switch>(R.id.security_switch)
        switchButton.isChecked = sharedPreferences.getBoolean("SWITCH_STATE", false)

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("SWITCH_STATE", isChecked)
            editor.apply()

            if (isChecked) {
                Toast.makeText(this, "Switch On", Toast.LENGTH_SHORT).show()
                // Switch button on
            } else {
                Toast.makeText(this, "Switch Off", Toast.LENGTH_SHORT).show()
                // Switch button off
            }
        }


    }

}