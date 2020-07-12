package br.com.alan.tasklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

public class Visualizar extends AppCompatActivity {

    EditText etTrabalho3, etData3, etHorarioAl3,etHorario3, etDesc3;
    String idEnv[];
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar);
        Intent extra = getIntent();
        id = extra.getStringExtra("id");
        //Log.i("Editar",""+id);
        idEnv = new String[] {id};
        open();
        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas",null,"id = ?", idEnv, null,null,null,null);
        BancoDeDados.cursor.moveToFirst();

        etTrabalho3 = (EditText) findViewById(R.id.etTrabalho3);
        etData3 = (EditText) findViewById(R.id.etData3);
        etHorario3 = (EditText) findViewById(R.id.etHorario3);
        etHorarioAl3 = (EditText) findViewById(R.id.etHorarioAl3);
        etDesc3 = (EditText) findViewById(R.id.etDesc3);

        etTrabalho3.setText(BancoDeDados.cursor.getString(1));
        etTrabalho3.setEnabled(false);
        etData3.setText(BancoDeDados.cursor.getString(2));
        etData3.setEnabled(false);
        etHorarioAl3.setText(BancoDeDados.cursor.getString(3));
        etHorarioAl3.setEnabled(false);
        etHorario3.setText(BancoDeDados.cursor.getString(4));
        etHorario3.setEnabled(false);
        etDesc3.setText(BancoDeDados.cursor.getString(5));
        etDesc3.setEnabled(false);
        BancoDeDados.banco.close();
    }

    public void  open(){

        try{

            BancoDeDados.banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);
            String sql = "CREATE TABLE IF NOT EXISTS tarefas"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trabalho TEXT, data TEXT, horaAl TEXT, hora TEXT, descr TEXT);";
            BancoDeDados.banco.execSQL(sql);

        }catch(Exception erro){
            Toast.makeText(getApplicationContext(), "Erro ao abrir ou criar Banco de Dados", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean onKeyDown(int keyCode, KeyEvent service){
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                Intent i = new Intent(getApplicationContext(), TelaInicial.class);
                startActivity(i);
                finish();
                return true;

        }
        return false;
    }
}