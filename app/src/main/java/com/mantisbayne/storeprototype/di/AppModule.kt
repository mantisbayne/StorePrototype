package com.mantisbayne.storeprototype.di

import android.content.Context
import androidx.room.Room
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mantisbayne.storeprototype.data.api.CartApi
import com.mantisbayne.storeprototype.data.api.FakeProductApi
import com.mantisbayne.storeprototype.data.api.ProductApi
import com.mantisbayne.storeprototype.data.api.UserApi
import com.mantisbayne.storeprototype.data.local.CartDao
import com.mantisbayne.storeprototype.data.local.Database
import com.mantisbayne.storeprototype.domain.ProductRepository
import com.mantisbayne.storeprototype.domain.ProductRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://fakestoreapi.com/")
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }
//
//    @Provides
//    @Singleton
//    fun provideProductApi(retrofit: Retrofit): ProductApi =
//        retrofit.create(ProductApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(): ProductApi {
        return FakeProductApi()
    }

    @Provides
    @Singleton
    fun provideCartApi(retrofit: Retrofit): CartApi =
        retrofit.create(CartApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi =
        retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideProductRepository(api: FakeProductApi): ProductRepository {
        return ProductRepositoryImpl(api)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object DatabaseModule {

        @Singleton
        @Provides
        fun provideDataBase(@ApplicationContext context: Context): Database {
            return Room.databaseBuilder(
                context.applicationContext,
                Database::class.java,
                "Cart.db"
            ).build()
        }

        @Provides
        fun provideCartDao(database: Database): CartDao = database.cartDao()
    }


    @Provides
    @Singleton
    fun provideCartRepository(api: FakeProductApi): ProductRepository {
        return ProductRepositoryImpl(api)
    }
}