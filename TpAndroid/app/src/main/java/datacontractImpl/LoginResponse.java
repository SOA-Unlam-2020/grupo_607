package datacontractImpl;

import datacontractDomain.SOAResponse;

public class LoginResponse extends SOAResponse {

    public LoginResponse() {
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "state='" + this.getState() + '\'' +
                ", env='" + this.getEnv() + '\'' +
                ", token='" + this.getToken() + '\'' +
                ", error='" + this.getMsg() + '\'' +
                '}';
    }
}
