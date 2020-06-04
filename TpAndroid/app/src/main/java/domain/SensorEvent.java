package domain;

/**
 * Clase que enviamos como body del método event de la API
 */
public class SensorEvent {

    private String env;
    private String type_events;
    private String state;
    private String description;

    public SensorEvent() {
    }

    public SensorEvent(String env, String type_events, String state, String description) {
        this.env = env;
        this.type_events = type_events;
        this.state = state;
        this.description = description;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
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

}
