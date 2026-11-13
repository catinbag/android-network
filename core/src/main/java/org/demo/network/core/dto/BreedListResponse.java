package org.demo.network.core.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/** Dog CEO: GET breeds/list/all. The DTO repeats the JSON shape. */
public class BreedListResponse {
    @SerializedName("message")
    public Map<String, List<String>> breeds;   // breed -> sub-breeds
    public String status;
}
