package org.demo.network.core.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Dog CEO: GET breed/{breed}/images and breed/{breed}/images/random/{count}.
 * Not for images/random without a number: there "message" is a string.
 */
public class ImagesResponse {
    @SerializedName("message")
    public List<String> imageUrls;
    public String status;
}
