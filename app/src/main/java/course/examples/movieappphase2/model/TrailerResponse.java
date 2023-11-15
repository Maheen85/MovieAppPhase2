package course.examples.movieappphase2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TrailerResponse {

    @SerializedName("id")
    private int id_trailer;
    @SerializedName("results")
    private List<Trailer> results;


    public List<Trailer> getResults(){
        return results;
    }
}
