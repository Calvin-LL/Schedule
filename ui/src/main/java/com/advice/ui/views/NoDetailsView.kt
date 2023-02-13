package com.advice.ui.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun NoDetailsView() {
    Text("No further information available.\nMaybe ask Chatgpt.", modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(), textAlign = TextAlign.Center)
}

@Preview(showBackground = true)
@Composable
fun NoDetailsViewPreview() {
    MaterialTheme {
        NoDetailsView()
    }
}