package br.com.alan.tasklist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceptorAlarme extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String id = intent.getStringExtra("id");
        Intent i = new Intent(context, Alarme.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("ident", id);
        context.startActivity(i);
    }
}

