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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
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

    init {
        println("debugg init")
        processIntents()
        sendIntent(ProductIntent.Refresh)
    }

    fun sendIntent(intent: ProductIntent) {
        _intent.tryEmit(intent)
    }

    fun processIntents() {
        viewModelScope.launch {
            println("debugg process")
            intent.collectLatest {intent ->
                println("debugg $intent")
                when (intent) {
                    is ProductIntent.Refresh -> loadProducts()
                    is ProductIntent.AddProduct -> addProduct(intent.id)
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts()
                .collect { result ->
                    println("debugg result $result")
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

    private fun addProduct(id: Int) {

    }
}

data class ProductViewState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val errorMessage: String = ""
)
