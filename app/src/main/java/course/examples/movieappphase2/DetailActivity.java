package course.examples.movieappphase2;



import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import course.examples.movieappphase2.ViewModel.MyExecutors;
import course.examples.movieappphase2.ViewModel.ViewModel;
import course.examples.movieappphase2.adapter.ReviewAdapter;
import course.examples.movieappphase2.adapter.TrailerAdapter;
import course.examples.movieappphase2.database.AppDatabase;
import course.examples.movieappphase2.database.FavoriteEntry;
import course.examples.movieappphase2.model.Movie;
import course.examples.movieappphase2.model.Review;
import course.examples.movieappphase2.model.ReviewResult;
import course.examples.movieappphase2.model.Trailer;
import course.examples.movieappphase2.model.TrailerResponse;
import course.examples.movieappphase2.remote.Client;
import course.examples.movieappphase2.remote.MovieApiService;




import com.github.ivbaranov.mfb.MaterialFavoriteButton;
import com.squareup.picasso.Picasso;
import com.takusemba.multisnaprecyclerview.MultiSnapRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressWarnings("ALL")
public class DetailActivity extends AppCompatActivity {

    TextView nameOfMovie, plotSynopsis, userRating, releaseDate;
    ImageView imageView;

    private RecyclerView recyclerViewTrailer,recyclerViewReview;
    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerList;
    private ReviewAdapter reviewAdapter;
    private List<ReviewResult> reviewList;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;
    private AppDatabase mDb;
    List<FavoriteEntry> entries = new ArrayList<>();
    boolean exists;
    private ViewModel mMainViewModel;

    Movie clickedMovieRecived;

    String thumbnail, movieName, synopsis, rating, dateOfRelease, poster;
    int movie_id;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = AppDatabase.getInstance(getApplicationContext());

        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        nameOfMovie = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        userRating = (TextView) findViewById(R.id.userrating);
        releaseDate = (TextView) findViewById(R.id.releasedate);


        //Recieve data
        Intent intentThatStartedThisActivity = getIntent();


        //first check if the received intent has extra
        if(intentThatStartedThisActivity.hasExtra("Movie Clicked"))
        {
             clickedMovieRecived = getIntent().getParcelableExtra("Movie Clicked");

             thumbnail = clickedMovieRecived.getPosterPath();
             movieName = clickedMovieRecived.getOriginalTitle();
             synopsis = clickedMovieRecived.getOverview();
             rating = Double.toString(clickedMovieRecived.getVoteAverage());
             dateOfRelease = clickedMovieRecived.getReleaseDate();
            movie_id =clickedMovieRecived.getId();
            String poster = "http://image.tmdb.org/t/p/w500" + thumbnail ;

            Picasso.with(this)
                    .load(poster)
                    .placeholder(R.drawable.defaultposter)
                    .into(imageView);

            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);

            initCollaspingToolbar();
        }

        //if the intent that was received does not has an extra show a message
        else
            Toast.makeText(this, "No API Data", Toast.LENGTH_LONG).show();

        checkStatus(movieName);
        initViews();

    }

    private void initCollaspingToolbar() {

        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collasping_toolbar);
        collapsingToolbarLayout.setTitle(movieName);

        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }

        });


    }

    private void initViews() {

        reviewList =new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this ,reviewList);

        trailerList = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailerList);


        //initializing recycler view for trailers
        recyclerViewTrailer = (RecyclerView) findViewById(R.id.trailer_recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        recyclerViewTrailer.setLayoutManager(mLayoutManager);
        recyclerViewTrailer.setAdapter(trailerAdapter);
        trailerAdapter.notifyDataSetChanged();

        //initializing recycler view for reviews
        recyclerViewReview = (RecyclerView) findViewById(R.id.review_recyclerview);
        RecyclerView.LayoutManager reviewLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewReview.setLayoutManager(reviewLayoutManager);
        recyclerViewReview.setAdapter(reviewAdapter);
        reviewAdapter.notifyDataSetChanged();


        loadJSONtrailer();
        loadReview();
    }


    private void loadJSONtrailer(){

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please get your API Key", Toast.LENGTH_SHORT).show();
                return;
            }else {
                Client Client = new Client();
                MovieApiService apiService = Client.getClient().create(MovieApiService.class);
                Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
                call.enqueue(new Callback<TrailerResponse>() {
                    @Override
                    public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                List<Trailer> trailer = response.body().getResults();
                                MultiSnapRecyclerView recyclerViewTrailer = (MultiSnapRecyclerView) findViewById(R.id.trailer_recyclerview);
                                LinearLayoutManager managerTrailer = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerViewTrailer.setLayoutManager(managerTrailer);
                                recyclerViewTrailer.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                                recyclerViewTrailer.smoothScrollToPosition(0);

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TrailerResponse> call, Throwable t) {
                        Log.d("Error", t.getMessage());
                        Toast.makeText(DetailActivity.this, "Error fetching trailer", Toast.LENGTH_SHORT).show();

                    }
                });
            }

        } catch(Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }


    }


    private void loadReview(){
        try {
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please get your API Key", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Client Client = new Client();
                MovieApiService apiService = Client.getClient().create(MovieApiService.class);
                Call<Review> call = apiService.getReview(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);

                call.enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        if (response.isSuccessful()){
                            if (response.body() != null){
                                List<ReviewResult> reviewResults = response.body().getResults();
                                MultiSnapRecyclerView recyclerView2 = (MultiSnapRecyclerView) findViewById(R.id.review_recyclerview);
                                LinearLayoutManager firstManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView2.setLayoutManager(firstManager);
                                recyclerView2.setAdapter(new ReviewAdapter(getApplicationContext(), reviewResults));
                                recyclerView2.smoothScrollToPosition(0);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {

                    }
                });
            }

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
            Toast.makeText(this, "unable to fetch data",Toast.LENGTH_SHORT).show();
        }

    }


    public void saveFavorite(){
        Double rate = clickedMovieRecived.getVoteAverage();
        final FavoriteEntry favoriteEntry = new FavoriteEntry(movie_id, movieName, rate, thumbnail, synopsis);
        MyExecutors.getInstance().getThread1().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteDao().insertFavorite(favoriteEntry);
            }
        });
    }


    private void deleteFavoriteWithId(final int movie_id){

        MyExecutors.getInstance().getThread1().execute(new Runnable() {
            @Override
            public void run() {
                mDb.favoriteDao().deleteFavoriteWithId(movie_id);
            }
        });

    }


    //using an asynctask so that we can recieve data on main thread
    //also calling the saveFavorite() and deleteFavorite() from favorite table in onPostExecute()

    @SuppressLint("StaticFieldLeak")
    private void checkStatus(final String movieName){
        final MaterialFavoriteButton materialFavoriteButton = (MaterialFavoriteButton) findViewById(R.id.favorite_button);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params){
                entries.clear();
                entries = mDb.favoriteDao().loadAll(movieName);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                if (entries.size() > 0){
                    materialFavoriteButton.setFavorite(true);
                    materialFavoriteButton.setOnFavoriteChangeListener(
                            new MaterialFavoriteButton.OnFavoriteChangeListener() {
                                @Override
                                public void onFavoriteChanged(MaterialFavoriteButton buttonView, boolean favorite) {
                                    if (favorite == true) {
                                        saveFavorite();
                                        Snackbar.make(buttonView, "Added to Favorite",
                                                Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        deleteFavoriteWithId(movie_id);
                                        Snackbar.make(buttonView, "Removed from Favorite",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }else {
                    materialFavoriteButton.setOnFavoriteChangeListener(
                            new MaterialFavoriteButton.OnFavoriteChangeListener() {
                                @Override
                                public void onFavoriteChanged(MaterialFavoriteButton heartButtonView, boolean favoriteSelected) {
                                    if (favoriteSelected == true) {
                                        saveFavorite();
                                        Snackbar.make(heartButtonView, "Added to Favorite",
                                                Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        int movie_id = getIntent().getExtras().getInt("id");
                                        deleteFavoriteWithId(movie_id);
                                        Snackbar.make(heartButtonView, "Removed from Favorite",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        }.execute();


    }


}



