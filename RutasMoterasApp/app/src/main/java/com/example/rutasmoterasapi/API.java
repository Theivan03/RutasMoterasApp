package com.example.rutasmoterasapi;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


// Proporciona métodos para interactuar con el API RESTful.
public class API {
    public static void getPosts(String url, String token, UtilREST.OnResponseListener listener) {
        UtilREST.runQueryWithHeaders(UtilREST.QueryType.GET, url, token, listener);
    }

    public static void getPost(int id, String url, UtilREST.OnResponseListener listener) {
        if (id < 0) {
            throw new IllegalArgumentException("ID de post no válido");
        }
        UtilREST.runQuery(UtilREST.QueryType.GET, url + "/" + id, listener);
    }

    public static void postPost(JSONObject post, String url, UtilREST.OnResponseListener listener) {
        UtilREST.runQuery(UtilREST.QueryType.POST, url, post.toString(), listener);
    }

    public static void putPost(int id, JSONObject post, String url, UtilREST.OnResponseListener listener) {
        if (id < 0) {
            throw new IllegalArgumentException("ID de post no válido");
        }
        UtilREST.runQuery(UtilREST.QueryType.PUT, url + "/" + id, post.toString(), listener);
    }

    public static void deletePost(int id, String url, UtilREST.OnResponseListener listener) {
        if (id < 0) {
            throw new IllegalArgumentException("ID de post no válido");
        }
        UtilREST.runQuery(UtilREST.QueryType.DELETE, url + "/" + id, listener);
    }
}