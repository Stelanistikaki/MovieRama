package com.example.movierama

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.widget.AutoCompleteTextView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.TextView
import android.text.Editable
import android.text.TextWatcher
import android.text.TextUtils
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MainActivity : AppCompatActivity() {
    private lateinit var autoSuggestAdapter: AutoSuggestAdapter
    private var handler: Handler? = null
    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY: Long = 300
    var myApiKey: String = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title.
        supportActionBar?.hide(); //hide the title bar.

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //swipe down to refresh
        refreshApp()

        //var declarations
        var popularList: ListView = findViewById(R.id.popularList)
        var autoComplete: AutoCompleteTextView  = findViewById(R.id.autoCompleteTextView)
        var selectedText: TextView  = findViewById(R.id.selected_item);
        //get favorite movies from api
        var task = MovieTask("movie/popular?api_key=$myApiKey&language=en-US&page=1")
        var movies: ArrayList<Movie> = task.execute().get()

        //set adapter for popular listview
        var adapter = MovieListAdapter(this, movies)
        popularList.adapter = adapter

        //set on click listener for list
        popularList.onItemClickListener = OnItemClickListener {
                parent, view, position, id ->
            val element: Movie? = adapter.getItem(position) // The item that was clicked
            val intent = Intent(this, DetailedMovieActivity::class.java)
            //when clicked get title and open the detailed activity
            intent.putExtra("title", element?.title)
            startActivity(intent);
        }

        //adapter for auto suggest view
        autoSuggestAdapter = AutoSuggestAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line
        )

        //autocomplete settings
        autoComplete.threshold = 2
        autoComplete.setAdapter(autoSuggestAdapter)
        autoComplete.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            selectedText.text = autoSuggestAdapter.getObject(position)
            val intent = Intent(this, DetailedMovieActivity::class.java)
            //when clicked from auto completed items open in detailed activity
            intent.putExtra("title",selectedText.text.toString())
            autoComplete.setText("")
            startActivity(intent);
        }

        autoComplete.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int,
                count: Int
            ) {
                handler?.removeMessages(TRIGGER_AUTO_COMPLETE)
                handler?.sendEmptyMessageDelayed(
                    TRIGGER_AUTO_COMPLETE,
                    AUTO_COMPLETE_DELAY
                )
            }

            override fun afterTextChanged(s: Editable) {}
        })
        //handler for when typing in autocomplete
        handler = Handler { msg ->
            if (msg.what === TRIGGER_AUTO_COMPLETE) {
                if (!TextUtils.isEmpty(autoComplete.text)) {
                    var autoCompleteTask = SearchMovieTask(autoComplete.text.toString())
                    var titles: ArrayList<String> = autoCompleteTask.execute().get()
                    //set data while typing
                    autoSuggestAdapter.setData(titles);
                    autoSuggestAdapter.notifyDataSetChanged();
                }
            }
            false
        }

    }

    private fun refreshApp() {
        val swipeToRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeToRefresh)
        val popularList: ListView = findViewById(R.id.popularList)
        //when refresh fetch new data again
        swipeToRefresh.setOnRefreshListener {
            var task = MovieTask("movie/popular?api_key=$myApiKey&language=en-US&page=1")
            var movies: ArrayList<Movie> = task.execute().get()

            var adapter = MovieListAdapter(this, movies)
            popularList.adapter = adapter
            swipeToRefresh.isRefreshing = false
        }

    }
}