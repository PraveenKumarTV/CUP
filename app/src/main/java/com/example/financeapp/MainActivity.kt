package com.example.financeapp

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ExpenseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvNetBalance: TextView
    private lateinit var btnFromDate: Button
    private lateinit var btnToDate: Button
    private lateinit var tabLayout: TabLayout

    private var fromDate: Calendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    private var toDate: Calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    
    private var currentTab: Int = 0 // 0: All, 1: Income, 2: Expense
    private val CHANNEL_ID = "balance_alert_channel"
    private val NOTIFICATION_ID = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewExpenses)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvNetBalance = findViewById(R.id.tvNetBalance)
        btnFromDate = findViewById(R.id.btnFromDate)
        btnToDate = findViewById(R.id.btnToDate)
        tabLayout = findViewById(R.id.tabLayout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        setupTabs()
        updateDateButtons()

        // Link the adapter to the Delete icon click
        adapter = ExpenseAdapter(
            emptyList(),
            onItemClick = { record -> showExpenseDialog(record) },
            onItemLongClick = { record -> showDeleteConfirmation(record) }
        )
        recyclerView.adapter = adapter

        findViewById<FloatingActionButton>(R.id.fabAddExpense).setOnClickListener {
            showExpenseDialog(null)
        }

        btnFromDate.setOnClickListener { showDatePicker(true) }
        btnToDate.setOnClickListener { showDatePicker(false) }

        refreshData()
    }

    private fun setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("All"))
        tabLayout.addTab(tabLayout.newTab().setText("Income"))
        tabLayout.addTab(tabLayout.newTab().setText("Expenses"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                refreshData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun showDatePicker(isFromDate: Boolean) {
        val calendar = if (isFromDate) fromDate else toDate
        DatePickerDialog(this, { _, year, month, day ->
            if (isFromDate) {
                fromDate.set(year, month, day)
            } else {
                toDate.set(year, month, day)
            }
            updateDateButtons()
            refreshData()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateButtons() {
        btnFromDate.text = "From: ${dateFormat.format(fromDate.time)}"
        btnToDate.text = "To: ${dateFormat.format(toDate.time)}"
    }

    private fun showExpenseDialog(record: FinanceRecord?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_expense, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val textViewDialogTitle = dialogView.findViewById<TextView>(R.id.textViewDialogTitle)
        val radioGroupType = dialogView.findViewById<RadioGroup>(R.id.radioGroupType)
        val radioButtonIncome = dialogView.findViewById<RadioButton>(R.id.radioButtonIncome)
        val radioButtonExpense = dialogView.findViewById<RadioButton>(R.id.radioButtonExpense)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextAmount = dialogView.findViewById<EditText>(R.id.editTextAmount)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        val editTextDate = dialogView.findViewById<EditText>(R.id.editTextDate)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)

        val selectedDate = Calendar.getInstance()
        if (record != null) {
            textViewDialogTitle.text = "Edit Transaction"
            editTextTitle.setText(record.title)
            editTextAmount.setText(record.amount.toString())
            editTextCategory.setText(record.category)
            editTextDate.setText(record.date)
            if (record.type == TransactionType.INCOME) radioButtonIncome.isChecked = true
            else radioButtonExpense.isChecked = true

            val dateParts = record.date.split("-")
            if (dateParts.size == 3) {
                selectedDate.set(dateParts[0].toInt(), dateParts[1].toInt() - 1, dateParts[2].toInt())
            }
        } else {
            textViewDialogTitle.text = "Add Transaction"
            editTextDate.setText(dateFormat.format(selectedDate.time))
            if (currentTab == 1) radioButtonIncome.isChecked = true
            else if (currentTab == 2) radioButtonExpense.isChecked = true
        }

        editTextDate.setOnClickListener {
            DatePickerDialog(this, { _, year, month, day ->
                selectedDate.set(year, month, day)
                editTextDate.setText(dateFormat.format(selectedDate.time))
            }, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        buttonSave.setOnClickListener {
            val title = editTextTitle.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull() ?: 0.0
            val category = editTextCategory.text.toString()
            val type = if (radioGroupType.checkedRadioButtonId == R.id.radioButtonIncome) TransactionType.INCOME else TransactionType.EXPENSE
            val date = editTextDate.text.toString()

            if (title.isNotEmpty() && amount > 0 && date.isNotEmpty()) {
                if (record == null) {
                    val newRecord = FinanceRecord(title = title, amount = amount, category = category, date = date, type = type)
                    dbHelper.addRecord(newRecord)
                } else {
                    val updatedRecord = record.copy(title = title, amount = amount, category = category, date = date, type = type)
                    dbHelper.updateRecord(updatedRecord)
                }
                refreshData()
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showDeleteConfirmation(record: FinanceRecord) {
        AlertDialog.Builder(this)
            .setTitle("Delete Record")
            .setMessage("Are you sure you want to delete this record?")
            .setPositiveButton("Delete") { _, _ ->
                dbHelper.deleteRecord(record.id)
                refreshData()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshData() {
        val allRecordsInRange = dbHelper.getRecords(dateFormat.format(fromDate.time), dateFormat.format(toDate.time))

        var totalIncome = 0.0
        var totalExpense = 0.0
        for (record in allRecordsInRange) {
            if (record.type == TransactionType.INCOME) totalIncome += record.amount
            else totalExpense += record.amount
        }
        val netBalance = totalIncome - totalExpense

        tvTotalIncome.text = "$ ${String.format("%.2f", totalIncome)}"
        tvTotalExpense.text = "$ ${String.format("%.2f", totalExpense)}"
        tvNetBalance.text = "$ ${String.format("%.2f", netBalance)}"
        tvNetBalance.setTextColor(if (netBalance >= 0) android.graphics.Color.WHITE else android.graphics.Color.parseColor("#FF5252"))

        // Trigger notification if balance is low
        if (netBalance < 5000) {
            showNotification(netBalance)
        }

        val filteredRecords = when (currentTab) {
            1 -> allRecordsInRange.filter { it.type == TransactionType.INCOME }
            2 -> allRecordsInRange.filter { it.type == TransactionType.EXPENSE }
            else -> allRecordsInRange
        }

        adapter.updateData(filteredRecords)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Balance Alert"
            val descriptionText = "Notifications for low balance"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(balance: Double) {
        // Request permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
                return
            }
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Low Balance Alert")
            .setContentText("Your current balance is $ ${String.format("%.2f", balance)}, which is below $ 5000.00")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                notify(NOTIFICATION_ID, builder.build())
            }
        }
    }
}