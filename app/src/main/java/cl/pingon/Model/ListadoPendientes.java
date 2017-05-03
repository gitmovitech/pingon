package cl.pingon.Model;


public class ListadoPendientes {
    private int local_doc_id;
    private String cliente;
    private String obra;
    private String marca;
    private String equipo;
    private String serie;
    private String formulario_titulo;
    private String formulario_subtitulo;

    public ListadoPendientes(int local_doc_id, String cliente, String obra, String marca, String equipo, String serie, String formulario_titulo, String formulario_subtitulo){
        this.local_doc_id = local_doc_id;
        this.cliente = cliente;
        this.obra = obra;
        this.marca = marca;
        this.equipo = equipo;
        this.serie = serie;
        this.formulario_titulo = formulario_titulo;
        this.formulario_subtitulo = formulario_subtitulo;
    }

    public int getLocal_doc_id() {
        return local_doc_id;
    }

    public String getCliente() {
        return cliente;
    }

    public String getEquipo() {
        return equipo;
    }

    public String getMarca() {
        return marca;
    }

    public String getFormulario_subtitulo() {
        return formulario_subtitulo;
    }

    public String getFormulario_titulo() {
        return formulario_titulo;
    }

    public String getObra() {
        return obra;
    }

    public String getSerie() {
        return serie;
    }

}
