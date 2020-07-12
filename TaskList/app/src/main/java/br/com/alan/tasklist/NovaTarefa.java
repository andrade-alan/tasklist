package br.com.alan.tasklist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

public class NovaTarefa extends Activity{

    private AlarmManagerBroadcastReceiver alarm;
    Button btAdicionar;
    private SpeechRecognizer stt;
    ImageButton btVoz, btVoz2;
    EditText etTrabalho,etDesc,etData,etHoraAl,etHoraTar;
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
        setContentView(R.layout.activity_nota_tarefa);

        alarm = new AlarmManagerBroadcastReceiver();
        btAdicionar = (Button) findViewById(R.id.btAdicionar);
        btVoz = (ImageButton) findViewById(R.id.voz1);
        btVoz2 = (ImageButton) findViewById(R.id.voz2);
        etTrabalho = (EditText) findViewById(R.id.etTrabalho);
        etDesc = (EditText) findViewById(R.id.etDesc);
        etData = (EditText) findViewById(R.id.etData);
        etHoraAl = (EditText) findViewById(R.id.etHoraAl);
        etHoraTar = (EditText) findViewById(R.id.etHoraTar);

        etData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(NovaTarefa.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etHoraAl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(NovaTarefa.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String min = String.valueOf(minute);
                                if(minute < 10){
                                    min = "0"+String.valueOf(minute);
                                }
                                etHoraAl.setText(hourOfDay + ":" + min);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        etHoraTar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(NovaTarefa.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String min = String.valueOf(minute);
                                if(minute < 10){
                                    min = "0"+String.valueOf(minute);
                                }
                                etHoraTar.setText(hourOfDay + ":" + min);
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        btAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dataComp = etData.getText().toString();
                horaAlComp = etHoraAl.getText().toString();
                horaComp = etHoraTar.getText().toString();
                gravaTarefa();

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
                        etTrabalho.setText(""+words.get(0));
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
                        etDesc.setText(""+words.get(0));
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

        etData.setText(sdf.format(myCalendar.getTime()));
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

    public void gravaTarefa(){

        if(!(etTrabalho.getText().toString().equals(""))) {


            String sql = "INSERT INTO tarefas (trabalho, data, horaAl, hora, descr) VALUES('"
                    + etTrabalho.getText().toString() + "' , '"
                    + dataComp + "' , '"
                    + horaAlComp + "' , '"
                    + horaComp + "' , '"
                    + etDesc.getText().toString() + "');";

            BancoDeDados.banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);

            String sql2 = "CREATE TABLE IF NOT EXISTS tarefas"
                    + " (id INTEGER PRIMARY KEY AUTOINCREMENT, trabalho TEXT, data TEXT, horaAl TEXT, hora TEXT, descr TEXT);";
            BancoDeDados.banco.execSQL(sql2);

            BancoDeDados.banco.execSQL(sql);
            BancoDeDados.banco.close();

            BancoDeDados.banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);
            BancoDeDados.cursor = BancoDeDados.banco.query("tarefas",null,null,null,null,null,null);
            BancoDeDados.cursor.moveToLast();
            alarm.configurarAlarme(getApplicationContext(),dataComp,horaAlComp, BancoDeDados.cursor.getString(0));

            BancoDeDados.banco.close();
            Toast.makeText(getApplicationContext(), "Tarefa Criada com Sucesso",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getApplicationContext(), TelaInicial.class);
            startActivity(i);
            finish();
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
    protected void onStart(){
        super.onStart();
    }

}
