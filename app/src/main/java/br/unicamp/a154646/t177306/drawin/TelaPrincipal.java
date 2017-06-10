package br.unicamp.a154646.t177306.drawin;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import java.io.File;

public class TelaPrincipal extends AppCompatActivity {

    private ListView listView;
    private SQLiteDatabase sqLiteDatabase;
    DataBaseHelper dbHelper;
    private Cursor cursor;
    private String sql;
    private int PERMISSAO_LER = 0;
    private int PERMISSAO_GRAVAR =0 ;
    private int PERMISSAO_SDCARD = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abre a tela de desenhos
                Intent intent = new Intent(TelaPrincipal.this, TelaDesenho.class);
                startActivity(intent);
            }
        });

        //Pede as permissões necessárias
        ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSAO_LER);
        ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSAO_GRAVAR);
        ActivityCompat.requestPermissions(TelaPrincipal.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSAO_SDCARD);
    }

    //Coloca as imagens correspondentes ao itens do listview
    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {
        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (columnIndex == cursor.getColumnIndex("_id")) {
                ImageView imgIcone = (ImageView) view;
                int idIcone = cursor.getInt(columnIndex);
                File caminho = new File("storage/emulated/0/PicturesDrawin/imagem"+idIcone+".png");
                if(caminho.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(caminho.getAbsolutePath());
                    imgIcone.setImageBitmap(myBitmap);
                }
                return true;
            }
           return false;
        }
    }

    //começa a activity toda vez que é startada
    @Override
    public void onStart(){
        super.onStart();

        listView = (ListView) findViewById(R.id.lvItens);
        dbHelper = new DataBaseHelper(TelaPrincipal.this);
        sqLiteDatabase = dbHelper.getReadableDatabase();

        //Obtem as tuplas do Banco de Dados
        sql = "SELECT * from Item;";
        cursor = sqLiteDatabase.rawQuery(sql, null);

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(this,
                R.layout.list_itens,
                cursor,
                new String[]{"_id", "nome", "data"},
                new int[]{R.id.ivList,
                        R.id.tvNome,
                        R.id.tvData
                },
                0
        );

        //Edita o listview
        listView.setAdapter(simpleCursorAdapter);
        simpleCursorAdapter.setViewBinder(new CustomViewBinder());
        listView.setAdapter(simpleCursorAdapter);

        //Abre o item clicado no listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listview,
                                    View view,
                                    int position,
                                    long id) {
                Intent TelaVisualizacao = new Intent(TelaPrincipal.this, TelaVisualizacao.class);
                String idString = String.valueOf(id);
                TelaVisualizacao.putExtra("Clicado", idString);
                startActivity(TelaVisualizacao);
            }
        };

        //Seta a edição ao listview
        listView.setOnItemClickListener(itemClickListener);
    }

    //Fecha o cursor e o sqlLite para evitar erros
    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        sqLiteDatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
