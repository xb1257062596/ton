package LianFragmentPac;

/**
 * Created by 123 on 2016/12/23.
 */

public class LianXiRen  {
    private String name;
    private String firstName;

    public LianXiRen() {
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {

        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public LianXiRen(String firstName, String name) {

        this.name = name;
        this.firstName = firstName;
    }
}
