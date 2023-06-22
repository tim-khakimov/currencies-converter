package com.timkhakimov.currenciesconverter.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timkhakimov.currenciesconverter.R
import com.timkhakimov.currenciesconverter.databinding.ItemCurrencyValueBinding
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue

class CurrencyValueViewHolder(
    private val binding: ItemCurrencyValueBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(currencyValue: CurrencyValue) = with(binding) {
        currencyNameTextView.text = currencyValue.currency.name
        currencyValue.rate?.let {
            currencyValueTextView.text = it.toString()
        } ?: run {
            currencyValueTextView.setText(R.string.no_data)
        }
    }

    companion object {
        fun create(parent: ViewGroup) =
            CurrencyValueViewHolder(
                ItemCurrencyValueBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
    }
}