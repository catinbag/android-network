package org.demo.network.graphql;

import java.util.Map;

/** Body of the request: {"query": "...", "variables": {...}}. Gson skips null variables. */
public class GraphQlRequest {
    public final String query;
    public final Map<String, Object> variables;

    public GraphQlRequest(String query, Map<String, Object> variables) {
        this.query = query;
        this.variables = variables;
    }
}
