package br.com.alan.tasklist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
//Tela de Splash, inicial do aplicativo
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent it = new Intent(MainActivity.this,TelaInicial.class);//Código que leva
                startActivity(it);//Para a tela Seguinte
                finish();//Finaliza esta tela
            }
        },4000);//ela fica QUATRO segundos na tela, para poder ir para a tela de listagem de Tarefas
    }
}
