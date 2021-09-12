# MovieRama

This is an Android app about movies. It is connected to the Movie DB API (https://themoviedb.org). 

When the user opens the app the first screen is showing a list of the popular movies. 
It is handled with an arraylist coming from the popular URL from the API and an adapter for the view of the list. 
The user also can search a movie in the dB. While searching the suggested moves will pop up in the autosuggested view with a list. 
Once the user clicks a movie from the search or the popular list he will be directed to the detailed view of the movie. 
There the information that it is available is the poster, the title of the movie, the released date and also 2 more lists: the reviews and the similar movies if they exist. 
Last but not least, the user can chose his favourite movies by clicking the heart sign in the popular list or directly in the detailed view of the movie.

For the API calls okhttp library is used in MovieTask, ReviewTask and SearchMovieTask which return the Movie Objects, reviews and titles for the autocomplete view respectively. 
The poster images are loaded though the library of Picasso.
