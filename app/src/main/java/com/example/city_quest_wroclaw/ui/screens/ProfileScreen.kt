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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                title = { Text("Mój Profil") },
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
                text = "Statystyki",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Odwiedzone miejsca:",
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
                    text = "🏆 Gratulacje! Zdobyto odznakę Mistrza Wrocławia!",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium
                )
            } else if (visitedCount > 0) {
                Text(
                    text = "Odkrywaj dalej, aby zdobyć główną odznakę!",
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Text(
                    text = "Rozpocznij swoją przygodę!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
