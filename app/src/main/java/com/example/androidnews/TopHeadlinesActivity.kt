@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.androidnews

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.androidnews.ui.theme.AndroidNewsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import coil.compose.AsyncImage



class TopHeadlinesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidNewsTheme {
                    TopHeadlineSearch()
                }
            }
        }
    }




@Composable
fun TopHeadlineSearch() {

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("my_prefs", Context.MODE_PRIVATE) }
    var expanded by remember { mutableStateOf(false) }
    val categories =
        listOf("Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology")
    var categorySelect by remember { mutableStateOf(prefs.getString("selected_category","")?: "") }
    var currentPage by remember { mutableIntStateOf(1) }
    var maxPages by remember { mutableIntStateOf(1) } // 100 results / 20 results per page



    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Top Headlines")})
        }

    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Dropdown for categories
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categorySelect,
                    onValueChange = {},
                    label = { Text("Category") },
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .clickable {true}
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                categorySelect = category
                                expanded = false
                                prefs.edit().putString("selected_category", category).apply()
                                currentPage = 1
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (categorySelect.isNotEmpty()) {
                DisplayHeadlines(
                    category = categorySelect,
                    page = currentPage,
                    modifier = Modifier.weight(1f),
                    {newMax -> maxPages = newMax }
                )
            } else {
                Text(
                    text = "Please select a category to load headlines",
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                Button(
                    onClick = {if (currentPage > 1) currentPage--},
                    enabled = currentPage > 1
                ) {
                    Text("Back")
                }

                Text("$currentPage of $maxPages")

                Button(
                    onClick = {if(currentPage < maxPages) currentPage++},
                    enabled = currentPage < maxPages
                ) {
                    Text("Next")
                    }
                }
            }
        }
    }


@Composable
fun DisplayHeadlines(
    category: String,
    page: Int = 1,
    modifier: Modifier = Modifier,
    MaxPageUpdate: (Int) -> Unit
) {
    val context = LocalContext.current
    val apiKey = BuildConfig.News_API_Key
    var articleList by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    val manager = remember { NewsManager() }


    LaunchedEffect(category, page) {
        val (result,totalPages) = withContext(Dispatchers.IO) {
            manager.GetTopHeadlines(category,page,apiKey)
        }
        articleList = result
        MaxPageUpdate(totalPages)
    }

    LazyColumn(modifier = modifier) {
        //items should be iterating on items not count)
        items(items = articleList){article: NewsArticle->
            ArticleCard(
                article=article,
                modifier=Modifier.padding(1.dp)
                )
            }
        }
    }

@Composable
fun ArticleCard(article: NewsArticle, modifier:Modifier=Modifier){
    val context = LocalContext.current
    Card(modifier=Modifier.fillMaxWidth()
        .padding(6.dp)
        .clickable(onClick = {
            val yelpCardIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(article.url)
            }

            context.startActivity(yelpCardIntent)
        }),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp),
        ){
        Row(modifier=Modifier.padding(2.dp)) {
            AsyncImage(
                model = article.urlImage,
                //painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = article.title,
                modifier = Modifier
                    .size(100.dp)
                    .padding( 10.dp)
            )
            Spacer(modifier=Modifier.width(6.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()

            ) {
                Text(
                    article.title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                        )
                Spacer(modifier=Modifier.width(6.dp))
                Text(
                    article.source,
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall
                )
                Spacer(modifier=Modifier.width(6.dp))
                Text(article.content ?: "No description is available")
            }

        }
    }
}






