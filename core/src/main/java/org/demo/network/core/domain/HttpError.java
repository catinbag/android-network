package org.demo.network.core.domain;

/**
 * The server answered with a non-2xx code. The screen sees this class,
 * not a Retrofit class. {@code serverMessage} can be null (the body was not JSON).
 */
public class HttpError extends RuntimeException {
    private final int code;
    private final String serverMessage;

    public HttpError(int code, String serverMessage) {
        super(serverMessage != null ? serverMessage : "HTTP " + code);
        this.code = code;
        this.serverMessage = serverMessage;
    }

    public int getCode() {
        return code;
    }

    /** Text from the server body, or null. */
    public String getServerMessage() {
        return serverMessage;
    }
}
