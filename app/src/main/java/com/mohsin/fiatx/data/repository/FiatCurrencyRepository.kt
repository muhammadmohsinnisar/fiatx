package com.mohsin.fiatx.data.repository

import android.util.Log
import com.mohsin.fiatx.data.local.AppDatabase
import com.mohsin.fiatx.data.local.ExchangeRateEntity
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.remote.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class FiatCurrencyRepository(
    private val retrofitClient: RetrofitClient,
    private val database: AppDatabase
) {
    private val fiatCurrencyDao = database.fiatCurrencyDao()
    private val exchangeRateDao = database.exchangeRateDao()

    suspend fun fetchAndStoreIfEmpty() {
        val count = fiatCurrencyDao.getCount()
        if (count == 0) {
            Log.d("FiatCurrencyRepository", "No currencies in DB, fetching from API...")
            val rawCurrencies = retrofitClient.getSupportedCurrencies()

            val now = System.currentTimeMillis()
            val filtered = rawCurrencies
                .filterKeys { key -> key.all { it.isLetter() } && key.length == 3 }
                .map { FiatCurrencyEntity(it.key.lowercase(), it.value, now) }

            fiatCurrencyDao.insertAll(filtered)
        }
    }

    suspend fun fetchAndCachePopularRates() = coroutineScope {
        val fiatCurrencies = listOf(
            "aed", "afn", "all", "amd", "ang", "aoa", "ars", "awg",
            "azn", "bam", "bbd", "bdt", "bgn", "bhd", "bif", "bmd",
            "bnd", "bob", "brl", "bsd", "btn", "bwp", "byn", "bzd",
            "cad", "cdf", "chf", "clp", "cnh", "cny", "cop", "crc",
            "cuc", "cup", "cve", "czk", "djf", "dkk", "dop", "dzd",
            "egp", "ern", "etb", "eur", "fjd", "fkp", "gbp", "gel",
            "ghs", "gip", "gmd", "gnf", "gtq", "gyd", "hkd", "hnl",
            "hrk", "htg", "huf", "idr", "ils", "inr", "iqd", "irr",
            "isk", "jmd", "jod", "jpy", "kes", "kgs", "khr", "kmf",
            "kpw", "krw", "kwd", "kyd", "kzt", "lak", "lbp", "lkr",
            "lsl", "lyd", "mad", "mdl", "mga", "mkd", "mmk", "mnt",
            "mop", "mro", "mur", "mvr", "mwk", "mxn", "myr", "mzn",
            "nad", "ngn", "nio", "nok", "npr", "nzd", "omr", "pab",
            "pen", "pgk", "php", "pkr", "pln", "pyg", "qar", "ron",
            "rsd", "rub", "rwf", "sar", "sbd", "scr", "sdg", "sek",
            "sgd", "shp", "sll", "sos", "srd", "stn", "svc", "szl",
            "thb", "tjs", "tmt", "tnd", "top", "try", "ttd", "twd",
            "tzs", "uah", "ugx", "usd", "uyu", "uzs", "vef", "vnd",
            "vuv", "wst", "xaf", "xcd", "xof", "xpf", "yer", "zar",
            "zmw", "zwl"
        )
        val now = System.currentTimeMillis()

        val jobs = fiatCurrencies.map { base ->
            async {
                try {
                    val ratesMap = retrofitClient.getLatestRatesForBase(base)
                    val rates = ratesMap["rates"] as? Map<*, *> ?: ratesMap

                    val entities = rates.map { (target, rate) ->
                        ExchangeRateEntity(
                            base = base,
                            target = target as String,
                            rate = (rate as? Number)?.toDouble() ?: 0.0,
                            date = getCurrentDate(),
                            timestamp = now
                        )
                    }

                    exchangeRateDao.deleteRatesForBase(base)
                    exchangeRateDao.insertRates(entities)
                } catch (e: Exception) {
                    Log.e("FiatCurrencyRepository", "Failed fetching rates for base $base", e)
                }
            }
        }
        jobs.forEach { it.await() }
    }

    suspend fun getFiatCurrencies(): List<FiatCurrencyEntity> = fiatCurrencyDao.getAll()

    suspend fun getRateFromNetwork(base: String): Map<String, Any> {
        return retrofitClient.getLatestRatesForBase(base)
    }

    suspend fun getRateFromDb(base: String, target: String): Double? {
        return exchangeRateDao.getRatesByBase(base)
            .firstOrNull { it.target.equals(target, ignoreCase = true) }
            ?.rate
    }

    private fun getCurrentDate(): String {
        val formatter = java.text.SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(System.currentTimeMillis())
    }
}

