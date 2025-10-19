package com.example.androidnews

data class Source(
    val name: String,
    val category: String,
    val description: String
)

val fakeSources = listOf(
    Source("The Verge", "Technology", "Tech news and product reviews"),
    Source("Bloomberg", "Business", "Financial, business, and market news"),
    Source("ESPN", "Sports", "Sports updates, scores, and analysis"),
    Source("BBC News", "General", "International and local news coverage"),
    Source("National Geographic", "Science", "Science and environmental insights"),
    Source("Reuters", "General", "Breaking international and business news"),
    Source("IGN", "Entertainment", "Gaming, movies, and pop culture content"),
    Source("CNBC", "Finance", "Market and economic updates"),
    Source("Al Jazeera", "General", "Global news and documentaries"),
    Source("Wired", "Technology", "Technology, culture, and innovation reporting")
)