package com.example.androidnews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.androidnews.ui.theme.AndroidNewsTheme
import com.example.androidnews.fakeSources
import com.example.androidnews.Source

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


    val categories = listOf("Business", "Technology", "Sports", "Health", "Entertainment", "Science")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Select News Sources") })
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

            // Skip Button
            Button(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Skip")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Available Sources",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(fakeSources) { source ->
                    SourceItem(source)
                }
            }


        }
    }

}

@Composable
fun SourceItem(source: Source) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Text(text = source.name, fontWeight = FontWeight.Bold)
        Text(text = source.category)
        Text(text = source.description)
        Spacer(modifier = Modifier.height(8.dp))
    }
}