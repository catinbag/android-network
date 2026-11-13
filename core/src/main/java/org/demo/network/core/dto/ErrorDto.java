package org.demo.network.core.dto;

/** Dog CEO error body: {"status":"error","message":"Breed not found …","code":404}. */
public class ErrorDto {
    public String status;
    public String message;
    public int code;
}
