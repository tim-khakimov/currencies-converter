package com.timkhakimov.currenciesconverter.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timkhakimov.currenciesconverter.App
import com.timkhakimov.currenciesconverter.R
import com.timkhakimov.currenciesconverter.data.persistence.model.Currency
import com.timkhakimov.currenciesconverter.databinding.FragmentConverterBinding
import com.timkhakimov.currenciesconverter.presentation.ConverterViewModel
import com.timkhakimov.currenciesconverter.presentation.ConverterViewModelFactory
import com.timkhakimov.currenciesconverter.presentation.model.UiState
import com.timkhakimov.currenciesconverter.ui.adapter.CurrencyValuesAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ConverterFragment : Fragment() {

    private var _binding: FragmentConverterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ConverterViewModel by viewModels(
        factoryProducer = {
            ConverterViewModelFactory(
                (requireActivity().applicationContext as App).database
            )
        }
    )

    private val currencyValuesAdapter = CurrencyValuesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConverterBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        setListeners()
        observeData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initList() = with(binding.currencyValuesRecyclerView) {
        adapter = currencyValuesAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { setUiState(it) }
        }
        lifecycleScope.launch {
            viewModel.currenciesSelectionEvent
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest { showCurrenciesSelectionDialog(it) }
        }
    }

    private fun setUiState(uiState: UiState) = with(binding) {
        targetCurrencyTextView.text = uiState.targetCurrency
        when (uiState) {
            is UiState.Initial -> {
                errorGroup.isVisible = false
                progressBar.isVisible = false
            }
            is UiState.Loading -> {
                errorGroup.isVisible = false
                progressBar.isVisible = true
            }
            is UiState.Error -> {
                errorGroup.isVisible = true
                progressBar.isVisible = false
            }
            is UiState.ContentLoaded -> {
                errorGroup.isVisible = false
                progressBar.isVisible = false
                currencyValuesAdapter.setItems(uiState.currencyValues)
            }
        }
    }

    private fun setListeners() = with(binding) {
        inputValueEditText.doAfterTextChanged {
            viewModel.onTextInput(it.toString())
        }
        targetCurrencyTextView.setOnClickListener {
            viewModel.onTargetCurrencyClicked()
        }
        retryButton.setOnClickListener {
            viewModel.onRetryClicked()
        }
    }

    private fun showCurrenciesSelectionDialog(currencies: List<Currency>) {
        AlertDialog.Builder(requireContext())
            .apply {
                setTitle(R.string.select_currency)
                setItems(
                    currencies.map { it.name }.toTypedArray()
                ) { _, which ->
                    viewModel.onCurrencySelect(currencies[which])
                }
            }.show()
    }
}
