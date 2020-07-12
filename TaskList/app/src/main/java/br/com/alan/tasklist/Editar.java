package br.com.alan.tasklist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Editar extends AppCompatActivity {

    Button btAtualizar;
    EditText etTrabalho2, etDesc2,etEditData,etEditHoraAl,etEditHoraTar;
    String idEnv[];
    String id;
    AlarmManagerBroadcastReceiver alarm;
    private SpeechRecognizer stt;
    ImageButton btVoz, btVoz2;
    String dataComp, horaComp, horaAlComp;

    final Calendar myCalendar = Calendar.getInstance();
    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = myCalendar.get(Calendar.MINUTE);
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);

        Intent extra = getIntent();
        id = extra.getStringExtra("id");

        idEnv = new String[] {id};
        open();
        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas",null,"id = ?", idEnv, null,null,null,null);
        BancoDeDados.cursor.moveToFirst();
        btAtualizar = (Button) findViewById(R.id.btAtualizar);
        etTrabalho2 = (EditText) findViewById(R.id.etTrabalho2);
        etDesc2 = (EditText) findViewById(R.id.etDesc2);
        etEditData = (EditText) findViewById(R.id.etEditData);
        etEditHoraAl = (EditText) findViewById(R.id.etEditHoraAl);
        etEditHoraTar = (EditText) findViewById(R.id.etEditHoraTar);
        btVoz = (ImageButton) findViewById(R.id.voz3);
        btVoz2 = (ImageButton) findViewById(R.id.voz4);



        etTrabalho2.setText(BancoDeDados.cursor.getString(1));
        etDesc2.setText(BancoDeDados.cursor.getString(5));
        etEditData.setText(BancoDeDados.cursor.getString(2));
        etEditHoraAl.setText(BancoDeDados.cursor.getString(3));
        etEditHoraTar.setText(BancoDeDados.cursor.getString(4));

        etEditData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Editar.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEditHoraAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Editar.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String min = String.valueOf(minute);
                                if(minute < 10){
                                    min = "0"+String.valueOf(minute);
                                }
                                etEditHoraAl.setText(hourOfDay + ":" + min);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        etEditHoraTar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Editar.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String min = String.valueOf(minute);
                                if(minute < 10){
                                    min = "0"+String.valueOf(minute);
                                }
                                etEditHoraTar.setText(hourOfDay + ":" + min);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        btAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BancoDeDados.banco.close();
                gravaAtualizacao();
            }
        });

        btVoz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stt = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                stt.setRecognitionListener(new BaseRecognitionListener() {
                    public void onResults(Bundle results) {
                        super.onResults(results);
                        // Recupera as possivels palavras que foram pronunciadas
                        ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        etTrabalho2.setText(""+words.get(0));
                    }
                });
                Intent intent = getRecognizerIntent();
                stt.startListening(intent);

            }
        });

        btVoz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stt = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                stt.setRecognitionListener(new BaseRecognitionListener() {
                    public void onResults(Bundle results) {
                        super.onResults(results);
                        // Recupera as possivels palavras que foram pronunciadas
                        ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                        etDesc2.setText(""+words.get(0));
                    }
                });
                Intent intent = getRecognizerIntent();
                stt.startListening(intent);

            }
        });
    }
    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.GERMANY);

        etEditData.setText(sdf.format(myCalendar.getTime()));
    }

    protected Intent getRecognizerIntent() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Fale algo");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 10);
        return intent;
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

    public void gravaAtualizacao(){
        if(!(etTrabalho2.getText().toString().equals(""))) {
            open();
            dataComp = etEditData.getText().toString();
            horaAlComp = etEditHoraAl.getText().toString();
            horaComp = etEditHoraTar.getText().toString();
            ContentValues values = new ContentValues();
            values.put("trabalho", etTrabalho2.getText().toString());
            values.put("data", dataComp);
            values.put("horaAl", horaAlComp);
            values.put("hora", horaComp);
            values.put("descr", etDesc2.getText().toString());

            BancoDeDados.banco.update("tarefas", values, "id = ?", idEnv);
            alarm.configurarAlarme(getApplicationContext(),dataComp,horaAlComp, BancoDeDados.cursor.getString(0));
            BancoDeDados.banco.close();
            Toast.makeText(getApplicationContext(), "Tarefa Atualizada com Sucesso",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), TelaInicial.class);
            BancoDeDados.banco.close();
            finish();
            startActivity(i);

        }
        else{
            Toast.makeText(getApplicationContext(), "Você deve informar pelo menos o titulo do trabalho", Toast.LENGTH_SHORT).show();
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