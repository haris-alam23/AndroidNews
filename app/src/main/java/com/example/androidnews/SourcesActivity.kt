package com.example.androidnews

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidnews.ui.theme.AndroidNewsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SourcesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val searchTerm = intent.getStringExtra("search_term") ?: ""

        enableEdgeToEdge()
        setContent {
            AndroidNewsTheme {
                SourcesScreen(searchTerm)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SourcesScreen(searchTerm: String) {


    val categories = listOf("Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }
    val apiKey = BuildConfig.News_API_Key
    val manager = remember { NewsManager() }
    var sources by remember { mutableStateOf<List< Source>>(emptyList()) }
    var isLoading by  remember { mutableStateOf(false) }
    val context = LocalContext.current
    var error by remember {mutableStateOf<String?>(null)}

    LaunchedEffect(selectedCategory) {
        isLoading = true
        error = null
        try {

         sources = withContext(Dispatchers.IO) {
             manager.GetSources(selectedCategory, apiKey)
         }
            if (sources.isEmpty()) {
                error = "No sources found for $selectedCategory."
            }
        } catch (e: Exception) {
            e.printStackTrace()
            error = "Failed to load sources."
        } finally {
            isLoading = false
        }

    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = stringResource(R.string.news_sources)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Search for: \"$searchTerm\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(selectedCategory)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val intent = Intent(context, ResultsActivity::class.java).apply {
                        putExtra("search_term", searchTerm)
                    }
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.available_sources),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        LinearProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }

                else -> {
                    LazyColumn {
                        items(sources) { source ->
                            SourceItem(source, searchTerm = searchTerm)
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun SourceItem(source: Source, searchTerm: String) {
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable{
            val intent = Intent(context, ResultsActivity::class.java).apply {
                putExtra("source_id", source.id)
                putExtra("source_name", source.name)
                putExtra("search_term", searchTerm)
            }
            context.startActivity(intent)
        }

    ) {

        Text(text = source.name, fontWeight = FontWeight.Bold)
        Text(text = source.category)
        Text(text = source.description)
        Spacer(modifier = Modifier.height(8.dp))
    }
}