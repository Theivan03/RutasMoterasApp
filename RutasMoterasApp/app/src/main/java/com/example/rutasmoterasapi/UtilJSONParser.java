package com.example.rutasmoterasapi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UtilJSONParser {

    // Formato: [ { "id": 1, "userId": 1, "title": "...", "body": "..." }, ... ]
    // Recibe una cadena JSON (formato anterior) que representa un array de objetos de post y devuelve una lista de objetos PostModel.
    public static List<RutasModel> parseArrayPosts(String strJson) {
        List<RutasModel> list = new ArrayList();
        try {
            JSONArray arrayPosts = new JSONArray(strJson);
            for(int i=0; i<arrayPosts.length(); i++) {
                list.add(parsePost(arrayPosts.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Formato: { "id": 1, "userId": 1, "title": "...", "body": "..." }
    // Recibe una cadena JSON (formato anterior) que representa un objeto de post y devuelve un objeto PostModel.
    public static RutasModel parsePost(String strJson) {
        RutasModel post = null;
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            post = new RutasModel(
                    jsonObject.optInt("id", -1),
                    jsonObject.optString("titulo", "").trim(),
                    jsonObject.optString("fecha_creacion", "").trim(),
                    jsonObject.optString("descripcion", "").trim(),
                    jsonObject.optString("comunidadAutonoma", "").trim(),
                    jsonObject.optString("tipoMoto", "").trim(),
                    jsonObject.optInt("userId", -1),
                    jsonObject.optString("imageURL", "").trim()
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post;
    }

    // Este método crea y devuelve un objeto JSONObject que representa un post con los valores proporcionados para "id", "userId", "title" y "body".
    // Utiliza el método put() para establecer los valores en el objeto JSON.
    public static JSONObject createPost(int id, int userId, String title, String body) {
        JSONObject jsonPost = new JSONObject();
        try {
            jsonPost.put("id", id);
            jsonPost.put("userId", userId);
            jsonPost.put("title", title);
            jsonPost.put("body", body);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonPost;
    }

    // Este constructor es privado y lanza un AssertionError.
    // Esto asegura que la clase no pueda ser instanciada, ya que todos los métodos son estáticos y la clase se utiliza como una utilidad.
    private UtilJSONParser() { throw new AssertionError(); }
}
