package com.example.movierama

import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import android.os.AsyncTask

class ReviewsTask(id: String) : AsyncTask<Void, Void, ArrayList<String>>() {
    var myApiKey: String = BuildConfig.API_KEY
    var client = OkHttpClient()
    val url = "https://api.themoviedb.org/3/movie/$id/reviews?api_key=$myApiKey&language=en-US&page=1"
    val reviewList = ArrayList<String>()

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
            val number:Int = jsonObject.getInt("total_results")
            //if reviews exist get the 2 first
            if(number > 0) {
                for (i in 0 until 1) {
                    val row = jsonArray.getJSONObject(i)
                    //get the reviews and add them in the list
                    reviewList.add(row.getString("content"))
                }
            }
        }
        //return the titles shown in autocomplete
        return reviewList
    }
}
