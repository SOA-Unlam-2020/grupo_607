package datacontractImpl;

import datacontractDomain.SOAResponse;
import domain.User;

/**
 * Clase creada para la respuesta de la API/register extiende de SOAResponse
 */
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
