package es.upm.miw.bantumi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import es.upm.miw.bantumi.adapters.RecyclerViewAdapter;
import es.upm.miw.bantumi.database.AppDataBase;
import es.upm.miw.bantumi.model.Partido;

public class MejoresRes extends AppCompatActivity {

    private RecyclerView myrv ;
    AppDataBase dataBase ;
    RecyclerViewAdapter myAdapter;
    List<Partido> mData= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mejores_res);
        dataBase = AppDataBase.getAppDatabase(this);
        mData = dataBase.pilotDao().getAll();


        myrv = findViewById(R.id.rv);
        myAdapter = new RecyclerViewAdapter(this,mData) ;
        myrv.setLayoutManager(new LinearLayoutManager(this));
        myrv.setAdapter(myAdapter);

    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.mymenu is a reference to an xml file named mymenu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.deleteItem) {
            // do something here
            dataBase.pilotDao().deleteAll();
            mData.clear();
            myAdapter.notifyDataSetChanged();


        }
        return super.onOptionsItemSelected(item);
    }
}