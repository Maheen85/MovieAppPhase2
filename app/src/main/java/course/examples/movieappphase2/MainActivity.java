package course.examples.movieappphase2;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import course.examples.movieappphase2.ViewModel.ViewModel;
import course.examples.movieappphase2.adapter.MoviesAdapter;
import course.examples.movieappphase2.database.FavoriteEntry;
import course.examples.movieappphase2.model.Movie;
import course.examples.movieappphase2.model.MovieResponse;
import course.examples.movieappphase2.remote.Client;
import course.examples.movieappphase2.remote.MovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MoviesAdapter.class.getSimpleName();
    private RecyclerView recyclerViewMovie;
    private MoviesAdapter moviesAdapter;
    private ArrayList<Movie> moviesInstance = new ArrayList<>();
    private List<Movie> movies = new ArrayList<>();
    private static String LIST_STATE = "list_state";
    private Parcelable savedRecyclerViewLayoutManager;
    private static final String KEY_RV_POSITION = "recycler_layout";
    private ViewModel mMainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviesAdapter = new MoviesAdapter(this, movies);
        mMainViewModel = ViewModelProviders.of(this).get(ViewModel.class);

        checkForNetworkConnection();

        if (savedInstanceState != null) {
            moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
            savedRecyclerViewLayoutManager = savedInstanceState.getParcelable(KEY_RV_POSITION);
            recreatingUIonScreenRotation();
        } else
            firstTimeInitializingViews();
    }

    private void recreatingUIonScreenRotation() {

        recyclerViewMovie = findViewById(R.id.movie_recyclerview);
        moviesAdapter = new MoviesAdapter(this, moviesInstance);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewMovie.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerViewMovie.setLayoutManager(new GridLayoutManager(this, 4));
        }

        recyclerViewMovie.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMovie.setAdapter(moviesAdapter);
        restoreLayoutManagerPosition();
        moviesAdapter.notifyDataSetChanged();
    }

    private void restoreLayoutManagerPosition() {
        if (savedRecyclerViewLayoutManager != null)
            recyclerViewMovie.getLayoutManager().onRestoreInstanceState(savedRecyclerViewLayoutManager);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {

        super.onSaveInstanceState(currentState);
        //to restore movie objects from the list of movies when activity is recreated on screen rotation
        currentState.putParcelableArrayList(LIST_STATE, moviesInstance);
        //Save state of recyclerview
        currentState.putParcelable(KEY_RV_POSITION, recyclerViewMovie.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        moviesInstance = savedInstanceState.getParcelableArrayList(LIST_STATE);
        savedRecyclerViewLayoutManager = savedInstanceState.getParcelable(KEY_RV_POSITION);
        super.onRestoreInstanceState(savedInstanceState);
    }

    //getting activity context to find device orientation which is used to set GridLayoutManager according to orientation.
    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    //Method to initialize the views

    private void firstTimeInitializingViews() {
        recyclerViewMovie = findViewById(R.id.movie_recyclerview);
        moviesAdapter = new MoviesAdapter(this, moviesInstance);
        //setting GridLayoutManager according to device orientation
        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerViewMovie.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerViewMovie.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerViewMovie.setItemAnimator(new DefaultItemAnimator());
        recyclerViewMovie.setAdapter(moviesAdapter);
        checkSortOrder();
    }

    private void checkSortOrder() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(this.getString(R.string.pref_sort_order_key), this.getString(R.string.pref_most_popular));


        if (sortOrder.equals(this.getString(R.string.pref_most_popular))) {
            Log.d(LOG_TAG, "Sorting by most popular");
            loadJSONmostpopular();
        } else if (sortOrder.equals(this.getString(R.string.favorite))) {
            Log.d(LOG_TAG, "Sorting by favorite");
            loadFavorite();
        } else {
            Log.d(LOG_TAG, "Sorting by vote average");
            loadJSONtoprated();

        }
    }

    private boolean checkForNetworkConnection() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isConnected) {
            Toast.makeText(this, "Check Network Connection !!! ", Toast.LENGTH_LONG).show();
            return !isConnected;

        }

        return isConnected;
    }

    private void loadJSONmostpopular() {

        //checking for Api key
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                return;
            }


            Client Client = new Client();
            MovieApiService apiService =
                    Client.getClient().create(MovieApiService.class);
            Call<MovieResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);


            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Loading Most Popular movies successful!!", Toast.LENGTH_SHORT).show();


                        if (response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            moviesInstance.addAll(movies);
                            recyclerViewMovie.setAdapter(new MoviesAdapter(getApplicationContext(), moviesInstance));
                            recyclerViewMovie.smoothScrollToPosition(0);
                        }
                    }
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void loadJSONtoprated() {

        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "API Key from themoviedb.org is required ...", Toast.LENGTH_SHORT).show();
                return;
            }


            Client Client = new Client();
            MovieApiService apiService = Client.getClient().create(MovieApiService.class);

            Call<MovieResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);

            call.enqueue(new Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Loading Top Rated movies successful!!", Toast.LENGTH_SHORT).show();

                        if (response.body() != null) {
                            List<Movie> movies = response.body().getResults();
                            moviesInstance.addAll(movies);
                            recyclerViewMovie.setAdapter(new MoviesAdapter(getApplicationContext(), moviesInstance));
                            recyclerViewMovie.smoothScrollToPosition(0);
                        }
                    }

                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }


    }

    private void loadFavorite() {


        recyclerViewMovie = findViewById(R.id.movie_recyclerview);

        getAllFavorite();
    }

    private void getAllFavorite() {

        mMainViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        mMainViewModel.getFavorite().observe(this, new Observer<List<FavoriteEntry>>() {
            @Override
            public void onChanged(@Nullable List<FavoriteEntry> favoriteEntries) {

                for (FavoriteEntry singleMovieEntry : favoriteEntries) {

                    //movie object to get data from room database and add it later to list of movies called movieInstance.
                    Movie movie = new Movie();

                    movie.setId(singleMovieEntry.getMovieid());
                    movie.setOverview(singleMovieEntry.getOverview());
                    movie.setOriginalTitle(singleMovieEntry.getTitle());
                    movie.setPosterPath(singleMovieEntry.getPosterpath());
                    movie.setVoteAverage(singleMovieEntry.getUserrating());

                    moviesInstance.add(movie);
                }
                moviesAdapter.setMovies(moviesInstance);
                recyclerViewMovie.setAdapter(new MoviesAdapter(getApplicationContext(), moviesInstance));
                recyclerViewMovie.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
   public boolean onOptionsItemSelected(MenuItem item){


        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;


            case R.id.delete_all_favorite:
                mMainViewModel.getFavorite().observe(this, new Observer<List<FavoriteEntry>>() {
                    @Override
                    public void onChanged(@Nullable List<FavoriteEntry> favoriteEntries) {
                        mMainViewModel.deleteAllFavorite();
                        moviesInstance.clear();
                        moviesAdapter.setMovies(moviesInstance);
                    }
                });


               Toast.makeText(this," Favorite Movies record Deleted",Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
