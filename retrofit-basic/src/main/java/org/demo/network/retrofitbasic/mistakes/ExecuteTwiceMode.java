package org.demo.network.retrofitbasic.mistakes;

import org.demo.network.core.dto.BreedListResponse;
import org.demo.network.retrofitbasic.NetworkModule;

import retrofit2.Call;

/** Mistake 12: one Call runs once. The second enqueue throws IllegalStateException. */
public class ExecuteTwiceMode extends MistakeMode {
    public ExecuteTwiceMode() {
        super("Execute twice");
    }

    @Override
    public void run(boolean fix, Reporter out) {
        Call<BreedListResponse> call = NetworkModule.get().dogApi().getBreeds();
        call.enqueue(countBreeds(out));
        (fix ? call.clone() : call).enqueue(countBreeds(out));   // the fix: clone()
    }
}
