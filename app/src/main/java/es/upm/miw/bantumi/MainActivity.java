package es.upm.miw.bantumi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.upm.miw.bantumi.database.AppDataBase;
import es.upm.miw.bantumi.model.BantumiViewModel;
import es.upm.miw.bantumi.model.Partido;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    //Save game in file
    protected   String SAVE_GAME = "savedGame.txt";
    JuegoBantumi juegoBantumi;
    BantumiViewModel bantumiVM;
    int numInicialSemillas;
    AlertDialog.Builder builder;
    TextView scoreJ2;
    TextView scoreJ1;
    private List<Partido> rowList = new ArrayList<Partido>();
    private TextView textViewPlayer1;
    private SharedPreferences sharedPref;
    //Database instance
    AppDataBase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scoreJ2 = findViewById(R.id.casilla_13);
        scoreJ1 = findViewById(R.id.casilla_06);
        database = AppDataBase.getAppDatabase(this);
        this.textViewPlayer1 = findViewById(R.id.tvPlayer1);

        //Init Builder
        builder = new AlertDialog.Builder(this);

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        crearObservadores();

        //Resetting the Game
        Button ResetBtn = findViewById(R.id.button);
        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AlertDialog
                builder
                        .setTitle(R.string.txtReiniciarPartida)
                        .setMessage(R.string.ResetGameMessage)
                        .setPositiveButton(
                                getString(R.string.txtDialogoFinalAfirmativo),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);
                                    }
                                }
                        )
                        .setNegativeButton(
                                getString(R.string.txtDialogoFinalNegativo),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Nothing
                                    }
                                }
                        );
                AlertDialog AD = builder.create();
                AD.show();





            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = this.sharedPref.getString("signature","");

        if (name != null) {
            this.textViewPlayer1.setText(name);
        }
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero, actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.design_default_color_primary));
                tvJugador2.setTextColor(getColor(R.color.black));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.design_default_color_primary));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcAjustes:
                startActivity(new Intent(this, BantumiPrefs.class));
                return true;
            case R.id.opcAcercaDe:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.aboutTitle)
                        .setMessage(R.string.aboutMessage)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return true;

            case R.id.opcReiniciarPartida:
                juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);
                return true;

            case R.id.opcMejoresResultados:
                Intent newIntent = new Intent(MainActivity.this, MejoresRes.class);
                startActivity(newIntent);
                return true;

            case R.id.opcGuardarPartida:
                        new AlertDialog.Builder(this)
                                .setTitle(R.string.save_game)
                                .setMessage(R.string.saving_game)
                                .setPositiveButton(getString(R.string.txtDialogoFinalAfirmativo),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try{
                                                saveGame();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }
                                )
                                .setNegativeButton(getString(R.string.txtDialogoFinalNegativo), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Do nothing
                                    }
                                })
                                .show();

                return true;

            case R.id.opcRecuperarPartida:
                if(gameSaved()) {
                    Snackbar.make(
                            findViewById(android.R.id.content),
                            getString(R.string.emptyfile),
                            Snackbar.LENGTH_LONG
                    ).show();
                } else {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.get_game)
                        .setMessage(R.string.getting_game)
                        .setPositiveButton(getString(R.string.txtDialogoFinalAfirmativo),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                       try {
                                        getGame();}
                                       catch (Exception e) {
                                           e.printStackTrace();
                                       }
                                    }
                                }
                        )
                        .setNegativeButton(getString(R.string.txtDialogoFinalNegativo), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        })
                        .show(); }
            default:
                Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.txtSinImplementar),
                        Snackbar.LENGTH_LONG
                ).show();
        }
        return true;

    }

    private void saveGame() {
        FileOutputStream fileSaveGame = null;
        try {
            fileSaveGame = openFileOutput(SAVE_GAME, MODE_PRIVATE); // removes previously existing content
            fileSaveGame = openFileOutput(SAVE_GAME, MODE_APPEND); // appends content in the end
            fileSaveGame.write(juegoBantumi.serializa().getBytes());
            Toast.makeText(this, "Saved to " + getFilesDir() + "/" + SAVE_GAME,
                    Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (fileSaveGame != null) {
                try {
                    fileSaveGame.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private boolean gameSaved() {
        return  savedFile().size() != 14;
    }
    private void getGame() {
        List<String> gameState = savedFile();
        juegoBantumi.deserializa(gameState);
    }
    private List<String> savedFile() {
        FileInputStream fileGame = null;
        List<String> gameState = new ArrayList<>();

        try {
            fileGame = openFileInput(SAVE_GAME);
            BufferedReader brGetGame = new BufferedReader(new InputStreamReader(fileGame));
            String text;
            while ((text = brGetGame.readLine()) != null) {
                gameState.add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileGame != null) {
                try {
                    fileGame.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return gameState ;
    }


    /**
     * Acción que se ejecuta al pulsar sobre un hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * Elige una posición aleatoria del campo del jugador2 y realiza la siembra
     * Si mantiene turno -> vuelve a jugar
     */
    void juegaComputador() {
        while (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            int pos = 7 + (int) (Math.random() * 6);    // posición aleatoria
            Log.i(LOG_TAG, "juegaComputador(), pos=" + pos);
            if (juegoBantumi.getSemillas(pos) != 0 && (pos < 13)) {
                juegoBantumi.jugar(pos);
            } else {
                Log.i(LOG_TAG, "\t posición vacía");
            }
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String score1 = scoreJ1.getText().toString();
        String score2 = scoreJ2.getText().toString();
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String name = this.sharedPref.getString("signature","");
        Partido win = (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                ? winner(name,score1)
                : winner("Jugador 2",score2);
        Snackbar.make(
                findViewById(android.R.id.content),
                win.toString(),
                Snackbar.LENGTH_LONG
        ).show();
        //inserting partido to database
        database.pilotDao().insertOne(win);

        rowList = database.pilotDao().getAll();
        System.out.println(rowList);
        new FinalAlertDialog().show(getSupportFragmentManager(), "ALERT_DIALOG");
    }

    private Partido winner(String the_winner,String score){
        Date today =  Calendar.getInstance().getTime();;
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = curFormater.format(today);
        //Creating Partido instance
        Partido prt = new Partido();
        prt.setDate(formattedDate);
        prt.setJugador(the_winner);
        prt.setScore(score);
        return prt;
    }
}