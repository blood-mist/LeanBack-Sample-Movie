package stb.androidtv.moviesleanback;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by ACER on 10/27/2017.
 */

public class ApplicationMain extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);

        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(0) // Must be bumped when the schema changes
//                .migration(new MyMigration()) // Migration to run instead of throwing an exception
                .deleteRealmIfMigrationNeeded()
                .build();

        Realm.setDefaultConfiguration(config);
    }
}
