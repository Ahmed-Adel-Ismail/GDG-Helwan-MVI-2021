package com.mvi.sample.cart

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mvi.core.entities.Item
import com.mvi.sample.cart.pattern.CartIntents

class CartItemsAdapter(private val intents: MutableLiveData<CartIntents>) :
    ListAdapter<ItemViewData, CartItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CartItemViewHolder(parent, intents)

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) =
        holder.bind(currentList[position])

    override fun onViewRecycled(holder: CartItemViewHolder) = holder.unbind()
}

class ItemDiffCallback : DiffUtil.ItemCallback<ItemViewData>() {

    override fun areItemsTheSame(oldItem: ItemViewData, newItem: ItemViewData) =
        oldItem.item.id == newItem.item.id

    override fun areContentsTheSame(oldItem: ItemViewData, newItem: ItemViewData) =
        oldItem.item == newItem.item
}

/**
 * TODO inflate the xml file with itemView context into super class constructor
 */
class CartItemViewHolder(itemView: View, private val intents: MutableLiveData<CartIntents>) :
    RecyclerView.ViewHolder(TODO()) {

    private val name: TextView by lazy { TODO() }
    private val description: TextView by lazy { TODO() }
    private val price: TextView by lazy { TODO() }
    private val quantity: TextView by lazy { TODO() }
    private val plusButton: View by lazy { TODO() }
    private val minusButton: View by lazy { TODO() }

    fun bind(itemData: ItemViewData) {

        name.text = itemData.item.name
        description.text = itemData.item.description
        price.text = itemData.item.price?.toString()
        quantity.text = itemData.quantity.toString()

        val viewState = intents.value?.viewState ?: return

        plusButton.setOnClickListener {
            intents.value = CartIntents.AddItem(viewState, itemData.item)
        }

        minusButton.setOnClickListener {
            intents.value = CartIntents.RemoveItem(viewState, itemData.item)
        }
    }

    fun unbind() {
        name.text = null
        description.text = null
        price.text = null
        quantity.text = null
        plusButton.setOnClickListener(null)
        minusButton.setOnClickListener(null)
    }
}

data class ItemViewData(val item: Item, val quantity: Int)