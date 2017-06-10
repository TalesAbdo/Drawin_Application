package br.unicamp.a154646.t177306.drawin;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class TelaVisualizacao extends AppCompatActivity {

    private SQLiteDatabase sqLiteDatabase;
    private Cursor cursor;
    private String sql;
    DataBaseHelper dbHelper;
    private ImageView imageView;
    String caminhoImagem;
    String caminhoAudio;
    ImageButton btPlay;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_visualizacao);

        dbHelper = new DataBaseHelper(TelaVisualizacao.this);
        sqLiteDatabase = dbHelper.getReadableDatabase();
        imageView = (ImageView) findViewById(R.id.ivTeste);
        btPlay = (ImageButton) findViewById(R.id.btPlay);

        //serve para pegar a posição que foi clicada no listview e abrir a imagem certa
        Intent i = getIntent();
        String newString = i.getStringExtra("Clicado");

        //seleciona o caminho certo
        sql = "SELECT caminhoImagem, caminhoAudio from Item where _id = " + newString + ";";
        cursor = sqLiteDatabase.rawQuery(sql, null);
        cursor.moveToFirst();
        do {
            caminhoImagem = (cursor.getString(0));
            caminhoAudio = (cursor.getString(1));
        }while(cursor.moveToNext());

        //coloca a imagem do caminho selecionado
        File imgFile = new File(caminhoImagem);
        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);
        }

        btPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(caminhoAudio);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(TelaVisualizacao.this, "Tocando",
                            Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Toast.makeText(TelaVisualizacao.this, "Erro ao encontrar o áudio",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
        sqLiteDatabase.close();
    }
}
