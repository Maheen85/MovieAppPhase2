package course.examples.movieappphase2.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.List;
import course.examples.movieappphase2.DetailActivity;
import course.examples.movieappphase2.R;
import course.examples.movieappphase2.model.Movie;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movies;

    public MoviesAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public MoviesAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //passing the layout of movie_item.xml file through view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MovieViewHolder holder, int position) {

        Movie current = movies.get(position);
        holder.title.setText(current.getOriginalTitle());
        String vote = Double.toString(current.getVoteAverage());
        holder.userrating.setText(vote);

        String poster = "https://image.tmdb.org/t/p/w500" + movies.get(position).getPosterPath();

        //gets poster from movie object and display it on image view of recycler view's movie_item.xml file
        Picasso.with(context)
                .load(poster)
                .placeholder(android.R.drawable.sym_def_app_icon)
                .error(android.R.drawable.sym_def_app_icon)
                .into(holder.thumbnail);
    }

    public void setMovies(List<Movie> movie) {
        movies = movie;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(movies != null)
            return movies.size();
        else  return 0;
    }

//inner class - viewHolder class
    public class MovieViewHolder extends RecyclerView.ViewHolder{


        private final TextView title, userrating;
        private final ImageView thumbnail;

        private MovieViewHolder(View view){
            super(view);
            title = view.findViewById(R.id.title);
            userrating = view.findViewById(R.id.userrating);
            thumbnail = view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        Movie clickedDataItem = movies.get(pos);
                        Intent intent = new Intent(context , DetailActivity.class);
                        intent.putExtra("Movie Clicked", clickedDataItem );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clicked " + clickedDataItem.getOriginalTitle(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}



