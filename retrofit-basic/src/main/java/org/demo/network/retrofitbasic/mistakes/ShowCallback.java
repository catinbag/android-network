package org.demo.network.retrofitbasic.mistakes;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Callback for the mistakes screen. An exception that escapes from onResponse
 * would close the app on Android, so we catch it and show it.
 */
public abstract class ShowCallback<T> implements Callback<T> {
    private final MistakeMode.Reporter out;

    protected ShowCallback(MistakeMode.Reporter out) {
        this.out = out;
    }

    /** The code of onResponse. It can throw: that is the point of some modes. */
    protected abstract String handle(Response<T> response);

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        String head = "onResponse, HTTP " + response.code() + "\n";
        try {
            out.show(head + handle(response));
        } catch (RuntimeException e) {
            out.show(head + "exception inside onResponse:\n" + MistakeMode.describe(e));
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        out.show("onFailure\n" + MistakeMode.describe(t));
    }
}
