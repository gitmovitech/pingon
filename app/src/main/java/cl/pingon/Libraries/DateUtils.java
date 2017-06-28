package cl.pingon.Libraries;

public class DateUtils {

    public DateUtils(){

    }

    private int ObtenerMinutos(String hora){
        String[] arr = hora.split(":");
        int minutos = Integer.parseInt(arr[0]) * 60;
        minutos += Integer.parseInt(arr[1]);
        return minutos;
    }

    public String HoursDiference(String hora_entrada, String hora_salida, Boolean hora_colacion){

        int minutos_entrada = ObtenerMinutos(hora_entrada);
        int minutos_salida = ObtenerMinutos(hora_salida);
        int minutos_diferencia = minutos_salida - minutos_entrada;
        int horas_diferencia = (int) Math.floor(minutos_diferencia/60);
        minutos_diferencia = minutos_diferencia % 60;

        if(hora_colacion) {
            return (horas_diferencia-1)+":"+minutos_diferencia;
        } else {
            return horas_diferencia+":"+minutos_diferencia;
        }
    }

}
