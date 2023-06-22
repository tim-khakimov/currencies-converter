package com.timkhakimov.currenciesconverter.presentation

import com.timkhakimov.currenciesconverter.BaseTestCase
import com.timkhakimov.currenciesconverter.MainTestDispatcherRule
import com.timkhakimov.currenciesconverter.data.Constants.BASE_CURRENCY
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.domain.ConverterInteractor
import com.timkhakimov.currenciesconverter.domain.CurrenciesInteractor
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue
import com.timkhakimov.currenciesconverter.presentation.model.UiState
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger

@ExperimentalCoroutinesApi
class ConverterViewModelTest : BaseTestCase() {

    @get:Rule
    val coroutineTestRule = MainTestDispatcherRule()

    @MockK
    lateinit var currenciesInteractor: CurrenciesInteractor

    @MockK
    lateinit var converterInteractor: ConverterInteractor

    private val reducer = ConverterViewModelReducer()

    private val mapper = UiStateMapper()

    private lateinit var converterViewModel: ConverterViewModel

    private fun createViewModel() {
        converterViewModel = ConverterViewModel(currenciesInteractor, converterInteractor, reducer, mapper)
    }

    @Test
    fun `should not do anything after input invalid value`() = runTest {
        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY)
        )

        runCurrent()

        converterViewModel.onTextInput(" ")

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show rates list after input valid value and get valid currencies and rates`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        coEvery {
            converterInteractor.getCurrenciesRates(BASE_CURRENCY, any())
        } returns CURRENCY_VALUES_MAP_FOR_USD

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.ContentLoaded(
                BASE_CURRENCY,
                listOf(
                    CurrencyValue(Currency("EUR", "Euro"), 0.93),
                    CurrencyValue(Currency("GBP", "Pound"), 0.79),
                    CurrencyValue(Currency("JPY", "Yen"), 139.4),
                    CurrencyValue(Currency("CHF", "Franc"), null)
                )
            )
        )

        runCurrent()

        converterViewModel.onTextInput("1")

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show currencies selection after click to target currency`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        createViewModel()

        val collectedStates = mutableListOf<List<Currency>>()
        val collectJob = converterViewModel.currenciesSelectionEvent
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)


        runCurrent()

        converterViewModel.onTargetCurrencyClicked()

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(listOf(CURRENCIES_LIST), collectedStates)
    }

    @Test
    fun `should not load and show rates after select target currency with invalid input value`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        coEvery {
            converterInteractor.getCurrenciesRates(any(), any())
        } returns CURRENCY_VALUES_MAP_FOR_USD

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Initial("EUR"),
        )

        runCurrent()
        converterViewModel.onTargetCurrencyClicked()

        runCurrent()
        converterViewModel.onCurrencySelect(Currency("EUR", "Euro"))

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show rates after select target currency with valid input value`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        coEvery {
            converterInteractor.getCurrenciesRates("USD", 1.0)
        } returns CURRENCY_VALUES_MAP_FOR_USD

        coEvery {
            converterInteractor.getCurrenciesRates("EUR", 1.0)
        } returns CURRENCY_VALUES_MAP_FOR_EUR

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.ContentLoaded(
                BASE_CURRENCY,
                listOf(
                    CurrencyValue(Currency("EUR", "Euro"), 0.93),
                    CurrencyValue(Currency("GBP", "Pound"), 0.79),
                    CurrencyValue(Currency("JPY", "Yen"), 139.4),
                    CurrencyValue(Currency("CHF", "Franc"), null)
                )
            ),
            UiState.Loading("EUR"),
            UiState.ContentLoaded(
                "EUR",
                listOf(
                    CurrencyValue(Currency("GBP", "Pound"), 0.83),
                    CurrencyValue(Currency("JPY", "Yen"), 146.7),
                    CurrencyValue(Currency("USD", "Dollar"), 1.05),
                    CurrencyValue(Currency("CHF", "Franc"), null)
                )
            )
        )

        runCurrent()

        converterViewModel.onTextInput("1")

        advanceUntilIdle()

        converterViewModel.onCurrencySelect(Currency("EUR", "Euro"))

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show error after get currencies exception after click to target currency`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } throws Exception()

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Error(BASE_CURRENCY),
        )

//        runCurrent()

        converterViewModel.onTargetCurrencyClicked()

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show error after get currencies exception after input valid value`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } throws Exception()

        coEvery {
            converterInteractor.getCurrenciesRates(any(), any())
        } returns CURRENCY_VALUES_MAP_FOR_USD

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Error(BASE_CURRENCY)
        )

        runCurrent()

        converterViewModel.onTextInput("1")

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should show error after get rates exception after input valid value`() = runTest {
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        coEvery {
            converterInteractor.getCurrenciesRates(any(), any())
        } throws Exception()

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Error(BASE_CURRENCY)
        )

        runCurrent()

        converterViewModel.onTextInput("1")

        advanceUntilIdle()

        collectJob.cancelAndJoin()

        assertEquals(expectedStates, collectedStates)
    }

    @Test
    fun `should reload currencies after target currency clicked get exception and click to retry`() = runTest {
        val callCount = AtomicInteger(0)
        coEvery {
            currenciesInteractor.getCurrencies()
        } answers {
            callCount.incrementAndGet()
            if (callCount.get() == 1) {
                throw Exception()
            } else {
                CURRENCIES_LIST
            }
        }

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectUiStatesJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedUiStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Error(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Initial(BASE_CURRENCY)
        )

        val collectedEvents = mutableListOf<List<Currency>>()

        val collectEventsJob = converterViewModel.currenciesSelectionEvent
            .onEach { state -> collectedEvents.add(state) }
            .launchIn(this)

        runCurrent()
        converterViewModel.onTargetCurrencyClicked()

        runCurrent()
        converterViewModel.onRetryClicked()

        advanceUntilIdle()

        collectUiStatesJob.cancelAndJoin()
        collectEventsJob.cancelAndJoin()

        assertEquals(expectedUiStates, collectedStates)
        assertEquals(listOf(CURRENCIES_LIST), collectedEvents)
    }

    @Test
    fun `should reload currencies and rates after input valid value get exception and click to retry`() = runTest {
        val callCount = AtomicInteger(0)
        coEvery {
            currenciesInteractor.getCurrencies()
        } returns CURRENCIES_LIST

        coEvery {
            converterInteractor.getCurrenciesRates(any(), any())
        } answers {
            callCount.incrementAndGet()
            if (callCount.get() == 1) {
                throw Exception()
            } else {
                CURRENCY_VALUES_MAP_FOR_USD
            }
        }

        createViewModel()

        val collectedStates = mutableListOf<UiState>()
        val collectUiStatesJob = converterViewModel.uiState
            .onEach { state -> collectedStates.add(state) }
            .launchIn(this)

        val expectedUiStates = listOf(
            UiState.Initial(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.Error(BASE_CURRENCY),
            UiState.Loading(BASE_CURRENCY),
            UiState.ContentLoaded(
                BASE_CURRENCY,
                listOf(
                    CurrencyValue(Currency("EUR", "Euro"), 0.93),
                    CurrencyValue(Currency("GBP", "Pound"), 0.79),
                    CurrencyValue(Currency("JPY", "Yen"), 139.4),
                    CurrencyValue(Currency("CHF", "Franc"), null)
                )
            ),
        )

        runCurrent()
        converterViewModel.onTextInput("1")

        runCurrent()
        converterViewModel.onRetryClicked()

        advanceUntilIdle()

        collectUiStatesJob.cancelAndJoin()

        assertEquals(expectedUiStates, collectedStates)
    }

    private companion object {
        val CURRENCIES_LIST = listOf(
            Currency("EUR", "Euro"),
            Currency("GBP", "Pound",),
            Currency("JPY", "Yen"),
            Currency("USD", "Dollar"),
            Currency("CHF", "Franc"),
        )

        val CURRENCY_VALUES_MAP_FOR_USD = mapOf(
            "EUR" to 0.93,
            "GBP" to 0.79,
            "JPY" to 139.4,
            "USD" to 1.0
        )


        val CURRENCY_VALUES_MAP_FOR_EUR = mapOf(
            "EUR" to 1.0,
            "GBP" to 0.83,
            "JPY" to 146.7,
            "USD" to 1.05
        )
    }
}