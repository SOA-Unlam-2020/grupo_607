package datacontractEvent;

public class RegisterEventResponse {
    private String state;
    private String env;
    private ResponseEvent event;
    private String msg;

    public RegisterEventResponse() {
    }

    public RegisterEventResponse(String state, String env, ResponseEvent event, String msg) {
        this.state = state;
        this.env = env;
        this.event = event;
        this.msg = msg;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public ResponseEvent getEvent() {
        return event;
    }

    public void setEvent(ResponseEvent event) {
        this.event = event;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
