package org.demo.network.core.domain;

/** Result of an asynchronous request. A cancelled request calls neither method. */
public interface ResultCallback<T> {
    void onSuccess(T data);

    /** {@code error} is an {@link HttpError} for a non-2xx answer, otherwise the original failure. */
    void onError(Throwable error);
}
