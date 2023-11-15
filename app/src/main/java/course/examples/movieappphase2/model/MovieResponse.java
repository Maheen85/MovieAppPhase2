
package course.examples.movieappphase2.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class MovieResponse implements Parcelable {


    public static final Parcelable.Creator<MovieResponse> CREATOR = new Parcelable.Creator<MovieResponse>() {
        @Override
        public MovieResponse createFromParcel(Parcel source) {
            return new MovieResponse(source);
        }

        @Override
        public MovieResponse[] newArray(int size) {
            return new MovieResponse[size];
        }
    };

    @SerializedName("results")
    private List<Movie> results;


    public List<Movie> getResults() {
        return results;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.results);
    }

    protected MovieResponse(Parcel in) {
        this.results = in.createTypedArrayList(Movie.CREATOR);
    }
}
