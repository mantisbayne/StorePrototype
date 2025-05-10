package com.mantisbayne.storeprototype.domain

import com.mantisbayne.storeprototype.data.Product
import com.mantisbayne.storeprototype.data.api.FakeProductApi
import com.mantisbayne.storeprototype.data.api.ProductApi
import com.mantisbayne.storeprototype.data.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface ProductRepository {
    fun getAllProducts(): Flow<ProductResult<List<Product>>>

    fun getProduct(id: Int): Flow<ProductResult<Product>>
}

class ProductRepositoryImpl @Inject constructor(private val api: FakeProductApi) : ProductRepository {

    override fun getAllProducts() = flow<ProductResult<List<Product>>> {
        val products = api.getAllProducts().map { product ->
            product.toDomain(
                id = product.id,
                title = product.title,
                price = product.price,
                description = product.description,
                category = product.category,
                image = product.image
            )
        }
        emit(ProductResult.Success(products))
    }.catch { e ->
        emit(ProductResult.Error(e.message))
    }

    override fun getProduct(id: Int) = flow<ProductResult<Product>> {
        val product = api.getProduct(id)
        val result = product.toDomain(
            id = product.id,
            title = product.title,
            price = product.price,
            description = product.description,
            category = product.category,
            image = product.image
        )
        emit(ProductResult.Success(result))
    }.catch { e ->
        emit(ProductResult.Error(e.message))
    }
}

sealed class ProductResult<out T> {
    data class Error(val errorMessage: String?) : ProductResult<Nothing>()
    data class Success<out T>(val value: T) : ProductResult<T>()
}
