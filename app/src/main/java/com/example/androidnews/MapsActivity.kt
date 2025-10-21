package com.example.androidnews

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.Transparent) {
                MapsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen() {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("map_prefs", android.content.Context.MODE_PRIVATE)
    val apiKey = BuildConfig.News_API_Key
    val manager = remember { NewsManager() }

    var marker by remember { mutableStateOf<LatLng?>(null) }
    var addressInfo by remember { mutableStateOf("Long-press to select a location") }
    var articles by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val cameraState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(38.9072, -77.0369), 5f)
    }

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(marker) {
        marker?.let { latLng ->
            error = null
            addressInfo = "Resolving address..."

            try {
                val resolvedAddress = withContext(Dispatchers.IO) {
                    getAddressGeocodeCurrent(context, latLng)
                }
                addressInfo = resolvedAddress

                isLoading = true
                articles = withContext(Dispatchers.IO) {
                    val cleanTerm = resolvedAddress.substringBefore(",").trim()
                    manager.GetEverythingTitle(cleanTerm, apiKey)
                }
                if(articles.isEmpty()) {
                    error = "No local news found for $resolvedAddress"
                }
                prefs.edit()
                    .putFloat("lat", latLng.latitude.toFloat())
                    .putFloat("lng", latLng.longitude.toFloat())
                    .apply()
            } catch (e: Exception) {
                e.printStackTrace()
                error = "Failed to load local news."

            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.local_news_map)) })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraState,
                onMapLongClick = { latLng ->
                    marker = latLng
                    coroutineScope.launch {
                        cameraState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 6f))
                    }
                }
            ) {
                marker?.let {
                    Marker(
                        state = MarkerState(it),
                        title = addressInfo,
                        snippet = "Lat: ${it.latitude}, Lng: ${it.longitude}"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = addressInfo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                when {
                    isLoading -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            LinearProgressIndicator()
                        }
                    }

                    error != null -> {
                        Text(
                            text = error ?: "Unknown error",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    articles.isEmpty() && addressInfo != "Long-press to select a location" -> {
                        Text("No results found.", color = Color.Gray)
                    }
                    articles.isNotEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp)
                        ) {
                            items(articles) { article ->
                                ArticleCard(article)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

suspend fun getAddressGeocodeCurrent(context: android.content.Context, latLng: LatLng): String =
    suspendCoroutine { continuation ->
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(
            latLng.latitude,
            latLng.longitude,
            1,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addressList: MutableList<Address>) {
                    val result = if (addressList.isNotEmpty()) {
                        val address = addressList[0]
                        val city = address.locality ?: ""
                        val state = address.adminArea ?: ""
                        when {
                            city.isNotBlank() && state.isNotBlank() -> "$city, $state"
                            city.isNotBlank() -> city
                            state.isNotBlank() -> state
                            else -> "Unknown location"
                        }
                    } else "No address found."
                    continuation.resume(result)
                }

                override fun onError(errorMessage: String?) {
                    continuation.resume("Geocoding failed: ${errorMessage ?: "Unknown error"}")
                }
            }
        )
    }