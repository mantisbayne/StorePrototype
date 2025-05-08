package com.mantisbayne.storeprototype.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mantisbayne.storeprototype.data.Product
import com.mantisbayne.storeprototype.domain.CartRepository
import com.mantisbayne.storeprototype.domain.ProductRepository
import com.mantisbayne.storeprototype.domain.ProductResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val repository: ProductRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(ProductViewState())
    val viewState: StateFlow<ProductViewState> = _viewState.asStateFlow()

    private val _intent = MutableSharedFlow<ProductIntent>(replay = 1, extraBufferCapacity = 1)
    val intent: SharedFlow<ProductIntent> = _intent.asSharedFlow()

    val cart: StateFlow<Map<Int, Int>> = cartRepository.observeCart()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val totalPrice: StateFlow<Double> = cart
        .combine(viewState.map { it.products }) { cartMap, products ->
            calculateTotal(products, cartMap)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0.0)

    init {
        processIntents()
        sendIntent(ProductIntent.Refresh)
    }

    fun sendIntent(intent: ProductIntent) {
        _intent.tryEmit(intent)
    }

    fun processIntents() {
        viewModelScope.launch {
            intent.collectLatest {intent ->
                when (intent) {
                    is ProductIntent.Refresh -> loadProducts()
                    is ProductIntent.AddProduct -> addProductToCart(intent.id)
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .collect { result ->

                    when (result) {
                        is ProductResult.Success -> {
                            _viewState.update {
                                it.copy(
                                    isLoading = false,
                                    products = result.value
                                )
                            }
                        }
                        is ProductResult.Error -> {
                            _viewState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun addProductToCart(id: Int) {
        viewModelScope.launch {
            cartRepository.
        }
    }

    private fun calculateTotal(items: List<Product>, cart: Map<Int, Int>): Double {
        return items.sumOf { item ->
            val count = cart[item.id] ?: 0
            item.price * count
        }
    }
}

data class ProductViewState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val errorMessage: String = ""
)
