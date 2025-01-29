package com.password_generator

import android.content.ClipData
import android.os.Bundle
import android.content.ClipboardManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.password_generator.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var etPasswordName: EditText
    private lateinit var spinnerDifficulty: Spinner
    private lateinit var btnGeneratePassword: Button
    private lateinit var tvGeneratedPassword: TextView
    private lateinit var btnSavePassword: Button
    private lateinit var rvSavedPasswords: RecyclerView

    private lateinit var passwordGenerator: PasswordGenerator
    private lateinit var passwordDao: PasswordDao
    private lateinit var passwordAdapter: PasswordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupSpinner()
        setupRecyclerView()
        setupClickListeners()

        passwordGenerator = PasswordGenerator()
        passwordDao = AppDatabase.getDatabase(this).passwordDao()

        loadSavedPasswords()
    }

    private fun initializeViews() {
        etPasswordName = findViewById(R.id.etPasswordName)
        spinnerDifficulty = findViewById(R.id.spinnerDifficulty)
        btnGeneratePassword = findViewById(R.id.btnGeneratePassword)
        tvGeneratedPassword = findViewById(R.id.tvGeneratedPassword)
        btnSavePassword = findViewById(R.id.btnSavePassword)
        rvSavedPasswords = findViewById(R.id.rvSavedPasswords)
    }
    private fun deletePassword(password: Password) {
        lifecycleScope.launch(Dispatchers.IO) {
            passwordDao.delete(password)
            withContext(Dispatchers.Main) {
                loadSavedPasswords()
                Toast.makeText(this@MainActivity, "Password deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refreshPassword(password: Password) {
        val newPassword = passwordGenerator.generatePassword(password.difficulty)
        val updatedPassword = password.copy(password = newPassword)
        lifecycleScope.launch(Dispatchers.IO) {
            passwordDao.update(updatedPassword)
            withContext(Dispatchers.Main) {
                loadSavedPasswords()
                Toast.makeText(this@MainActivity, "Password refreshed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupSpinner() {
        val difficulties = Difficulty.values().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDifficulty.adapter = adapter
    }

    private fun setupRecyclerView() {
        passwordAdapter = PasswordAdapter(
            onDeleteClick = { password -> deletePassword(password) },
            onRefreshClick = { password -> refreshPassword(password) },
            onCopyClick = { password -> copyToClipboard(password) }
        )
        rvSavedPasswords.layoutManager = LinearLayoutManager(this)
        rvSavedPasswords.adapter = passwordAdapter
    }

    private fun copyToClipboard(password: String) {
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Password", password)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun setupClickListeners() {
        btnGeneratePassword.setOnClickListener {
            val selectedDifficulty = Difficulty.valueOf(spinnerDifficulty.selectedItem.toString())
            val generatedPassword = passwordGenerator.generatePassword(selectedDifficulty)
            tvGeneratedPassword.text = generatedPassword
        }

        btnSavePassword.setOnClickListener {
            val name = etPasswordName.text.toString()
            val password = tvGeneratedPassword.text.toString()
            val difficulty = Difficulty.valueOf(spinnerDifficulty.selectedItem.toString())

            if (name.isNotEmpty() && password.isNotEmpty()) {
                savePassword(Password(name = name, password = password, difficulty = difficulty))
            } else {
                Toast.makeText(this, "Please enter a name and generate a password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun savePassword(password: Password) {
        lifecycleScope.launch(Dispatchers.IO) {
            passwordDao.insert(password)
            withContext(Dispatchers.Main) {
                loadSavedPasswords()
                Toast.makeText(this@MainActivity, "Password saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadSavedPasswords() {
        lifecycleScope.launch(Dispatchers.IO) {
            val passwords = passwordDao.getAllPasswords()
            withContext(Dispatchers.Main) {
                passwordAdapter.submitList(passwords)
            }
        }
    }
}