package com.mantisbayne.storeprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mantisbayne.storeprototype.ui.theme.StorePrototypeTheme
import com.mantisbayne.storeprototype.viewmodel.ProductViewModel
import com.mantisbayne.storeprototype.viewmodel.ProductViewState
import com.mantisbayne.storeprototype.viewmodel.StoreItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StorePrototypeTheme {
                val scope = rememberCoroutineScope()
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    val viewModel = hiltViewModel<ProductViewModel>()
                    val viewState by viewModel.viewState.collectAsStateWithLifecycle()

                    when {
                        viewState.isLoading -> CircularProgressIndicator()
                        viewState.errorMessage.isNotBlank() -> Text("error")
                        else -> ProductScreen(innerPadding, viewState)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductScreen(
    innerPadding: PaddingValues,
    viewState: ProductViewState
) {
    Surface(
        Modifier
            .padding(innerPadding)
            .fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(viewState.items) { item ->
                Card(
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleLarge
                        )
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "add item")
                        }
                        Text(
                            text = item.count
                        )
                        IconButton(
                            onClick = {

                            }
                        ) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                painter = painterResource(R.drawable.minus_icon),
                                contentDescription = "remove item"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StorePrototypeTheme {
        ProductScreen(
            innerPadding = PaddingValues(0.dp),
            viewState = ProductViewState(
                items = listOf(
                    StoreItem("Cookies"),
                    StoreItem("Milk"),
                    StoreItem("Cheese"),
                    StoreItem("Apples")
                )
            )
        )
    }
}