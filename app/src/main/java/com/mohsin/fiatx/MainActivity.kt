package com.mohsin.fiatx

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mohsin.fiatx.data.local.AppDatabase
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.remote.RetrofitClient
import com.mohsin.fiatx.data.repository.FiatCurrencyRepository
import com.mohsin.fiatx.viewmodel.CurrencyViewModel
import com.mohsin.fiatx.viewmodel.CurrencyViewModelFactory
import com.mohsin.fiatx.adapter.CurrencySearchAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    // UI Components
    private lateinit var fromCurrencySpinner: MaterialAutoCompleteTextView
    private lateinit var toCurrencySpinner: MaterialAutoCompleteTextView
    private lateinit var amountInput: TextInputEditText
    private lateinit var amountInputLayout: TextInputLayout
    private lateinit var convertButton: MaterialButton
    private lateinit var swapButton: MaterialButton
    private lateinit var retryButton: MaterialButton
    
    // Cards
    private lateinit var resultCard: MaterialCardView
    private lateinit var errorCard: MaterialCardView
    private lateinit var loadingOverlay: FrameLayout
    
    // Result Views
    private lateinit var resultAmount: TextView
    private lateinit var exchangeRate: TextView
    private lateinit var lastUpdated: TextView
    private lateinit var loadingText: TextView
    private lateinit var errorTitle: TextView
    private lateinit var errorMessage: TextView

    private lateinit var viewModel: CurrencyViewModel
    private var currencies: List<FiatCurrencyEntity> = emptyList()
    private var isResultVisible = false // Track if result is currently shown
    private var lastConversionParams: Triple<String, String, Double>? = null // Track last conversion
    private lateinit var fromCurrencyAdapter: CurrencySearchAdapter
    private lateinit var toCurrencyAdapter: CurrencySearchAdapter
    private val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault()).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // Enable Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        setContentView(R.layout.activity_main)
        
        initializeViews()
        setupEdgeToEdge()
        setupViewModel()
        setupUI()
        observeViewModel()
        
        // Load currencies
        viewModel.loadCurrencies(isNetworkAvailable())
    }

    private fun initializeViews() {
        fromCurrencySpinner = findViewById(R.id.fromCurrencySpinner)
        toCurrencySpinner = findViewById(R.id.toCurrencySpinner)
        amountInput = findViewById(R.id.amountInput)
        amountInputLayout = findViewById(R.id.amountInputLayout)
        convertButton = findViewById(R.id.convertButton)
        swapButton = findViewById(R.id.swapButton)
        retryButton = findViewById(R.id.retryButton)
        
        resultCard = findViewById(R.id.resultCard)
        errorCard = findViewById(R.id.errorCard)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        
        resultAmount = findViewById(R.id.resultAmount)
        exchangeRate = findViewById(R.id.exchangeRate)
        lastUpdated = findViewById(R.id.lastUpdated)
        loadingText = findViewById(R.id.loadingText)
        errorTitle = findViewById(R.id.errorTitle)
        errorMessage = findViewById(R.id.errorMessage)
    }

    private fun setupEdgeToEdge() {
        // Handle window insets for Edge-to-Edge without toolbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left + 16, // Add original padding
                systemBars.top + 16,  // Add original padding
                systemBars.right + 16, // Add original padding
                systemBars.bottom + 16 // Add original padding
            )
            insets
        }
    }

    private fun setupViewModel() {
        val db = AppDatabase.getInstance(this)
        val retrofitClient = RetrofitClient()
        val repo = FiatCurrencyRepository(retrofitClient, db)
        val factory = CurrencyViewModelFactory(repo)
        viewModel = ViewModelProvider(this, factory)[CurrencyViewModel::class.java]
    }

    private fun setupUI() {
        // Setup amount input validation
        amountInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                lastConversionParams = null // Reset when amount changes
                validateInput()
            }
        })

        // Setup convert button
        convertButton.setOnClickListener {
            performConversion()
        }

        // Setup swap button
        swapButton.setOnClickListener {
            swapCurrencies()
        }

        // Setup retry button
        retryButton.setOnClickListener {
            hideError()
            viewModel.loadCurrencies(isNetworkAvailable())
        }

        // Initially disable convert button
        convertButton.isEnabled = false
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.isLoading.collectLatest { loading ->
                showLoading(loading, getString(R.string.loading))
                updateUIState(!loading)
            }
        }

        lifecycleScope.launch {
            viewModel.currencies.collectLatest { currencyList ->
                if (currencyList.isNotEmpty()) {
                    currencies = currencyList
                    setupCurrencySpinners(currencyList)
                    validateInput()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.conversionResult.collectLatest { result ->
                result?.let { showResult(it) }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collectLatest { error ->
                error?.let { 
                    showError(getString(R.string.error_conversion_failed), it)
                    viewModel.clearError()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.networkError.collectLatest { error ->
                error?.let { 
                    showError(getString(R.string.error_no_internet), getString(R.string.error_no_internet_desc))
                }
            }
        }
    }

    private fun setupCurrencySpinners(currencies: List<FiatCurrencyEntity>) {
        // Create custom search adapters
        fromCurrencyAdapter = CurrencySearchAdapter(this, currencies)
        toCurrencyAdapter = CurrencySearchAdapter(this, currencies)
        
        fromCurrencySpinner.setAdapter(fromCurrencyAdapter)
        toCurrencySpinner.setAdapter(toCurrencyAdapter)
        
        // Configure search behavior for better UX
        fromCurrencySpinner.threshold = 1
        toCurrencySpinner.threshold = 1

        // Set default selections (USD and EUR if available)
        val usdIndex = currencies.indexOfFirst { it.code.equals("usd", ignoreCase = true) }
        val eurIndex = currencies.indexOfFirst { it.code.equals("eur", ignoreCase = true) }
        
        if (usdIndex >= 0) {
            val usdText = "${currencies[usdIndex].code.uppercase()} - ${currencies[usdIndex].name}"
            fromCurrencySpinner.setText(usdText, false)
        }
        if (eurIndex >= 0) {
            val eurText = "${currencies[eurIndex].code.uppercase()} - ${currencies[eurIndex].name}"
            toCurrencySpinner.setText(eurText, false)
        }

        // Add listeners for selection
        fromCurrencySpinner.setOnItemClickListener { _, _, position, _ -> 
            lastConversionParams = null // Reset when currency changes
            hideError() // Hide any previous errors
            validateInput() 
        }
        toCurrencySpinner.setOnItemClickListener { _, _, position, _ -> 
            lastConversionParams = null // Reset when currency changes
            hideError() // Hide any previous errors
            validateInput() 
        }
        
        // Add text change listeners for real-time search and validation
        fromCurrencySpinner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                lastConversionParams = null // Reset when currency changes
                validateInput()
            }
        })
        
        toCurrencySpinner.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                lastConversionParams = null // Reset when currency changes
                validateInput()
            }
        })
    }

    private fun validateInput(): Boolean {
        val amount = amountInput.text.toString().trim()
        val fromCurrency = fromCurrencySpinner.text.toString()
        val toCurrency = toCurrencySpinner.text.toString()

        val isValid = amount.isNotBlank() && 
                     amount.toDoubleOrNull() != null && 
                     amount.toDouble() > 0 &&
                     fromCurrency.isNotBlank() && 
                     toCurrency.isNotBlank() &&
                     currencies.isNotEmpty()

        convertButton.isEnabled = isValid && !viewModel.isLoading.value

        // Clear error state on amount input
        if (amount.isNotBlank() && amount.toDoubleOrNull() == null) {
            amountInputLayout.error = getString(R.string.invalid_amount)
        } else {
            amountInputLayout.error = null
        }

        return isValid
    }

    private fun performConversion() {
        if (!validateInput() || viewModel.isLoading.value) return

        val amountText = amountInput.text.toString().trim()
        val amount = amountText.toDoubleOrNull() ?: return
        
        val fromCurrency = fromCurrencySpinner.text.toString().substringBefore(" -").lowercase()
        val toCurrency = toCurrencySpinner.text.toString().substringBefore(" -").lowercase()

        // Check if this is the same conversion as before
        val currentParams = Triple(fromCurrency, toCurrency, amount)
        if (currentParams == lastConversionParams && isResultVisible) {
            // Same conversion and result is already visible, don't do anything
            return
        }

        hideError()
        if (!isResultVisible) {
            hideResult() // Only hide if not already showing a result
        }
        
        lastConversionParams = currentParams
        viewModel.convertCurrency(fromCurrency, toCurrency, amount)
    }

    private fun swapCurrencies() {
        val fromText = fromCurrencySpinner.text.toString()
        val toText = toCurrencySpinner.text.toString()
        
        // Only swap if both fields have valid selections
        if (fromText.isNotBlank() && toText.isNotBlank()) {
            fromCurrencySpinner.setText(toText, false)
            toCurrencySpinner.setText(fromText, false)
            
            // Reset conversion params since currencies changed
            lastConversionParams = null
            
            // Add swap animation
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
            swapButton.startAnimation(animation)
            
            validateInput()
        }
    }

    private fun showResult(result: String) {
        // Parse result to show formatted amount
        resultAmount.text = result
        
        // Show exchange rate
        val fromCurrency = fromCurrencySpinner.text.toString().substringBefore(" -").uppercase()
        val toCurrency = toCurrencySpinner.text.toString().substringBefore(" -").uppercase()
        val amount = amountInput.text.toString().toDoubleOrNull() ?: 1.0
        val convertedAmount = result.substringBefore(" ").toDoubleOrNull() ?: 0.0
        val rate = if (amount > 0) convertedAmount / amount else 0.0
        
        exchangeRate.text = "1 $fromCurrency = ${numberFormat.format(rate)} $toCurrency"
        
        // Show last updated time
        val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
        lastUpdated.text = getString(R.string.last_updated, dateFormat.format(Date()))
        
        // Only show animation and snackbar if result wasn't already visible
        if (!isResultVisible) {
            resultCard.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
            resultCard.startAnimation(animation)
            
            // Show success snackbar
            Snackbar.make(findViewById(android.R.id.content), 
                         getString(R.string.conversion_successful), 
                         Snackbar.LENGTH_SHORT).show()
        } else {
            // Just make sure it's visible (update content without animation)
            resultCard.visibility = View.VISIBLE
        }
        
        isResultVisible = true
    }

    private fun showError(title: String, message: String) {
        hideResult()
        
        errorTitle.text = title
        errorMessage.text = message
        errorCard.visibility = View.VISIBLE
        
        val animation = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        errorCard.startAnimation(animation)
    }

    private fun hideError() {
        errorCard.visibility = View.GONE
    }

    private fun hideResult() {
        resultCard.visibility = View.GONE
        isResultVisible = false
    }

    private fun showLoading(show: Boolean, message: String = getString(R.string.loading)) {
        loadingOverlay.visibility = if (show) View.VISIBLE else View.GONE
        loadingText.text = message
    }

    private fun updateUIState(enabled: Boolean) {
        fromCurrencySpinner.isEnabled = enabled
        toCurrencySpinner.isEnabled = enabled
        amountInput.isEnabled = enabled
        swapButton.isEnabled = enabled
        if (enabled) {
            validateInput()
        } else {
            convertButton.isEnabled = false
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}