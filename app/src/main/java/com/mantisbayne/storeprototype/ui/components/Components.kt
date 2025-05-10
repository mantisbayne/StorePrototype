package com.mantisbayne.storeprototype.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.w3c.dom.Text

@Composable
fun MediumDivider() {
    HorizontalDivider(
        thickness = 2.dp,
        modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
    )
}

@Composable
fun BodyText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        text = text
    )
}
