package com.mohsin.fiatx.data.repository

import com.mohsin.fiatx.data.local.AppDatabase
import com.mohsin.fiatx.data.local.FiatCurrencyDao
import com.mohsin.fiatx.data.local.FiatCurrencyEntity
import com.mohsin.fiatx.data.remote.RetrofitClient
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class FiatCurrencyRepositoryTest {

 private lateinit var retrofitClient: RetrofitClient
 private lateinit var fiatCurrencyDao: FiatCurrencyDao
 private lateinit var repository: FiatCurrencyRepository

 @Before
 fun setup() {
  retrofitClient = mock(RetrofitClient::class.java)
  fiatCurrencyDao = mock(FiatCurrencyDao::class.java)

  val database = mock(AppDatabase::class.java)
  `when`(database.fiatCurrencyDao()).thenReturn(fiatCurrencyDao)

  repository = FiatCurrencyRepository(retrofitClient, database)
 }

 @Test
 fun `fetchAndStoreIfEmpty should NOT fetch if database is not empty`() = runTest {
  whenever(fiatCurrencyDao.getCount()).thenReturn(5)

  repository.fetchAndStoreIfEmpty()

  verify(retrofitClient, never()).getSupportedCurrencies()
  verify(fiatCurrencyDao, never()).insertAll(any())
 }

 @Test
 fun `getFiatCurrencies returns list from dao`() = runTest {
  val fakeList = listOf(
   mock(FiatCurrencyEntity::class.java),
   mock(FiatCurrencyEntity::class.java)
  )
  whenever(fiatCurrencyDao.getAll()).thenReturn(fakeList)

  val result = repository.getFiatCurrencies()

  verify(fiatCurrencyDao).getAll()
  assert(result == fakeList)
 }
}
