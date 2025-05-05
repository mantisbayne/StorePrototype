package com.mantisbayne.storeprototype.domain

import com.mantisbayne.storeprototype.data.Product
import com.mantisbayne.storeprototype.data.api.ProductApi
import com.mantisbayne.storeprototype.data.toDomain
import com.mantisbayne.storeprototype.domain.ProductRepositoryImpl.ProductResult
import javax.inject.Inject

interface ProductRepository {
    suspend fun getAllProducts() : ProductResult<List<Product>>

    suspend fun getProduct(id: Int) : ProductResult<Product>
}

class ProductRepositoryImpl @Inject constructor(private val api: ProductApi): ProductRepository {

    override suspend fun getAllProducts(): ProductResult<List<Product>> {

        return try {
            val products = api.getAllProducts().map { product ->
                product.toDomain(
                    product.id,
                    product.title,
                    product.price,
                    product.image,
                    product.description,
                    product.category
                )
            }
            ProductResult.Success(products)
        } catch (e: Exception) {
            ProductResult.Error(e.message)
        }
    }

    override suspend fun getProduct(id: Int): ProductResult<Product> {

        return try {
            val product = api.getProduct(id)
            val result = product.toDomain(
                product.id,
                product.title,
                product.price,
                product.image,
                product.description,
                product.category
            )
            ProductResult.Success(result)
        } catch (e: Exception) {
            ProductResult.Error(e.message)
        }
    }

    sealed class ProductResult<T> {
        data class Error<T>(val errorMessage: String?) : ProductResult<T>()
        data class Success<T>(val value: T) : ProductResult<T>()
    }
}