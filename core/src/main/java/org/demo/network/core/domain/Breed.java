package org.demo.network.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** App model of a breed. Immutable. Does not know JSON or Retrofit. */
public final class Breed {
    private final String name;
    private final List<String> subBreeds;

    public Breed(String name, List<String> subBreeds) {
        this.name = name;
        this.subBreeds = subBreeds == null
                ? Collections.<String>emptyList()
                : Collections.unmodifiableList(new ArrayList<>(subBreeds));
    }

    public String getName() {
        return name;
    }

    public List<String> getSubBreeds() {
        return subBreeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Breed)) return false;
        Breed other = (Breed) o;
        return Objects.equals(name, other.name) && subBreeds.equals(other.subBreeds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, subBreeds);
    }

    @Override
    public String toString() {
        return "Breed{" + name + ", subBreeds=" + subBreeds + "}";
    }
}
