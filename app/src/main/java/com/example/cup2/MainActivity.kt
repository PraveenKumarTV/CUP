package com.example.cup2

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cup2.notifications.NotificationHelper
import com.example.cup2.ui.general.GeneralQuestionsActivity
import com.example.cup2.worker.SheetCheckoutWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.concurrent.TimeUnit
import com.example.cup2.ui.HistoryActivity


/**
 * MainActivity - Home screen with banner, module cards and bottom navigation.
 */
class MainActivity : AppCompatActivity() {

    private val SMS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uses Theme.PlacementPrep from themes.xml
        setContentView(R.layout.activity_main)
        //creating notification channel
        NotificationHelper.createNotificationChannel(this)
        //schedule background sheet checker
        val workRequest=PeriodicWorkRequestBuilder<SheetCheckoutWorker>(
            15,TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("SheetCheckWork",
            ExistingPeriodicWorkPolicy.KEEP,workRequest)

        showWelcomeDialog()

        // Menu button setup
        val btnMenu = findViewById<ImageButton>(R.id.btnMenu)
        btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        // Module card click listeners
        findViewById<View>(R.id.cardAptitude).setOnClickListener {
            startActivity(Intent(this, MockInterviewActivity::class.java))
        }
        findViewById<View>(R.id.cardTechnical).setOnClickListener {
            startActivity(Intent(this, TechnicalActivity::class.java))
        }
        findViewById<View>(R.id.cardHr).setOnClickListener {
            startActivity(Intent(this, HrActivity::class.java))
        }

        // Bottom navigation setup
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.selectedItemId = R.id.nav_home
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Already on home
                    true
                }
                R.id.nav_mock_interview -> {
                    startActivity(Intent(this, MockInterviewActivity::class.java))
                    true
                }
                R.id.nav_practice -> {
                    startActivity(Intent(this, GeneralQuestionsActivity::class.java))
                    true
                }
                R.id.nav_interview_xp -> {
                    startActivity(Intent(this, TechnicalActivity::class.java))
                    true
                }
                R.id.nav_history->{
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
//                R.id.nav_faq -> {
//                    startActivity(Intent(this, HrActivity::class.java))
//                    true
//                }
                else -> false
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.action_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                R.id.action_feedback -> {
                    val url = "https://docs.google.com/forms/d/e/1FAIpQLSfD_u_h8-58_86_H2_28_88_88_88_88_88_88_88/viewform" // Replace with your actual GForm link
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    true
                }
                R.id.action_help -> {
                    startActivity(Intent(this, HelpActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showWelcomeDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_welcome)
        val quoteTextView = dialog.findViewById<TextView>(R.id.quoteTextView)
        val quotes = resources.getStringArray(R.array.welcome_quotes)
        quoteTextView.text = quotes.random()
        val okButton = dialog.findViewById<Button>(R.id.okButton)
        okButton.setOnClickListener {
            checkSmsPermissionAndSend()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels*0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun checkSmsPermissionAndSend() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.SEND_SMS), SMS_PERMISSION_CODE)
        } else {
            sendSmsAcknowledgment()
        }
    }

    private fun sendSmsAcknowledgment() {
        val phoneNumber = "8248942776" // Assumed number
        val message = "Acknowledgement: User clicked OK in CUP2 Welcome Dialog."
        try {
            val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(this, "Acknowledgment SMS sent!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendSmsAcknowledgment()
            } else {
                Toast.makeText(this, "SMS Permission denied. Cannot send acknowledgment.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
