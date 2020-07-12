package br.com.alan.tasklist;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;


public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.w("BRODCAST",intent.getAction());
            context.startService(new Intent(context, BancoDeDados.class));
        }
    }

    public static void configurarAlarme(Context contexto, String data, String hora, String id) {
        Log.w("BRODCAST", data + " " + hora + " " + id);
        AlarmManager gerenciador = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.DAY_OF_MONTH, PreferenciaHorario.obterDia(data));
        cal.set(Calendar.MONTH, PreferenciaHorario.obterMes(data));
        cal.set(Calendar.YEAR, PreferenciaHorario.obterAno(data));

        cal.set(Calendar.HOUR_OF_DAY, PreferenciaHorario.obterHora(hora));
        cal.set(Calendar.MINUTE, PreferenciaHorario.obterMinuto(hora));
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (cal.getTimeInMillis() < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        gerenciador.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),obterIntentPendente(contexto, id));
    }

    public static void cancelarAlarme(Context contexto,String id) {
        AlarmManager gerenciador = (AlarmManager) contexto.getSystemService(Context.ALARM_SERVICE);
        gerenciador.cancel(obterIntentPendente(contexto, id));
    }

    private static PendingIntent obterIntentPendente(Context contexto, String id) {
        int ident = Integer.parseInt(id);
        Intent i = new Intent(contexto, ReceptorAlarme.class);
        i.putExtra("id", id);
        return PendingIntent.getBroadcast(contexto, ident, i, 0);
    }

}