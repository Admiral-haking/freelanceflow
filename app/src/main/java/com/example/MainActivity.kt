package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.repository.FreelanceFlowRepository
import com.example.ui.theme.FreelanceFlowTheme
import com.example.ui.viewmodel.FreelanceFlowViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: FreelanceFlowViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = FreelanceFlowRepository(database)

        val factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FreelanceFlowViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[FreelanceFlowViewModel::class.java]

        setContent {
            FreelanceFlowTheme {
                FreelanceFlowApp(viewModel = viewModel)
            }
        }
    }
}
