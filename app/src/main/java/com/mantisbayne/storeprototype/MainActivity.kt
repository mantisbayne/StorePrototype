package com.mantisbayne.storeprototype

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mantisbayne.storeprototype.ui.components.BodyText
import com.mantisbayne.storeprototype.ui.components.MediumDivider
import com.mantisbayne.storeprototype.ui.theme.StorePrototypeTheme
import com.mantisbayne.storeprototype.viewmodel.ProductIntent
import com.mantisbayne.storeprototype.viewmodel.ProductViewModel
import com.mantisbayne.storeprototype.viewmodel.ProductViewState
import com.mantisbayne.storeprototype.viewmodel.StoreItem
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.exp

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
                        else -> ProductScreen(
                            innerPadding,
                            viewState,
                            {
                                viewModel.sendIntent(
                                    ProductIntent.UpdateProduct(
                                        it, true
                                    )
                                )
                            },
                            {
                                viewModel.sendIntent(
                                    ProductIntent.UpdateProduct(
                                        it, false
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductScreen(
    innerPadding: PaddingValues,
    viewState: ProductViewState,
    onAdd: (Int) -> Unit,
    onSubtract: (Int) -> Unit
) {
    Surface(
        Modifier
            .padding(innerPadding)
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ProductList(viewState, onAdd, onSubtract)
    }
}

@Composable
private fun ProductList(
    viewState: ProductViewState,
    onAdd: (Int) -> Unit,
    onSubtract: (Int) -> Unit
) {

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(viewState.items) { item ->
            ProductItemContent(
                item,
                viewState.totalPrice,
                onAdd,
                onSubtract
            )
        }
    }
}

@Composable
private fun ProductItemContent(
    item: StoreItem,
    total: String,
    onAdd: (Int) -> Unit,
    onSubtract: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                expanded = !expanded
            }
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge
            )
            ProductQuantityCounter(
                { onAdd(item.id) },
                { onSubtract(item.id) },
                item.count
            )
        }
        AnimatedVisibility(
            visible = expanded
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                BodyText(item.description)
                MediumDivider()
                Row {
                    BodyText("Price:", Modifier.weight(1f))
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = item.price
                    )
                }
                MediumDivider()
                BodyText(item.count)
                MediumDivider()
                Row {
                    BodyText("Total:", Modifier.weight(1f))
                    BodyText(total)
                }
            }
        }
    }
}

@Composable
fun ProductQuantityCounter(
    onAdd: () -> Unit,
    onSubtract: () -> Unit,
    count: String
) {
    IconButton(
        onClick = {
            onAdd()
        }
    ) {
        Icon(Icons.Default.Add, contentDescription = "add item")
    }
    AnimatedContent(
        targetState = count,
        label = "Fade Animation"
    ) { count ->
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = count
        )
    }
    IconButton(
        onClick = {
            onSubtract()
        }
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(R.drawable.minus_icon),
            contentDescription = "remove item"
        )
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
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    ),
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    ),
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    ),
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    ),
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    ),
                    StoreItem(
                        id = 1,
                        description = "These cookies are delicious",
                        title = "Cookies",
                        count = "5",
                        price = "$5.00"
                    )
                )
            ),
            onAdd = {},
            onSubtract = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreviewExpanded() {
    val item = StoreItem(
        id = 1,
        description = "These cookies are delicious",
        title = "Cookies",
        count = "5",
        price = "$5.00"
    )
    StorePrototypeTheme {
        Card(
            modifier = Modifier
                .clickable {

                }
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(10.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleLarge
                    )
                    ProductQuantityCounter(
                        { },
                        { },
                        item.count
                    )
                }
                AnimatedVisibility(
                    visible = true
                ) {
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = item.title
                    )
                }
            }
        }
    }
}