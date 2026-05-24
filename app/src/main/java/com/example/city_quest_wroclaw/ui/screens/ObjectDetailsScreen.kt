package com.example.city_quest_wroclaw.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.city_quest_wroclaw.R
import com.example.city_quest_wroclaw.viewmodel.CityQuestViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjectDetailsScreen(
    attractionId: Int,
    viewModel: CityQuestViewModel,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val attractions by viewModel.attractions.collectAsState()
    val attraction = attractions.find { it.id == attractionId }

    if (attraction == null) {
        Text(stringResource(R.string.loading))
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(attraction.name) },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            val imageName = attraction.name.lowercase().replace(" ", "_").replace("ł", "l").replace("ó", "o").replace("ś", "s").replace("ź", "z").replace("ż", "z").replace("ń", "n").replace("ć", "c").replace("ą", "a").replace("ę", "e")
            val imageResId = remember(imageName) {
                val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
                if (resId != 0) resId else R.drawable.attraction_1 // fallback to generic placeholder
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = attraction.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = attraction.description,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (!attraction.isVisited) {
                Text(
                    text = stringResource(R.string.attraction_unvisited),
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = stringResource(R.string.attraction_visited),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                if (attraction.hasVideo) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.promo_vid), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    VideoPlayer(context, R.raw.promo_video)
                }

                if (attraction.hasAudio) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.audio_guide), style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    AudioPlayer(context, R.raw.guide_audio)
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(context: Context, videoRes: Int) {
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/$videoRes")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}

@Composable
fun AudioPlayer(context: Context, audioRes: Int) {
    // Similar to VideoPlayer but minimal UI or a custom play button could be used.
    // For simplicity, using PlayerView which provides basic controls.
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri("android.resource://${context.packageName}/$audioRes")
            setMediaItem(mediaItem)
            prepare()
        }
    }

    DisposableEffect(
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                }
            }
        )
    ) {
        onDispose {
            exoPlayer.release()
        }
    }
}
