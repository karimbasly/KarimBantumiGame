package es.upm.miw.bantumi.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import es.upm.miw.bantumi.model.Partido;

@Dao
public interface PartidoDao {
    @Insert
    void insertOne(Partido pilot);
    @Delete
    void delete(Partido pilot);
    @Query("SELECT * FROM bantumi_saves ORDER BY partido_score DESC LIMIT 10")
    List<Partido> getAll();
    @Query("DELETE FROM bantumi_saves")
    void deleteAll();
}
