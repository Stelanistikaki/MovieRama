package com.example.movierama

import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import android.os.AsyncTask

class SearchMovieTask(text: String) : AsyncTask<Void, Void, ArrayList<String>>() {
    var myApiKey: String = BuildConfig.API_KEY
    var client = OkHttpClient()
    val url = "https://api.themoviedb.org/3/search/movie?api_key=$myApiKey&language=en-US&query=$text&page=1&include_adult=false"
    val titleList = ArrayList<String>()

    override fun doInBackground(vararg params: Void?): ArrayList<String>? {
        val request = Request.Builder()
            .url(url)
            .build()
        //get response
        var response = client.newCall(request).execute()
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        else {
            val bodyString = response.body()?.string()
            val jsonObject = JSONTokener(bodyString).nextValue() as JSONObject
            val jsonArray = jsonObject.getJSONArray("results")
            for (i in 0 until jsonArray.length()) {
                val row = jsonArray.getJSONObject(i)
                //get the titles and add them in the list
                titleList.add(row.getString("title"))
            }
        }
        //return the titles shown in autocomplete
        return titleList
    }
}
