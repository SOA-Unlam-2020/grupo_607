package datacontractDomain;

public class SOAResponse {

    private String state;
    private String env;
    private String token;
    private String msg;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "SOAResponse{" +
                "state='" + state + '\'' +
                ", env='" + env + '\'' +
                ", token='" + token + '\'' +
                ", error='" + msg + '\'' +
                '}';
    }
}
