package com.mantisbayne.storeprototype.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantisbayne.storeprototype.data.Product
import com.mantisbayne.storeprototype.domain.CartRepository
import com.mantisbayne.storeprototype.domain.ProductRepository
import com.mantisbayne.storeprototype.domain.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _intent = MutableSharedFlow<ProductIntent>(replay = 1, extraBufferCapacity = 1)
    val intent: SharedFlow<ProductIntent> = _intent.asSharedFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private val productResult = productRepository.getAllProducts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProductResult.Success(emptyList())
        )

    private val products: StateFlow<List<Product>> = productResult
        .map { result ->
            (result as? ProductResult.Success)?.value ?: emptyList()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val observeCart = cartRepository.observeCart()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val cart: StateFlow<List<CartItem>> = observeCart
        .combine(products) { cartMap, productList ->
            productList.filter { cartMap.containsKey(it.id) }
                .map { product ->
                    val count = cartMap[product.id] ?: 0
                    val subtotal = total(product, count)
                    CartItem(
                        productId = product.id,
                        productName = product.title,
                        unitPrice = "$${product.price}",
                        count = count,
                        subtotal = subtotalText(subtotal)
                    )
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private fun subtotalText(subtotal: Double) = "$%.2f".format(subtotal)

    val storeItems = observeCart
        .combine(products) { cartMap, productList ->
            productList.map { product ->
                val count = cartMap[product.id] ?: 0
                StoreItem(
                    id = product.id,
                    description = product.description,
                    title = product.title,
                    count = "$count",
                    price = "$${product.price}",
                    subtotal = subtotalText(total(product, count))
                )
            }
        }

    private val totalPrice: StateFlow<Double> = observeCart
        .combine(products) { cartMap, productList ->
            calculateTotal(productList, cartMap)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val viewState: StateFlow<ProductViewState> = combine(
        productResult,
        cart,
        totalPrice,
        storeItems
    ) { productResult, cartItems, totalPrice, storeItems ->

        when (productResult) {
            is ProductResult.Error -> {
                ProductViewState(
                    isLoading = false,
                    errorMessage = errorMessage(productResult.errorMessage)
                )
            }

            is ProductResult.Success -> {
                ProductViewState(
                    isLoading = false,
                    items = storeItems,
                    cartItems = cartItems,
                    totalPrice = NumberFormat.getCurrencyInstance().format(totalPrice)
                )

            }
        }
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProductViewState()
        )

    init {
        processIntents()
    }

    fun sendIntent(intent: ProductIntent) {
        _intent.tryEmit(intent)
    }

    fun processIntents() {
        viewModelScope.launch {
            intent.collectLatest { intent ->
                when (intent) {
                    is ProductIntent.UpdateProduct -> addProductToCart(intent.id, intent.shouldAdd)
                }
            }
        }
    }

    private fun addProductToCart(id: Int, shouldAdd: Boolean) {
        viewModelScope.launch {
            val currentCount = observeCart.value[id] ?: 0
            val newCount = if (shouldAdd) currentCount + 1 else currentCount - 1

            if (newCount < 1) {
                val productTitle = products.value.find { it.id == id }?.title ?: "Item"
                _uiEvent.emit(UiEvent.SnackbarEvent("$productTitle removed from cart"))
            }

            cartRepository.updateCartItemCount(id, newCount)
        }
    }

    private fun calculateTotal(items: List<Product>, cart: Map<Int, Int>): Double {
        return items.sumOf { item ->
            val count = cart[item.id] ?: 0
            total(item, count)
        }
    }

    private fun total(item: Product, count: Int) =
        item.price * count

    private fun errorMessage(errorMessage: String?) = errorMessage ?: "Unable to load Products"
}

data class ProductViewState(
    val items: List<StoreItem> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val totalPrice: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

data class CartItem(
    val productId: Int,
    val productName: String,
    val unitPrice: String,
    val count: Int,
    val subtotal: String
)

data class StoreItem(
    val id: Int = 0,
    val description: String = "",
    val title: String = "",
    val count: String = "0",
    val price: String = "",
    val subtotal: String = ""
)

sealed class UiEvent {
    data class SnackbarEvent(val message: String) : UiEvent()
}
