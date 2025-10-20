package com.example.androidnews


 import androidx.compose.ui.input.key.Key
 import okhttp3.OkHttpClient
 import okhttp3.Request
 import okhttp3.Response
 import okhttp3.logging.HttpLoggingInterceptor
 import org.json.JSONObject



class NewsManager {

    val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        okHttpClient = builder.build()
    }

    suspend fun GetTopHeadlines(
        category: String,
        pageNumber: Int = 1,
        apiKey: String
    ): List<NewsArticle> {

        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines?country=us&category=$category&page=$pageNumber&apiKey=$apiKey")
            .header("authorization", "Bearer $apiKey")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val articleList = mutableListOf<NewsArticle>()
            val json = JSONObject(responseBody)
            val articles = json.getJSONArray("articles")
            for (i in 0 until articles.length()) {
                val currentArticle = articles.getJSONObject(i)
                val title = currentArticle.getString("title")
                val source = currentArticle.getString("source")
                val content = currentArticle.getString("description")
                val urlImage = currentArticle.getString("urlToImage")
                val urlArticle = currentArticle.getString("url")

                val article = NewsArticle(
                    title = title,
                    source = source,
                    content = content,
                    urlImage = urlImage,
                    url = urlArticle
                )
                articleList.add(article)
            }

            return articleList
        } else (
                return listOf()
                )


    }
}