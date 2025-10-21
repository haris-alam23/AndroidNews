package com.example.androidnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidnews.ui.theme.AndroidNewsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val searchTerm: String = intent.getStringExtra("search_term").toString()
        val sourceId = intent.getStringExtra("source_id").toString()
        val sourceName = intent.getStringExtra("source_name").toString()
        setContent {
            AndroidNewsTheme {
                ShowResults(searchTerm,sourceId,sourceName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowResults(searchTerm: String, sourceId: String, sourceName: String?) {

    val context = LocalContext.current
    val apiKey = BuildConfig.News_API_Key
    var articleList by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    val manager = remember { NewsManager() }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(searchTerm, sourceId) {
        isLoading = true
        articleList = withContext(Dispatchers.IO) {
            manager.GetEverything(searchTerm, sourceId, apiKey)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (!sourceName.isNullOrEmpty() && sourceName != "null") {
                            "$sourceName results for \"$searchTerm\""
                        } else {
                            "Results for \"$searchTerm\""
                        }
                    )
                }
            )
        }
    ) { padding ->
2
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    LinearProgressIndicator()
                }
            }

            articleList.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No articles found for \"$searchTerm\"",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                ) {
                    items(articleList) { article ->
                        ArticleCard(article)
                    }
                }
            }
        }
    }
    }

