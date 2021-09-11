package com.example.movierama

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.preference.PreferenceManager
import android.widget.*
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import java.lang.reflect.Type

class MovieListAdapter(private val context: Activity, private val data: ArrayList<Movie>)
    : ArrayAdapter<Movie>(context, R.layout.list_item, data) {
    var myFavorites = ArrayList<String>()

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_item, null, true)

        //var declarations
        val titleText :TextView = rowView.findViewById(R.id.titleTextView)
        val releaseDate: TextView = rowView.findViewById(R.id.dateTextView)
        val movieRating: RatingBar = rowView.findViewById(R.id.ratingBar)
        val favoriteCheckBox: CheckBox = rowView.findViewById(R.id.favorite)
        val posterImageView: ImageView = rowView.findViewById(R.id.posterImageView)

        //set the values
        val url: String = data[position].poster
        Picasso.get().load("https://image.tmdb.org/t/p/original/$url")
            .resize(250, 250)
            .into(posterImageView)

        titleText.text = data[position].title
        releaseDate.text = data[position].releaseDate
        //convert vote average to 5 star rating
        movieRating.rating = ((data[position].rating*5)/10)


        //get the favorites list
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val gson = Gson()
        val json: String? = prefs.getString("favorites", null)
        val type: Type = object : TypeToken<java.util.ArrayList<String?>?>() {}.getType()
        if(json != null)
            myFavorites = gson.fromJson(json, type)

        //set favorite checkbox
        for(id: String in myFavorites){
            if(data[position].id == id)
                favoriteCheckBox.setChecked(true)
        }

        //on change listener for the favorite checkbox
        favoriteCheckBox.setOnCheckedChangeListener { button, isChecked ->
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val editor: SharedPreferences.Editor = prefs.edit()
            if(favoriteCheckBox.isChecked)
                myFavorites.add(data[position].id)
            else
                myFavorites.remove(data[position].id)
            val gson = Gson()
            val json: String = gson.toJson(myFavorites)
            editor.putString("favorites", json)
            editor.apply()
        }

        return rowView

    }
}

