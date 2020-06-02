package utils;

public enum StatusSensor {

    ACTIVO("Activo"),
    INACTIVO("Inactivo");

    private String status;

    StatusSensor(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
