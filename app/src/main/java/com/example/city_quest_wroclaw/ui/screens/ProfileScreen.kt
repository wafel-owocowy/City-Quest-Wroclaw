package com.example.city_quest_wroclaw.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.city_quest_wroclaw.R
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: CityQuestViewModel,
    onBackClick: () -> Unit
) {
    val attractions by viewModel.attractions.collectAsState()
    val visitedCount = attractions.count { it.isVisited }
    val totalCount = attractions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_screen_header)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.profile_screen_statistics),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = stringResource(R.string.profile_places_visited),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "$visitedCount / $totalCount",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))
            if (visitedCount == totalCount && totalCount > 0) {
                Text(
                    text = stringResource(R.string.profile_visited_all),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )
            } else if (visitedCount > 0) {
                Text(
                    text = stringResource(R.string.profile_visited_some),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = stringResource(R.string.profile_visited_none),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
