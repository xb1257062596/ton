package CallFragmentPac;

/**
 * Created by 123 on 2016/12/22.
 */

public class Information  {

    private String name;
    private String phone;
    private String address;
    private String time;
    private String type;
    private String harass;

    public Information(String name, String phone, String address, String time, String type, String harass) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.time = time;
        this.type = type;
        this.harass = harass;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {

        return type;
    }


    public Information() {
    }

    public void setHarass(String harass) {
        this.harass = harass;
    }

    public String getHarass() {
        return harass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {

        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getTime() {
        return time;
    }

}
