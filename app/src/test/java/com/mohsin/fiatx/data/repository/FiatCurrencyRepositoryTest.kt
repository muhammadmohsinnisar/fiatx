package com.mohsin.fiatx.data.repository

import com.mohsin.fiatx.data.local.FiatCurrencyDao
import com.mohsin.fiatx.data.remote.CurrencyApiService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any

class FiatCurrencyRepositoryTest {

 private lateinit var apiService: CurrencyApiService
 private lateinit var fiatCurrencyDao: FiatCurrencyDao
 private lateinit var repository: FiatCurrencyRepository

 @Before
 fun setup() {
  apiService = mock(CurrencyApiService::class.java)
  fiatCurrencyDao = mock(FiatCurrencyDao::class.java)
  repository = FiatCurrencyRepository(apiService, fiatCurrencyDao)
 }

 @Test
 fun `fetchAndStoreIfEmpty should NOT fetch if database is not empty`() = runTest {
  val freshTimestamp = System.currentTimeMillis()

  `when`(fiatCurrencyDao.getLastUpdatedTime()).thenReturn(freshTimestamp)

  repository.fetchAndStoreIfEmpty()

  verify(apiService, never()).getSupportedCurrencies()
  verify(fiatCurrencyDao, never()).insertAll(any())
 }

}
