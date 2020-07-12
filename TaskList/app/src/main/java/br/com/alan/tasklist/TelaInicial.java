package br.com.alan.tasklist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
//Tela de listagem de Tarefas
public class TelaInicial extends AppCompatActivity {

    private AlarmManagerBroadcastReceiver alarm;//Variável para pegar os dados dos alarmes
    private ArrayList<String> array;//Pegará a lista de tarefas
    private ArrayAdapter<String> ad;//Irá adaptar a lista de tarefas para aparecer na tela
    ListView lista;//componente que mostra a lista de tarefas
    Intent i;//Variável para percorrer as páginas
    String where[];//Variável que vai buscar no banco as tarefas cadastradas
    String posi;//Posição que foi clicada na lista
    AlertDialog alerta;//Mensagem que aparece no meio da tela
    TextView tv;//Texto que aparece caso a lista esteja vazia

    private int widgetId = -1;//Variável que irá saber se um Widget foi criado na tela inicial
    private SiteMonitorModel smm = null;//Monitor de Widget
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);

        alarm = new AlarmManagerBroadcastReceiver();//Inicia o alarme
        Intent service = new Intent(TelaInicial.this, BancoDeDados.class);//Inicia o Banco de Dados
        startService(service);

        widgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);//Verifica se a tela foi chamada por Widget
        smm = SiteMonitorModel.getWidgetData(TelaInicial.this, widgetId);//Verificação do Widget

        open();//Inicializa o Banco de dados criando a tabela de tarefas

        tv = (TextView)findViewById(R.id.texto);//inicializa o texto da tela

        lista = (ListView) findViewById(R.id.lista);//inicializa a lista
        array = getData();//busca os dados no banco e salva em um vetor
        ad = new ArrayAdapter<String>(TelaInicial.this, android.R.layout.simple_expandable_list_item_1, array);//adapta a lista
        lista.setAdapter(ad);//adiciona a lista no componente de visualização

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//caso clique em algum componente da lista
                if(lista.getSelectedItemId() != -1){
                    BancoDeDados.cursor.moveToPosition(position);
                    String pos = BancoDeDados.cursor.getString(0);
                    exemplo_lista_single(pos);
                }
            }
        });
        BancoDeDados.banco.close();
    }

    private void exemplo_lista_single(String pos) {         //Lista de itens
        ArrayList<String> itens = new ArrayList<String>();

        where = new String[]{pos};
        posi = pos;
        itens.add("Visualizar");
        itens.add("Editar");
        itens.add("Excluir");
        itens.add("Widget");
        //adapter utilizando um layout customizado (TextView)
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, itens);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Opções:");
        //define o diálogo como uma lista, passa o adapter.
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                switch (arg1){
                    case 0:
                        i = new Intent(getApplicationContext(), Visualizar.class);
                        i.putExtra("id", posi);
                        startActivity(i);
                        finish();
                        break;
                    case 1:
                        i = new Intent(getApplicationContext(), Editar.class);
                        i.putExtra("id", posi);
                        startActivity(i);
                        finish();
                        break;
                    case 2:

                        open();
                        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas", null, "id = ?", where, null,null, null);
                        BancoDeDados.cursor.moveToFirst();
                        AlertDialog.Builder msg = new AlertDialog.Builder(TelaInicial.this);
                        msg.setTitle("Excluir");
                        msg.setMessage("Você realmente deseja excluir esta tarefa?");
                        msg.setNegativeButton("Cancelar",null);
                        msg.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(),"ALARME Excluido com Sucesso" + BancoDeDados.cursor.getString(2)+"-"+ BancoDeDados.cursor.getString(3), Toast.LENGTH_SHORT).show();
                                alarm.cancelarAlarme(getApplicationContext(), BancoDeDados.cursor.getString(0));
                                BancoDeDados.banco.close();

                                open();
                                BancoDeDados.banco.delete("tarefas", "id = ?",where);
                                array = getData();
                                ad = new ArrayAdapter<String>(TelaInicial.this, android.R.layout.simple_expandable_list_item_1, array);
                                lista.setAdapter(ad);
                                Toast.makeText(getApplicationContext(),"Tarefa Excluida com Sucesso", Toast.LENGTH_SHORT).show();
                                BancoDeDados.banco.close();
                            }
                        });
                        msg.show();

                        break;
                    case 3:
                        if(widgetId != -1) {
                            saveInfo(posi);

                            SiteMonitorWidgetImpl.UpdateOneWidget(TelaInicial.this, widgetId);

                            // let the widgetprovider know we're done and happy
                            Intent ret = new Intent();
                            ret.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                            setResult(Activity.RESULT_OK, ret);
                            finish();
                            Toast.makeText(getApplicationContext(), "Widget Criado", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Você deve criar o Widget na tela inical do dispositivo", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }
                alerta.dismiss();
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    public ArrayList<String> getData(){
        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas", null, null, null, null,null, null);

        ArrayList<String> result = new ArrayList<String>();

        for(BancoDeDados.cursor.moveToFirst(); !BancoDeDados.cursor.isAfterLast(); BancoDeDados.cursor.moveToNext()){
            if(!BancoDeDados.cursor.getString(1).equals("")){
                result.add(BancoDeDados.cursor.getString(1));
            }
        }
        if(BancoDeDados.cursor != null){
            if(result.isEmpty()) {
                tv.setText("Nenhuma Tarefa Cadastrada");
            }
            else{
                tv.setText("");
            }
            BancoDeDados.banco.close();
            return result;
        }
        BancoDeDados.banco.close();
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), NovaTarefa.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    protected void onResume(){
        BancoDeDados.banco = openOrCreateDatabase("agenda2", MODE_PRIVATE, null);
        array = getData();
        ad = new ArrayAdapter<String>(TelaInicial.this, android.R.layout.simple_expandable_list_item_1, array);
        lista.setAdapter(ad);
        BancoDeDados.banco.close();
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        //Log.i(tag, "onDestroy");
        if (!isFinishing()) {
            // Log.i(tag,"canceling");
            Intent ret = new Intent();
            ret.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widgetId);
            setResult(Activity.RESULT_CANCELED,ret);    	}
    }

    private void saveInfo(String posi) {
        //Log.i(tag,"Saving site info");
        open();
        BancoDeDados.cursor = BancoDeDados.banco.query("tarefas", null, "id = ?", where, null,null, null);
        BancoDeDados.cursor.moveToFirst();
        String name = BancoDeDados.cursor.getString(1);
        String url = BancoDeDados.cursor.getString(2);
        String homepageUrl = BancoDeDados.cursor.getString(4);
        if (name.trim().length() == 0 || url.trim().length() == 0) {
            return;
        }

        //Log.i(tag,"About to save");
        // save data
        if (smm == null) {
            //Log.i(tag,"smm is null, create a new one here");
            smm = new SiteMonitorModel(name,url,homepageUrl,"UNKNOWN",SiteMonitorModel.getFormattedDate(),"nothing yet");
        }

        smm.setName(name);
        smm.setUrl(url);
        smm.setHomepageUrl(homepageUrl);

        SiteMonitorModel.saveWidgetData(getApplicationContext(), widgetId, smm);

    }
}
