package cl.pingon.Model;

public class ModelTabsItem {

    String title;
    String total_string;
    String obligatorios_string;
    int check;

    public ModelTabsItem(String title, String total_string, String obligatorios_string, int check){
        this.title = title;
        this.total_string = total_string;
        this.obligatorios_string = obligatorios_string;
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public String getTotal_string() {
        return total_string;
    }

    public String getObligatorios_string() {
        return obligatorios_string;
    }

    public int getCheck() {
        return check;
    }
}
