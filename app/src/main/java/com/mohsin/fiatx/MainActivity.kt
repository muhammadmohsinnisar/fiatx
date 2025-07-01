package com.mohsin.fiatx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mohsin.fiatx.data.local.AppDatabase
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.remote.RetrofitClient
import com.mohsin.fiatx.data.repository.FiatCurrencyRepository
import com.mohsin.fiatx.viewmodel.CurrencyViewModel
import com.mohsin.fiatx.viewmodel.CurrencyViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var spinnerBase: Spinner
    private lateinit var spinnerTarget: Spinner
    private lateinit var editTextAmount: EditText
    private lateinit var buttonConvert: Button
    private lateinit var resultLayout: View
    private lateinit var textConvertedAmount: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var textLoadingMessage: TextView
    private var startTime: Long = 0L


    private lateinit var viewModel: CurrencyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startTime = System.currentTimeMillis()
        spinnerBase = findViewById(R.id.spinnerBase)
        spinnerTarget = findViewById(R.id.spinnerTarget)
        editTextAmount = findViewById(R.id.editTextAmount)
        buttonConvert = findViewById(R.id.buttonConvert)
        resultLayout = findViewById(R.id.resultLayout)
        textConvertedAmount = findViewById(R.id.textConvertedAmount)
        progressBar = findViewById(R.id.progressBar)
        textLoadingMessage = findViewById(R.id.textLoadingMessage)

        setConvertButtonEnabled(false)

        val isInternetAvailable = this.isNetworkAvailable()

        val db = AppDatabase.getInstance(this)
        val retrofitClient = RetrofitClient()
        val repo = FiatCurrencyRepository(retrofitClient, db)
        val factory = CurrencyViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[CurrencyViewModel::class.java]

        lifecycleScope.launch {
            viewModel.networkError.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                textLoadingMessage.visibility = if (loading) View.VISIBLE else View.GONE

                spinnerBase.isEnabled = !loading
                spinnerTarget.isEnabled = !loading
                editTextAmount.isEnabled = !loading
                buttonConvert.isEnabled = !loading && (viewModel.currencies.value.isNotEmpty())
            }
        }

        lifecycleScope.launch {
            viewModel.currencies.collectLatest { currencies ->
                if (currencies.isNotEmpty()) {
                    updateCurrencySpinners(currencies)
                    setConvertButtonEnabled(true)
                } else {
                    setConvertButtonEnabled(false)
                }
            }
        }
        viewModel.loadCurrencies(isInternetAvailable)


        lifecycleScope.launch {
            viewModel.conversionResult.collectLatest { result ->
                if (result != null) {
                    textConvertedAmount.text = result
                    resultLayout.visibility = View.VISIBLE
                } else {
                    resultLayout.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let {
                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show()
                    viewModel.clearError()
                }
            }
        }

        buttonConvert.setOnClickListener {
            val amountText = editTextAmount.text.toString().trim()
            val base = spinnerBase.selectedItem?.toString()?.substringBefore(" -")?.lowercase() ?: return@setOnClickListener
            val target = spinnerTarget.selectedItem?.toString()?.substringBefore(" -")?.lowercase() ?: return@setOnClickListener

            if (amountText.isNotBlank()) {
                val amount = amountText.toDoubleOrNull()
                if (amount != null) {
                    viewModel.convertCurrency(base, target, amount)
                } else {
                    Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setConvertButtonEnabled(flag: Boolean) {
        buttonConvert.isEnabled = flag
    }

    private fun updateCurrencySpinners(currencies: List<FiatCurrencyEntity>) {
        val currencyNames = currencies.map { "${it.code.uppercase()} - ${it.name}" }
        val adapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_spinner_item,
            currencyNames
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        spinnerBase.adapter = adapter
        spinnerTarget.adapter = adapter

        spinnerBase.setSelection(currencyNames.indexOfFirst { it.startsWith("USD") }
            .coerceAtLeast(0))
        spinnerTarget.setSelection(currencyNames.indexOfFirst { it.startsWith("PKR") }
            .coerceAtLeast(0))
    }

    private fun Context.isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
