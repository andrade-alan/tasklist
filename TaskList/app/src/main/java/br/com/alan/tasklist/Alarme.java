package br.com.alan.tasklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AnalogClock;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Alarme extends AppCompatActivity {

    MediaPlayer mp;
    Button bt, btAdiar;
    TextView al;
    String where[], adiado = "";
    AnalogClock ac;
    String[] min = {"5 Minutos","15 Minutos","30 Minutos"};
    ArrayAdapter<String> opMin;
    Spinner spMin;
    AlarmManagerBroadcastReceiver alarm;
    int minuto = 0 ;
    int hora = 0;
    int sobra = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarme);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarme);

        Intent extra = getIntent();
        String id = extra.getStringExtra("ident");

        bt = (Button)findViewById(R.id.parar);
        btAdiar = (Button)findViewById(R.id.adiar);
        al = (TextView)findViewById(R.id.alarme);
        ac = (AnalogClock) findViewById(R.id.analogClock);

        spMin = (Spinner)findViewById(R.id.adiAl);
        opMin = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,min);
        spMin.setAdapter(opMin);


        open();
        where = new String[]{id};
        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas", null, "id = ?", where, null,null, null);
        BancoDeDados.cursor.moveToFirst();
        al.setText("" + BancoDeDados.cursor.getString(1) + "\nHora Tarefa:" + BancoDeDados.cursor.getString(4) + "\nDescrição:\n" + BancoDeDados.cursor.getString(5));
        mp = MediaPlayer.create(getApplicationContext(), R.raw.over_the_horizon);
        mp.start();

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
                BancoDeDados.banco.delete("tarefas", "id = ?", where);
                mp.stop();
                mp = null;
                BancoDeDados.banco.close();
                finish();
            }
        });

        btAdiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
                int ts = 0;
                switch (spMin.getSelectedItemPosition()){
                    case 0: hora = PreferenciaHorario.obterHora(BancoDeDados.cursor.getString(3));
                        minuto = PreferenciaHorario.obterMinuto(BancoDeDados.cursor.getString(3));

                        minuto = minuto + 5;
                        if(minuto >= 60){
                            hora = hora + 1;
                            sobra = minuto%60;
                            minuto = sobra;
                        }
                        if(minuto < 10){
                            adiado = String.valueOf(""+hora+":0"+minuto);
                        }
                        else{
                            adiado = String.valueOf(""+hora+":"+minuto);
                        }

                        break;
                    case 1: hora = PreferenciaHorario.obterHora(BancoDeDados.cursor.getString(3));
                        minuto = PreferenciaHorario.obterMinuto(BancoDeDados.cursor.getString(3));

                        minuto = minuto + 15;
                        if(minuto >= 60){
                            hora = hora + 1;
                            sobra = minuto%60;
                            minuto = sobra;
                        }
                        if(minuto < 10){
                            adiado = String.valueOf(""+hora+":0"+minuto);
                        }
                        else{
                            adiado = String.valueOf(""+hora+":"+minuto);
                        }

                        break;
                    case 2: hora = PreferenciaHorario.obterHora(BancoDeDados.cursor.getString(3));
                        minuto = PreferenciaHorario.obterMinuto(BancoDeDados.cursor.getString(3));

                        minuto = minuto + 30;
                        if(minuto >= 60){
                            hora = hora + 1;
                            sobra = minuto%60;
                            minuto = sobra;
                        }
                        if(minuto < 10){
                            adiado = String.valueOf(""+hora+":0"+minuto);
                        }
                        else{
                            adiado = String.valueOf(""+hora+":"+minuto);
                        }
                        break;
                }
                ContentValues values = new ContentValues();
                values.put("trabalho", BancoDeDados.cursor.getString(1));
                values.put("data", BancoDeDados.cursor.getString(2));
                values.put("horaAl", adiado);
                values.put("hora", BancoDeDados.cursor.getString(4));
                values.put("descr", BancoDeDados.cursor.getString(5));

                BancoDeDados.banco.update("tarefas", values, "id = ?", where);
                alarm.configurarAlarme(getApplicationContext(),BancoDeDados.cursor.getString(2),adiado, BancoDeDados.cursor.getString(0));
                BancoDeDados.banco.close();
                Toast.makeText(getApplicationContext(), "Alarme adiado "+spMin.getSelectedItem(), Toast.LENGTH_SHORT).show();
                mp.stop();
                mp = null;
                finish();
            }
        });
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
                return false;
        }
        return false;
    }
}