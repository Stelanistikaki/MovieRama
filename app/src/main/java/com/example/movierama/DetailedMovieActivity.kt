package com.example.movierama

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.CarouselView
import java.lang.reflect.Type
import com.synnapps.carouselview.ImageListener
import android.widget.ArrayAdapter
import android.view.ViewGroup
import androidx.core.view.isVisible


class DetailedMovieActivity : AppCompatActivity() {
    var myFavorites = ArrayList<String>()
    var myApiKey: String = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title.
        supportActionBar?.hide(); //hide the title bar.

        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailed_activity)

        //var declarations
        var titleTextView: TextView = findViewById(R.id.detailedTitleTextView)
        var overviewTextView: TextView = findViewById(R.id.overviewTextView)
        var releaseDateTextView: TextView = findViewById(R.id.detailedRelasedDate)
        var reviewsTextView: TextView = findViewById(R.id.textViewReviews)
        var similarTextView: TextView = findViewById(R.id.textViewSimilar)
        var ratingBarView: RatingBar = findViewById(R.id.detailedRatingBar)
        var posterImageView: ImageView = findViewById(R.id.detailedImageView)
        var checkboxFavorite: CheckBox = findViewById(R.id.detailedFavoriteCheckbox)
        var similarList: ListView = findViewById(R.id.similarListView)
        var reviewList: ListView = findViewById(R.id.reviewsListView)
        var title: String? = intent.getStringExtra("title")
        titleTextView.text = title

        //by default they are false until the lists are not empty
        similarTextView.isVisible = false
        reviewsTextView.isVisible = false

        //get the favorites in a string list with movie's IDs
        val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val gson = Gson()
        val json: String? = prefs.getString("favorites", null)
        val type: Type = object : TypeToken<java.util.ArrayList<String?>?>() {}.getType()
        if(json != null)
            myFavorites = gson.fromJson(json, type)

        //get the details for the movie that was chosen
        val task = MovieTask("search/movie?api_key=$myApiKey&language=en-US&query=$title&page=1&include_adult=false")
        var movie: ArrayList<Movie> = task.execute().get()

        for(id: String in myFavorites){
            //the movie that was chosen is the first returned
            if(movie[0].id == id)
                checkboxFavorite.setChecked(true)
        }

        //set the values
        overviewTextView.text = movie[0].overview
        releaseDateTextView.text = movie[0].releaseDate
        //convert vote average to 5 star rating
        ratingBarView.rating = (movie[0].rating*5)/10
        //get images from poster url
        val url: String = movie[0].poster
        Picasso.get().load("https://image.tmdb.org/t/p/original/$url")
            .resize(1000, 1000)
            .into(posterImageView)

        //on check changed listener for favorite checkbox
        checkboxFavorite.setOnCheckedChangeListener { button, isChecked ->
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val editor: SharedPreferences.Editor = prefs.edit()
            //for new favorite movies add the movie in the string list
            if(checkboxFavorite.isChecked)
                myFavorites.add(movie[0].id)
            else
                //or remove if the favorite is unchecked
                myFavorites.remove(movie[0].id)
            val gson = Gson()
            val json: String = gson.toJson(myFavorites)
            editor.putString("favorites", json)
            editor.apply()
        }

        //get similar movies from api
        var id: String = movie[0].id
        var similarTask = MovieTask("movie/$id/similar?api_key=$myApiKey&language=en-US&page=1")
        var movies: ArrayList<Movie> = similarTask.execute().get()

        //set adapter for popular listview
        if(movies.isNotEmpty()){
            similarTextView.isVisible = true
            var adapter = MovieListAdapter(this, movies)
            similarList.adapter = adapter
            //set on click listener for list
            similarList.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id ->
                    val element: Movie? = adapter.getItem(position) // The item that was clicked
                    val intent = Intent(this, DetailedMovieActivity::class.java)
                    //when clicked get title and open the detailed activity
                    intent.putExtra("title", element?.title)
                    startActivity(intent);
                }
            justifyListViewHeightBasedOnChildren(similarList);
        }

        //get reviews for movie from api
        var reviewTask = ReviewsTask(id)
        var reviews: ArrayList<String> = reviewTask.execute().get()
        if(reviews.isNotEmpty()){
            reviewsTextView.isVisible = true
            reviewList.adapter = ArrayAdapter<String>(this@DetailedMovieActivity, R.layout.review_item, reviews)
            justifyListViewHeightBasedOnChildren(reviewList);
        }
    }

    //function to inactive list scrolling
    fun justifyListViewHeightBasedOnChildren(listView: ListView) {
        val adapter = listView.adapter ?: return
        val vg: ViewGroup = listView
        var totalHeight = 0
        for (i in 0 until adapter.count) {
            val listItem: View = adapter.getView(i, null, vg)
            listItem.measure(0, 0)
            totalHeight += listItem.getMeasuredHeight()
        }
        val par = listView.layoutParams
        par.height = totalHeight + listView.dividerHeight * (adapter.count - 1)
        listView.layoutParams = par
        listView.requestLayout()
    }

}