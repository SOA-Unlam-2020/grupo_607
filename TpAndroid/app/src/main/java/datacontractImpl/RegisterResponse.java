package datacontractImpl;

import datacontractDomain.SOAResponse;
import domain.User;

public class RegisterResponse extends SOAResponse {

    private User user;

    public RegisterResponse() {
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "state='" + this.getState() + '\'' +
                ", env='" + this.getEnv() + '\'' +
                ", token='" + this.getToken() + '\'' +
                ", msg='" + this.getMsg() + '\'' +
                '}';
    }
}
