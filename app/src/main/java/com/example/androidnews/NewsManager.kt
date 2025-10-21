package com.example.androidnews


 import okhttp3.OkHttpClient
 import okhttp3.Request
 import okhttp3.Response
 import okhttp3.logging.HttpLoggingInterceptor
 import org.json.JSONObject
 import kotlin.math.ceil


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
    ): Pair<List<NewsArticle>, Int> {

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
            var totalPages = 1


            val totalResults = json.optInt("totalResults", 0)

            totalPages = ceil(totalResults / 20.0).toInt()
            val articles = json.getJSONArray("articles")
            for (i in 0 until articles.length()) {
                val currentArticle = articles.getJSONObject(i)
                val title = currentArticle.getString("title")
                val sourceObject = currentArticle.getJSONObject("source")
                val source = sourceObject.optString("name", "Unknown Source")
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

            return Pair(articleList, totalPages)
        } else (
                return Pair(emptyList(), 1)
                )


    }

    suspend fun GetSources(
        category: String,
        apiKey: String
    ): List<Source> {

        val request = Request.Builder()
            .url("https://newsapi.org/v2/top-headlines/sources?category=$category&language=en&apiKey=$apiKey")
            .header("authorization", "Bearer $apiKey")
            .get()
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody = response.body?.string()
        if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
            val sourceList = mutableListOf<Source>()
            val json = JSONObject(responseBody)


            val sources = json.getJSONArray("sources") ?: return emptyList()
            for (i in 0 until sources.length()) {
                val currentSource = sources.getJSONObject(i)
                val id = currentSource.optString("id", "")
                val name = currentSource.optString("name", "")
                val content = currentSource.optString("description", "")
                val category = currentSource.optString("category", "")
                val urlSource = currentSource.optString("url", "")



                val sourceItem = Source(
                    id = id,
                    name = name,
                    category = category,
                    description = content,
                    url = urlSource

                )

                sourceList.add(sourceItem)
            }
            return sourceList

        } else {
            return emptyList()
        }
    }
}