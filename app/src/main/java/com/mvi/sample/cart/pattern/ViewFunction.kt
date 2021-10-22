package com.mvi.sample.cart.pattern

import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mvi.sample.cart.CartActivity

/**
 * this is the view function, can be @Composable with Compose
 */
internal fun CartActivity.drawCartScreen(intents: MutableLiveData<CartIntents>, viewState: CartViewState) {
    cartCount.text = viewState.cart?.totalQuantity?.toString()
    cartTotalPrice.text = viewState.cart?.totalPrice?.toString()
    cartProgressBar.visibility = if (viewState.progress != null) View.VISIBLE else View.GONE
    itemsAdapter.submitList(viewState.itemsViewData)
    if (viewState.error != null) {
        Toast.makeText(this, viewState.error.toString(), Toast.LENGTH_LONG).show()
        intents.value = CartIntents.ErrorDisplayed(viewState)
    }
}