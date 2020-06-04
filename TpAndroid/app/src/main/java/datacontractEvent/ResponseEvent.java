package datacontractEvent;

/**
 * Clase creada para respuesta de la API de event, es el array que nos responde en formato json
 */
public class ResponseEvent {
    private String type_events;
    private String state;
    private String description;
    private Integer group;

    public ResponseEvent() {
    }

    public String getType_events() {
        return type_events;
    }

    public void setType_events(String type_events) {
        this.type_events = type_events;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getGroup() {
        return group;
    }

    public void setGroup(Integer group) {
        this.group = group;
    }

}
