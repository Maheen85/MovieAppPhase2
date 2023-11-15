package course.examples.movieappphase2.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import course.examples.movieappphase2.database.AppDatabase;
import course.examples.movieappphase2.database.FavoriteDao;
import course.examples.movieappphase2.database.FavoriteEntry;

public class favoriteRepository {

    private FavoriteDao mFavoriteDao;
    private LiveData<List<FavoriteEntry>> mAllFavoriteMovies;

    private static final String TAG = favoriteRepository.class.getSimpleName();

    public favoriteRepository(Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        mFavoriteDao = db.favoriteDao();
        mAllFavoriteMovies = mFavoriteDao.loadAllFavorite();
    }



    //LOAD ALL FAVORITE
    public LiveData<List<FavoriteEntry>> loadAllFavorite(){
        return mAllFavoriteMovies;
    }


    //DELETE ALL
    public void deleteAllFavorite(){
        if (mAllFavoriteMovies != null){
            new deleteAllFavoriteAsyncTask(mFavoriteDao).execute();
        }else
            Log.d(TAG, " Favorite database is already empty");
    }

    private static class deleteAllFavoriteAsyncTask extends AsyncTask<Void, Void, Void> {

        private FavoriteDao mAsyncTaskfavoriteDao;

        private deleteAllFavoriteAsyncTask(FavoriteDao favoriteDao){
            mAsyncTaskfavoriteDao = favoriteDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {

            mAsyncTaskfavoriteDao.deleteAll();
            return null;
        }
    }



    //DELETE WITH MOVIE_ID
    public void deleteFavoriteWithId(int movie_id){
        new deleteFavoriteWithIdAsyncTask(mFavoriteDao).execute(movie_id);

    }

    private static class  deleteFavoriteWithIdAsyncTask extends AsyncTask<Integer , Void, Void>{

        private FavoriteDao mAsyncTaskfavoriteDao;

        private  deleteFavoriteWithIdAsyncTask(FavoriteDao favoriteDao){
            mAsyncTaskfavoriteDao = favoriteDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            mAsyncTaskfavoriteDao.deleteFavoriteWithId(integers[0]);
            return null;
        }
    }



    //INSERT
    public void insertFavoriteMovie(FavoriteEntry favoriteEntry){
        new insertFavoriteAsyncTask(mFavoriteDao).execute(favoriteEntry);
    }

    private static class insertFavoriteAsyncTask extends AsyncTask<FavoriteEntry,Void,Void>{

        private FavoriteDao mAsyncTaskfavoriteDao;
        private insertFavoriteAsyncTask(FavoriteDao favoriteDao){
            mAsyncTaskfavoriteDao = favoriteDao;
        }


        @Override
        protected Void doInBackground(final FavoriteEntry... favoriteEntries) {
            mAsyncTaskfavoriteDao.insertFavorite(favoriteEntries[0]);
            return null;
        }
    }

}
