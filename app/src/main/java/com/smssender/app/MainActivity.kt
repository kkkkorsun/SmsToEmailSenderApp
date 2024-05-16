package com.smssender.app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smssender.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        startSmsEmailService()

        binding.addEmailBtn.setOnClickListener {
            val email = binding.emailInput.text.toString()
            if (email.isNotBlank()) {
                saveEmailToPreferences(email)
                binding.currentEmail.text = email
            } else {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_LONG).show()
            }
        }

        val savedEmail = getEmailFromPreferences()
        if (savedEmail != null) {
            binding.currentEmail.text = savedEmail
        }
    }

    private fun startSmsEmailService() {
        Intent(this, SmsEmailService::class.java).also { intent ->
            startService(intent)
        }
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.SEND_SMS
                ),
                101
            )
        } else {
            Log.d("MainActivity", "All SMS permissions already granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101 && grantResults.isNotEmpty()) {
            val allPermissionsGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (allPermissionsGranted) {
                startSmsEmailService()
                Toast.makeText(this, "Permissions granted, service started", Toast.LENGTH_LONG)
                    .show()
            } else {
                Toast.makeText(this, "Permissions not granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveEmailToPreferences(email: String) {
        val sharedPref =
            getSharedPreferences("com.smssender.app.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("saved_email", email)
            apply()
        }
    }

    private fun getEmailFromPreferences(): String? {
        val sharedPref =
            getSharedPreferences("com.smssender.app.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        return sharedPref.getString("saved_email", null)
    }
}
