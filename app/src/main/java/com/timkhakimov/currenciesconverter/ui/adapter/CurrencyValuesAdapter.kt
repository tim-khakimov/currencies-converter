package com.timkhakimov.currenciesconverter.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.timkhakimov.currenciesconverter.presentation.model.CurrencyValue

class CurrencyValuesAdapter : RecyclerView.Adapter<CurrencyValueViewHolder>() {

    private val differ = AsyncListDiffer(this, CurrencyValuesDiffCallback())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CurrencyValueViewHolder.create(parent)

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(
        holder: CurrencyValueViewHolder,
        position: Int
    ) = holder.bind(differ.currentList[position])

    fun setItems(items: List<CurrencyValue>) {
        differ.submitList(items)
    }

    private class CurrencyValuesDiffCallback : DiffUtil.ItemCallback<CurrencyValue>() {

        override fun areItemsTheSame(
            oldItem: CurrencyValue,
            newItem: CurrencyValue
        ) = oldItem.currency.code == newItem.currency.code

        override fun areContentsTheSame(
            oldItem: CurrencyValue,
            newItem: CurrencyValue
        ) = oldItem == newItem
    }
}