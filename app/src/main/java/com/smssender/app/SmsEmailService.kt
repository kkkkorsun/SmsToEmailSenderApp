package com.smssender.app

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.provider.Telephony
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class SmsEmailService : Service() {
    private val TAG = "SmsEmailService"

    private val smsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "SMS received")
            if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                val emailBody = messages.joinToString(separator = "\n") { it.displayMessageBody }
                val sender = messages.firstOrNull()?.displayOriginatingAddress

                Log.d(TAG, "Preparing to send email with body: $emailBody and sender: $sender")

                val sendToEmail = getEmailFromPreferences()
                val replyEmail = ""

                if (sendToEmail != null && sender != null) {
                    sendEmail(emailBody, sender, sendToEmail, replyEmail)
                } else {
                    Log.e(TAG, "Failed to get sendToEmail or sender is null")
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service onStartCommand triggered")
        val filter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, filter)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(smsReceiver)
    }

    private fun sendEmail(body: String, sender: String, sendToEmail: String, replyEmail: String) {
        Log.d(TAG, "Attempting to send email")
        val client = OkHttpClient()

        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        val jsonBody = """
        {
            "sendto": "$sendToEmail",
            "name": "Sms from $deviceName",
            "replyTo": "$replyEmail",
            "ishtml": "false",
            "title": "New SMS from: $sender",
            "body": "$body"
        }
        """.trimIndent()

        val mediaType = "application/json".toMediaType()
        val requestBody = jsonBody.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("https://mail-sender-api1.p.rapidapi.com/")
            .post(requestBody)
            .addHeader("content-type", "application/json")
            .addHeader("X-RapidAPI-Key", "Enter your API KEY")
            .addHeader("X-RapidAPI-Host", "mail-sender-api1.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Log.e(TAG, "Failed to send email", e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                response.use { resp ->
                    val responseString = resp.body?.string() ?: "No response body"
                    if (resp.isSuccessful) {
                        Log.d(TAG, "Email sent successfully: $responseString")
                    } else {
                        Log.e(TAG, "Failed to send email: $responseString")
                    }
                }
            }
        })
    }

    private fun getEmailFromPreferences(): String? {
        val sharedPref =
            getSharedPreferences("com.smssender.app.PREFERENCE_FILE_KEY", Context.MODE_PRIVATE)
        return sharedPref.getString("saved_email", null)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
