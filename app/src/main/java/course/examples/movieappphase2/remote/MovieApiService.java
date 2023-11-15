package course.examples.movieappphase2.remote;


import course.examples.movieappphase2.model.MovieResponse;
import course.examples.movieappphase2.model.Review;
import course.examples.movieappphase2.model.TrailerResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {

    @GET("movie/top_rated")
   Call<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);


    @GET("movie/popular")
     Call<MovieResponse> getPopularMovies(@Query("api_key") String apiKey);


    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);


    @GET("movie/{movie_id}/reviews")
    Call<Review> getReview(@Path("movie_id") int id, @Query("api_key") String apiKey);

}
