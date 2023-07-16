package com.advice.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.Speaker
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.components.SearchableTopAppBar
import com.advice.ui.components.SpeakerView
import com.advice.ui.R
import com.advice.ui.components.EmptyView
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.preview.SpeakerProvider

@Composable
fun SpeakersScreenView(
    speakers: List<Speaker>?,
    onBackPressed: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit
) {
    Scaffold(topBar = {
        SearchableTopAppBar(title = { Text("Speakers") }, navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
            }
        }) { query ->

        }
    }) {
        Box(Modifier.padding(it)) {
            when {
                speakers == null -> {
                    ProgressSpinner()
                }

                speakers.isEmpty() -> {
                    EmptyView("Speakers not found")
                }

                else -> {
                    SpeakersScreenContent(speakers, onSpeakerClicked)
                }
            }
        }
    }
}

@Composable
fun SpeakersScreenContent(
    speakers: List<Speaker>?,
    onSpeakerClicked: (Speaker) -> Unit
) {
    LazyColumn() {
        if (speakers != null) {
            items(speakers) {
                SpeakerView(it.name, title = it.title) {
                    onSpeakerClicked(it)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakersScreenViewPreview(@PreviewParameter(SpeakerProvider::class) speaker: Speaker) {
    ScheduleTheme {
        SpeakersScreenView(listOf(speaker), {}, {})
    }
}