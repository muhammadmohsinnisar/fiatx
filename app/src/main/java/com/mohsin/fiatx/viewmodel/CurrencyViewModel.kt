package com.mohsin.fiatx.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.repository.FiatCurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val fiatCurrencyRepository: FiatCurrencyRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _networkError = MutableStateFlow<String?>(null)
    val networkError: StateFlow<String?> = _networkError

    private val _currencies = MutableStateFlow<List<FiatCurrencyEntity>>(emptyList())
    val currencies: StateFlow<List<FiatCurrencyEntity>> = _currencies

    private val _conversionResult = MutableStateFlow<String?>(null)
    val conversionResult: StateFlow<String?> = _conversionResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadCurrencies(hasInternet: Boolean) {
        viewModelScope.launch {
            if (!hasInternet) {
                _networkError.value = "No internet connection. Please connect to the internet to load currency data."
                return@launch
            }
            _networkError.value = null
            _isLoading.value = true
            try {
                fiatCurrencyRepository.fetchAndStoreIfEmpty()
                fiatCurrencyRepository.fetchAndCachePopularRates()

                val list = fiatCurrencyRepository.getFiatCurrencies()
                _currencies.value = list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load currencies: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    @SuppressLint("DefaultLocale")
    fun convertCurrency(base: String, target: String, amount: Double) {
        viewModelScope.launch {
            try {
                val rate = fiatCurrencyRepository.getRateFromDb(base, target)
                if (rate != null) {
                    Log.d("CurrencyViewModel", "Viewing from DB")
                    val result = amount * rate
                    _conversionResult.value = String.format("%.2f %s", result, target.uppercase())
                } else {
                    Log.d("CurrencyViewModel", "Rate not found in DB, fetching from network")
                    val response = fiatCurrencyRepository.getRateFromNetwork(base)
                    val baseData = response[base] as? Map<*, *>
                    val rateFromNetwork = when (val rateRaw = baseData?.get(target)) {
                        is Number -> rateRaw.toDouble()
                        is String -> rateRaw.toDoubleOrNull()
                        else -> null
                    }

                    if (rateFromNetwork != null) {
                        val result = amount * rateFromNetwork
                        _conversionResult.value = String.format("%.2f %s", result, target.uppercase())
                    } else {
                        _errorMessage.value = "Rate not available"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Conversion failed: ${e.message}"
                Log.d("CurrencyViewModel", "Conversion failed: ${e.message}")
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
