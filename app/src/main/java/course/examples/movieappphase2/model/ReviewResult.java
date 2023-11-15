package course.examples.movieappphase2.model;




import android.os.Parcel;
        import android.os.Parcelable;
        import android.os.Parcelable.Creator;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class ReviewResult implements Parcelable
{
    public final static Creator<ReviewResult> CREATOR = new Creator<ReviewResult>() {


        @SuppressWarnings({
                "unchecked"
        })
        public ReviewResult createFromParcel(Parcel in) {
            return new ReviewResult(in);
        }

        public ReviewResult[] newArray(int size) {
            return (new ReviewResult[size]);
        }

    };

    @SerializedName("author")
    @Expose
    private String author;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("id")
    @Expose
    private String id;

    protected ReviewResult(Parcel in) {
        this.author = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
        this.id = ((String) in.readValue((String.class.getClassLoader())));
    }


    public ReviewResult() {
    }

    public ReviewResult(String author, String content, String id) {
        super();
        this.author = author;
        this.content = content;
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(author);
        dest.writeValue(content);
        dest.writeValue(id);
    }

    public int describeContents() {
        return 0;
    }

}