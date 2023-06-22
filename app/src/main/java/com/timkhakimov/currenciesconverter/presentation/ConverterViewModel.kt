package com.timkhakimov.currenciesconverter.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.domain.ConverterInteractor
import com.timkhakimov.currenciesconverter.domain.CurrenciesInteractor
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue
import com.timkhakimov.currenciesconverter.presentation.model.UiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ConverterViewModel(
    private val currenciesInteractor: CurrenciesInteractor,
    private val converterInteractor: ConverterInteractor,
    private val reducer: ConverterViewModelReducer,
    private val mapper: UiStateMapper,
) : ViewModel() {

    private val state
        get() = reducer.viewModelState.value

    val uiState: Flow<UiState> = reducer.viewModelState.map {
        mapper.map(it)
    }

    private val _currenciesSelectionEvent = Channel<List<Currency>>()
    val currenciesSelectionEvent: Flow<List<Currency>> = _currenciesSelectionEvent.receiveAsFlow()

    private var ratesLoadingJob: Job? = null

    fun onTargetCurrencyClicked() {
        loadCurrencies()
    }

    private fun loadCurrencies() {
        viewModelScope.launch(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                reducer.setCurrenciesError()
            }
        ) {
            reducer.setLoading(true)
            val currencies = async { currenciesInteractor.getCurrencies() }.await()
            _currenciesSelectionEvent.send(currencies)
            reducer.setLoading(false)
        }
    }

    fun onTextInput(text: String) {
        cancelPreviousRatesLoading()
        val value = text.toDoubleOrNull()
        reducer.setInputValue(value)
        if (value != null) {
            loadCurrencyValues(state.targetCurrency, value)
        }
    }

    fun onCurrencySelect(currency: Currency) {
        cancelPreviousRatesLoading()
        reducer.setTargetCurrency(currency.code)
        state.inputValue?.let {
            loadCurrencyValues(currency.code, it)
        }
    }

    fun onRetryClicked() {
        if (state.isCurrenciesLoadingError) {
            loadCurrencies()
        } else if (state.isRatesLoadingError) {
            state.inputValue?.let {
                loadCurrencyValues(state.targetCurrency, it)
            }
        }
    }

    private fun loadCurrencyValues(targetCurrency: String, inputValue: Double) {
        ratesLoadingJob = viewModelScope.launch(
            CoroutineExceptionHandler { coroutineContext, throwable ->
                reducer.setRatesError()
            }
        ) {
            reducer.setLoading(true)
            val currenciesDeferred = async {
                currenciesInteractor.getCurrencies()
            }
            val currencyRatesDeferred = async {
                converterInteractor.getCurrenciesRates(targetCurrency, inputValue)
            }
            val currencies = currenciesDeferred.await()
            val currencyRates = currencyRatesDeferred.await()
            val currencyValues = currencies
                .filter {
                    it.code != targetCurrency
                }
                .map {
                    CurrencyValue(it, currencyRates[it.code])
                }
            reducer.setCurrencyValues(currencyValues)
        }
    }

    private fun cancelPreviousRatesLoading() {
        ratesLoadingJob?.cancel()
        ratesLoadingJob = null
    }
}
