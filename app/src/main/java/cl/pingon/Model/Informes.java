package cl.pingon.Model;

public class Informes {
    String title;
    String subtitle;

    public Informes(String title, String subtitle){
        this.title = title;
        this.subtitle = subtitle;
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
}
