package cl.pingon.Model;

public class ModelContadorTabs {
    int contador_total = 0;
    int contador_total_completados = 0;
    int contador_mandatorios = 0;
    int contador_mandatorios_completados = 0;
    int check = 0;

    public ModelContadorTabs(){

    }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getCheck() {
        return check;
    }

    public int getContador_mandatorios() {
        return contador_mandatorios;
    }

    public int getContador_mandatorios_completados() {
        return contador_mandatorios_completados;
    }

    public int getContador_total() {
        return contador_total;
    }

    public int getContador_total_completados() {
        return contador_total_completados;
    }

    public void setContador_mandatorios(int contador_mandatorios) {
        this.contador_mandatorios = contador_mandatorios;
    }

    public void setContador_mandatorios_completados(int contador_mandatorios_completados) {
        this.contador_mandatorios_completados = contador_mandatorios_completados;
    }

    public void setContador_total(int contador_total) {
        this.contador_total = contador_total;
    }

    public void setContador_total_completados(int contador_total_completados) {
        this.contador_total_completados = contador_total_completados;
    }
}
