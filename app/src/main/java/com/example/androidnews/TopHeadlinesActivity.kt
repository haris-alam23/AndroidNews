@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.androidnews

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.example.androidnews.ui.theme.AndroidNewsTheme

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
    val maxPages by remember { mutableIntStateOf(5) } // 100 results / 20 results per page



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

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {

                }
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


@Composable
fun getTopHeadlines() {











}





