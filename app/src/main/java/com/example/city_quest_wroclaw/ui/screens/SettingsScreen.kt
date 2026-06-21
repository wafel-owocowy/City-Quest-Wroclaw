package com.example.city_quest_wroclaw.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.city_quest_wroclaw.R
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel
import java.util.Locale

@Composable
fun SettingsScreen(viewModel: CityQuestViewModel) {
    val isDarkModeState by viewModel.isDarkMode.collectAsState()
    val isDarkMode = isDarkModeState ?: isSystemInDarkTheme()
    val visitRadius by viewModel.visitRadiusMeters.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.settings_header),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            HorizontalDivider(
                modifier = Modifier.padding(bottom = 16.dp),
                thickness = DividerDefaults.Thickness,
                color = DividerDefaults.color
            )
            
            // Dark Mode Switch Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_dark_mode),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { isChecked ->
                        viewModel.setDarkMode(isChecked)
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_visit_radius),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                Column {Slider(
                    value = visitRadius,
                    onValueChange = { newVal -> viewModel.setRadiusMeters(newVal) },
                    steps = 18,
                    valueRange = 10f..200f
                )
                    Text(text = "$visitRadius m") }

            }
            
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            
            // Language Selection Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_language),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(Modifier.weight(1f))
                
                val currentLocaleCode = AppCompatDelegate.getApplicationLocales()[0]?.language ?: Locale.getDefault().language
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LanguageButton(
                        label = "EN",
                        isSelected = currentLocaleCode == "en",
                        onClick = {
                            viewModel.setLanguage("en")
                        }
                    )
                    LanguageButton(
                        label = "PL",
                        isSelected = currentLocaleCode == "pl",
                        onClick = {
                            viewModel.setLanguage("pl")
                        }
                    )
                }
            }
            
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }
    }
}

@Composable
fun LanguageButton(label: String, isSelected: Boolean, onClick: () -> Unit) {
    if (isSelected) {
        Button(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = label, fontWeight = FontWeight.Bold)
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = label)
        }
    }
}
