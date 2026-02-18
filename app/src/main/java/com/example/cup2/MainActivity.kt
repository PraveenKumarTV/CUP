package com.example.cup2

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.widget.PopupMenu
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.cup2.notifications.NotificationHelper
import java.util.concurrent.TimeUnit
import com.example.cup2.worker.SheetCheckoutWorker
import com.example.cup2.ui.general.GeneralQuestionsActivity


/**
 * MainActivity - Home screen with banner, module cards and bottom navigation.
 */
class MainActivity : AppCompatActivity() {

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
                R.id.nav_faq -> {
                    startActivity(Intent(this, HrActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
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
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels*0.9).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
