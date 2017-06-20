package cl.pingon.Libraries;

import android.util.Log;

import java.util.Date;

public class DateUtils {

    public DateUtils(){

    }

    public String HoursDiference(String hora_entrada, String hora_salida, Boolean hora_colacion){

        String[] hora_entrada_arr = hora_entrada.split(":");
        String[] hora_salida_arr = hora_salida.split(":");

        Date date_entrada = new Date();
        Date date_salida = new Date();

        date_entrada.setHours(Integer.parseInt(hora_entrada_arr[0]));
        date_entrada.setMinutes(Integer.parseInt(hora_entrada_arr[01]));

        date_salida.setHours(Integer.parseInt(hora_salida_arr[0]));
        date_salida.setMinutes(Integer.parseInt(hora_salida_arr[01]));

        long cantidad_horas = date_entrada.getTime() - date_salida.getTime();
        Date horas = new Date();
        horas.setTime(cantidad_horas);
        if(hora_colacion){
            return (23-horas.getHours()-1)+":"+horas.getMinutes();
        } else {
            return (23-horas.getHours())+":"+horas.getMinutes();
        }
    }
}
