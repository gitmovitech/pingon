package cl.pingon.Model;

public class Informes {
    String title;
    String subtitle;
    int id;

    public Informes(String title, String subtitle, int id){
        this.title = title;
        this.subtitle = subtitle;
        this.id = id;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void  setSubtitle(String subtitle){
        this.subtitle = subtitle;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSubtitle(){
        return this.subtitle;
    }

    public int getId() {
        return this.id;
    }
}
