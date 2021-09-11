package com.example.movierama

import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import android.os.AsyncTask

class MovieTask(theUrl: String) : AsyncTask<Void, Void, ArrayList<Movie>>() {
    var client = OkHttpClient()
    val movieList = ArrayList<Movie>()
    val url = "https://api.themoviedb.org/3/$theUrl"

    override fun doInBackground(vararg params: Void?): ArrayList<Movie>? {
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
            //for each movie in result's array
            for (i in 0 until jsonArray.length()) {
                var id = jsonArray.getJSONObject(i).getString("id")
                val title = jsonArray.getJSONObject(i).getString("title")
                val releaseDate = jsonArray.getJSONObject(i).getString("release_date")
                val rating = jsonArray.getJSONObject(i).getString("vote_average")
                val overview = jsonArray.getJSONObject(i).getString("overview")
                val director = jsonArray.getJSONObject(i).getString("overview")
                val cast = jsonArray.getJSONObject(i).getString("overview")
                val poster = jsonArray.getJSONObject(i).getString("poster_path")
                val movie = Movie(id, title, releaseDate, rating.toFloat(), poster,"", overview, director, cast)
                //add data in movie list
                movieList.add(movie)
            }
        }
        return movieList
    }
}
