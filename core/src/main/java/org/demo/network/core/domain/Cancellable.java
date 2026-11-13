package org.demo.network.core.domain;

/** A running request that the caller can stop. Domain does not know Retrofit. */
public interface Cancellable {
    void cancel();
}
