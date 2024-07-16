package ir.wordpress.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object NetworkModule {

    private val client = OkHttpClient()
     suspend fun fetchContent(url: String): String? {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}