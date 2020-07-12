package br.com.alan.tasklist;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class BancoDeDados extends Service{

    Handler updatePost;
    LooperThread loop = new LooperThread();
    static SQLiteDatabase banco = null;
    static Cursor cursor;
    AlarmManagerBroadcastReceiver alarm;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if(!(loop.isAlive())){

            loop.start();

        }
        else{

        }

        return START_STICKY;

    }

    class LooperThread extends Thread {

        public void run() {
            Looper.prepare();

            updatePost = new Handler();

            open();

            Looper.loop();
        }
    }


    public void bootCompleto(Context context){
        banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);
        cursor = banco.query("tarefas",null,null,null,null,null,null);
        if (cursor != null){
            cursor.moveToFirst();
            for(int i = 0; i < cursor.getCount(); i++){
                Log.w("BRODCAST", cursor.getString(1));
                String data = cursor.getString(2);
                String hora = cursor.getString(3);
                String id = cursor.getString(0);
                alarm.configurarAlarme(context, data,hora, id);
                cursor.moveToNext();
            }

        }else{
            Toast.makeText(context, "NAO EXISTE BANCO", Toast.LENGTH_SHORT).show();
            Log.w("BRODCAST", "NAO EXISTE BANCO");
        }
    }

    public void  open(){

        try{
            Log.w("BRODCAST", " BANCO DE DADOS");
            banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);
            String sql = "CREATE TABLE IF NOT EXISTS tarefas"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trabalho TEXT, data TEXT, horaAl TEXT, hora TEXT, descr TEXT);";
            BancoDeDados.banco.execSQL(sql);

            bootCompleto(getApplicationContext());

        }catch(Exception erro){
            //Toast.makeText(getApplicationContext(), "Erro ao abrir ou criar Banco de Dados", Toast.LENGTH_SHORT).show();
        }
    }
}

