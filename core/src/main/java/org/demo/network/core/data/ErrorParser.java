package org.demo.network.core.data;

import com.google.gson.Gson;

import org.demo.network.core.domain.HttpError;
import org.demo.network.core.dto.ErrorDto;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class ErrorParser {
    private final Gson gson;

    public ErrorParser(Gson gson) {
        this.gson = gson;
    }

    // 404 from Dog CEO: {"status":"error","message":"Breed not found …","code":404}
    public HttpError toError(Response<?> response) {
        String message = null;
        try (ResponseBody body = response.errorBody()) {
            if (body != null) message = gson.fromJson(body.charStream(), ErrorDto.class).message;
        } catch (RuntimeException e) {
            // the error body is not JSON (or empty): keep only the code
        }
        return new HttpError(response.code(), message);   // domain class, not Retrofit
    }
}
