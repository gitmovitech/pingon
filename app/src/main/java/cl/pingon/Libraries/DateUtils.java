package cl.pingon.Libraries;

import java.util.Calendar;

public class DateUtils {

    public DateUtils(){

    }

    public int ObtenerMinutos(String hora){
        String[] arr = hora.split(":");
        int minutos = Integer.parseInt(arr[0]) * 60;
        minutos += Integer.parseInt(arr[1]);
        return minutos;
    }

    public String format(String hora){
        String[] arr = hora.split(":");
        return String.format("%02d",Integer.valueOf(arr[0]))+":"+String.format("%02d",Integer.valueOf(arr[1]));
    }

    public String MinutosHora(int minutos){
        int horas_diferencia = (int) Math.floor(minutos/60);
        int minutos_diferencia = minutos % 60;
        return format(horas_diferencia+":"+minutos_diferencia);
    }

    public String AproximarHora(String hora){
        int minutos = ObtenerMinutos(hora);
        int horas_diferencia = (int) Math.floor(minutos/60);
        int minutos_diferencia = minutos % 60;
        return format(horas_diferencia+":"+minutos_diferencia);
    }

    public String HoursDiference(String hora_entrada, String hora_salida, Boolean hora_colacion){

        int minutos_entrada = ObtenerMinutos(hora_entrada);
        int minutos_salida = ObtenerMinutos(hora_salida);
        int minutos_diferencia = minutos_salida - minutos_entrada;
        int horas_diferencia = (int) Math.floor(minutos_diferencia/60);
        minutos_diferencia = minutos_diferencia % 60;

        if(horas_diferencia >= 4) {
            return format((horas_diferencia-1)+":"+minutos_diferencia);
        } else {
            return format(horas_diferencia+":"+minutos_diferencia);
        }
    }

    public boolean isWeekend(String texto){
        boolean valor;
        try{
            String[] fecha_arr = texto.split("-");
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, Integer.parseInt(fecha_arr[2]));
            cal.set(Calendar.MONTH, Integer.parseInt(fecha_arr[1])-1);
            cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha_arr[0]));

            if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                valor = true;
            } else {
                valor = false;
            }
            return valor;
        } catch (Exception e){
            return false;
        }
    }

}
