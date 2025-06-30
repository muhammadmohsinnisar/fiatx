package com.mohsin.fiatx.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.repository.FiatCurrencyRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val fiatCurrencyRepository: FiatCurrencyRepository
) : ViewModel() {

    private val _currencies = MutableStateFlow<List<FiatCurrencyEntity>>(emptyList())
    val currencies: StateFlow<List<FiatCurrencyEntity>> = _currencies

    private val _conversionResult = MutableStateFlow<String?>(null)
    val conversionResult: StateFlow<String?> = _conversionResult

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadCurrencies() {
        viewModelScope.launch {
            try {
                val fetchCurrencies = async { fiatCurrencyRepository.fetchAndStoreIfEmpty() }
                val fetchRates = async { fiatCurrencyRepository.fetchAndCachePopularRates() }

                fetchCurrencies.await()
                fetchRates.await()

                val list = fiatCurrencyRepository.getFiatCurrencies()
                _currencies.value = list
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load currencies: ${e.message}"
            }
        }
    }

    fun convertCurrency(base: String, target: String, amount: Double) {
        viewModelScope.launch {
            try {
                val cachedRate = fiatCurrencyRepository.getRateFromDb(base, target)
                val response = fiatCurrencyRepository.getRateFromNetwork(base)
                val baseData = response[base] as? Map<*, *>
                val rate = when(val rateRaw = baseData?.get(target)) {
                    is Number -> rateRaw.toDouble()
                    is String -> rateRaw.toDoubleOrNull()
                    else -> null
                }
                if (rate != null) {
                    val result = amount * rate
                    _conversionResult.value = String.format("%.2f %s", result, target.uppercase())
                } else {
                    _errorMessage.value = "Rate not available"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Conversion failed: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
