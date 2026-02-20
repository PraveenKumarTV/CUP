package com.example.cup2.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.cup2.R

class UploadResumeActivity : AppCompatActivity() {

    private lateinit var fileNameTextView: TextView
    private lateinit var generateQuestionsButton: Button
    private var selectedFileUri: Uri? = null

    private val getPdfResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedFileUri = uri
                val fileName = getFileName(uri)
                fileNameTextView.text = fileName
                generateQuestionsButton.visibility = View.VISIBLE
                Toast.makeText(this, "Resume uploaded: $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_resume)

        fileNameTextView = findViewById(R.id.fileNameTextView)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        generateQuestionsButton = findViewById(R.id.generateQuestionsButton)

        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
            }
            getPdfResult.launch(intent)
        }

        generateQuestionsButton.setOnClickListener {
            // We will implement the question generation logic here in the next steps.
            Toast.makeText(this, "Generating questions from resume...", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex)
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                if (result != null) {
                    result = result.substring(cut!! + 1)
                }
            }
        }
        return result ?: "Unknown"
    }
}
