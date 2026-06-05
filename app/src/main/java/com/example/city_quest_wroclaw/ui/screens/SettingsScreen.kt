package com.example.city_quest_wroclaw.ui.screens

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.city_quest_wroclaw.R
import com.example.city_quest_wroclaw.ui.theme.CityQuestWroclawTheme

@Composable
fun SettingsScreen() {
    var isDarkTheme = false
    fun onCreate()
    {

    }
    fun setDarkMode(isChecked: Boolean){
        AppCompatDelegate.setDefaultNightMode(
            if (isChecked){
                AppCompatDelegate.MODE_NIGHT_YES

            }
            else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        isDarkTheme = !isDarkTheme
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        contentAlignment = Alignment.TopStart

    ) {Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(text = stringResource(R.string.settings_header),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge)
        HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
        Row (modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.settings_dark_mode), )

            Spacer(Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = {isChecked->setDarkMode(isChecked)})
        }
    }

    }

}
@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    CityQuestWroclawTheme {
        SettingsScreen()
    }
}
