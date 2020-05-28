package datacontract;

public class LoginResponse {
    private String state;
    private String env;
    private String token;
    private String error;

    public LoginResponse() {
    }

    public LoginResponse(String state, String env, String token, String error) {
        this.state = state;
        this.env = env;
        this.token = token;
        this.error = error;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "state='" + state + '\'' +
                ", env='" + env + '\'' +
                ", token='" + token + '\'' +
                ", error='" + error + '\'' +
                '}';
    }
}
