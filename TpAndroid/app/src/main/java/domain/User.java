package domain;

import java.io.Serializable;

public class User implements Serializable {

    private String env;
    private String name;
    private String lastname;
    private String dni;
    private String email;
    private String password;
    private String commission;
    private String group;

    public User() {
    }

    public User(String name, String lastname, String dni, String email, String password, String commission, String group) {
        this.name = name;
        this.lastname = lastname;
        this.dni = dni;
        this.email = email;
        this.password = password;
        this.commission = commission;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    @Override
    public String toString() {
        return "User{" +
                "env='" + env + '\'' +
                ", name='" + name + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dni='" + dni + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", commission='" + commission + '\'' +
                ", group='" + group + '\'' +
                '}';
    }
}
