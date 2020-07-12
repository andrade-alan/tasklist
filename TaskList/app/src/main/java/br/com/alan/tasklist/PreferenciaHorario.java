package br.com.alan.tasklist;

public class PreferenciaHorario{

    public static int obterDia(String data) {
        String[] fragmentos = data.split("/");
        return Integer.parseInt(fragmentos[0]);
    }

    public static int obterMes(String data) {
        String[] fragmentos = data.split("/");
        return Integer.parseInt(fragmentos[1])-1;
    }

    public static int obterAno(String data) {
        String[] fragmentos = data.split("/");
        return Integer.parseInt(fragmentos[2]);
    }

    public static int obterHora(String tempo) {
        String[] fragmentos = tempo.split(":");
        return Integer.parseInt(fragmentos[0]);
    }

    public static int obterMinuto(String tempo) {
        String[] fragmentos = tempo.split(":");
        return Integer.parseInt(fragmentos[1]);
    }
}