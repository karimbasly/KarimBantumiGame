package es.upm.miw.bantumi.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import es.upm.miw.bantumi.dao.PartidoDao;
import es.upm.miw.bantumi.model.Partido;


@Database(entities = {Partido.class},version = 1,exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    private static AppDataBase instance;

    public abstract PartidoDao pilotDao();


    public static AppDataBase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, "bantumi_saves")

                    .allowMainThreadQueries()
                    .build();

        }
        return instance;
    }
}
