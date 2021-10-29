package es.upm.miw.bantumi.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bantumi_saves")
public class Partido {
    @PrimaryKey(autoGenerate = true)
    private int idPartido;

    @ColumnInfo(name = "partido_score")
    private String score;

    @ColumnInfo(name = "partido_jugador")
    private String jugador;

    @ColumnInfo(name = "partido_date")
    private String date;

    public Partido() { }

    @Override
    public String toString() {
        return "Partido{" +
                "idPartido=" + idPartido +
                ", score='" + score + '\'' +
                ", jugador='" + jugador + '\'' +
                ", date='" + date + '\'' +
                '}';
    }

    public int getIdPartido() {
        return idPartido;
    }

    public void setIdPartido(int idPartido) {
        this.idPartido = idPartido;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
