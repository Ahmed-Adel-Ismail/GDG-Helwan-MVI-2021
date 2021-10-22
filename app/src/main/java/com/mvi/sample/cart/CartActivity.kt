package com.mvi.sample.cart

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.mvi.sample.R
import com.mvi.sample.cart.pattern.drawCartScreen

class CartActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProvider(this)[CartViewModel::class.java] }

    val cartCount: TextView by lazy { TODO() }
    val cartTotalPrice: TextView by lazy { TODO() }
    val cartProgressBar: ProgressBar by lazy { TODO() }
    val itemsAdapter: CartItemsAdapter by lazy {
        val adapter = CartItemsAdapter(viewModel.intents)
        val itemsRecyclerView = RecyclerView(this) // TODO inflate from xml
        itemsRecyclerView.adapter = itemsAdapter
        adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel.viewStates.observe(this) { drawCartScreen(viewModel.intents, it) }
    }


}