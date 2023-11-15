package course.examples.movieappphase2.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

import course.examples.movieappphase2.database.FavoriteDao;
import course.examples.movieappphase2.database.FavoriteEntry;
import course.examples.movieappphase2.repository.favoriteRepository;


public class ViewModel extends AndroidViewModel{

    /* learned from android code labs most of the architecture component parts
    https://codelabs.developers.google.com/codelabs/android-training-livedata-viewmodel/index.html?index=..%2F..%2Fandroid-training#0
     */

    private favoriteRepository mRepository;
    private LiveData<List<FavoriteEntry>> mAllFavorite;


    public ViewModel(Application application) {
        super(application);
        mRepository = new favoriteRepository(application);
        mAllFavorite = mRepository.loadAllFavorite();
    }


    public LiveData<List<FavoriteEntry>> getFavorite() {
        return mAllFavorite;
    }

    public void deleteAllFavorite(){
       mRepository.deleteAllFavorite();
    }

    public void deleteFavoriteWithId(int movie_id){ mRepository.deleteFavoriteWithId(movie_id);}

    public void insertFavoriteMovie(FavoriteEntry favoriteEntry){ mRepository.insertFavoriteMovie(favoriteEntry);}


}
