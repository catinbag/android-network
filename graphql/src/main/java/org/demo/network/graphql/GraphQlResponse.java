package org.demo.network.graphql;

import java.util.List;

/**
 * Answer: {"data": ..., "errors": [...]}. The shape of "data" depends on the query,
 * so the type T is a JsonObject. An error can come with HTTP 200 (Countries)
 * or with HTTP 400 (GraphQL Zero): then it is in errorBody(), not in body().
 */
public class GraphQlResponse<T> {
    public T data;
    public List<GraphQlError> errors;
}
