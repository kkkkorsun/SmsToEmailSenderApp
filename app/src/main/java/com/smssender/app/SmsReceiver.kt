package com.smssender.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("SmsReceiver", "SMS received")
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val emailBody = messages.joinToString(separator = "\n") { it.displayMessageBody }
            val sender = messages.firstOrNull()?.displayOriginatingAddress

            Log.d("SmsReceiver", "Preparing to send email with body: $emailBody and sender: $sender")
            val emailIntent = Intent(context, SmsEmailService::class.java)
            emailIntent.putExtra("email_body", emailBody)
            emailIntent.putExtra("sms_sender", sender)
            context.startService(emailIntent)
        }
    }
}
