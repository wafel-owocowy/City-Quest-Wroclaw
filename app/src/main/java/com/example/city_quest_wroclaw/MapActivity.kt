package com.example.city_quest_wroclaw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.city_quest_wroclaw.ui.theme.CityQuestWroclawTheme

class MapActivity : ComponentActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CityQuestWroclawTheme {

            }
        }
    }
}
