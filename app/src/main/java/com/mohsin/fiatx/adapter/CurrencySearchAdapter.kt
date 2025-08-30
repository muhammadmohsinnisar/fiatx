package com.mohsin.fiatx.adapter

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import com.mohsin.fiatx.data.local.FiatCurrencyEntity

class CurrencySearchAdapter(
    context: Context,
    private val currencies: List<FiatCurrencyEntity>
) : ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line), Filterable {

    private var filteredCurrencies = currencies.toMutableList()
    private val currencyFilter = CurrencyFilter()

    init {
        // Initialize with all currencies
        addAll(filteredCurrencies.map { "${it.code.uppercase()} - ${it.name}" })
    }

    override fun getFilter(): Filter = currencyFilter

    private inner class CurrencyFilter : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterResults = FilterResults()
            
            if (constraint.isNullOrBlank()) {
                // Show all currencies if no search term
                filterResults.values = currencies
                filterResults.count = currencies.size
            } else {
                val searchTerm = constraint.toString().lowercase().trim()
                
                // Filter currencies by code or name
                val filtered = currencies.filter { currency ->
                    currency.code.lowercase().contains(searchTerm) ||
                    currency.name.lowercase().contains(searchTerm) ||
                    currency.code.lowercase().startsWith(searchTerm) ||
                    currency.name.lowercase().startsWith(searchTerm)
                }.sortedWith(compareBy<FiatCurrencyEntity> { currency ->
                    // Prioritize exact matches and starts-with matches
                    when {
                        currency.code.lowercase() == searchTerm -> 0
                        currency.code.lowercase().startsWith(searchTerm) -> 1
                        currency.name.lowercase().startsWith(searchTerm) -> 2
                        else -> 3
                    }
                }.thenBy { it.code })
                
                filterResults.values = filtered
                filterResults.count = filtered.size
            }
            
            return filterResults
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredCurrencies.clear()
            
            if (results?.values != null) {
                filteredCurrencies.addAll(results.values as List<FiatCurrencyEntity>)
            }
            
            clear()
            addAll(filteredCurrencies.map { "${it.code.uppercase()} - ${it.name}" })
            notifyDataSetChanged()
        }
    }

    fun getFilteredCurrency(position: Int): FiatCurrencyEntity? {
        return if (position >= 0 && position < filteredCurrencies.size) {
            filteredCurrencies[position]
        } else null
    }

    fun findCurrencyPosition(currencyCode: String): Int {
        return filteredCurrencies.indexOfFirst { 
            it.code.equals(currencyCode, ignoreCase = true) 
        }
    }
}
