package com.example.rutasmoterasapi;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UtilJSONParser {

    // Formato: [ { "id": 1, "userId": 1, "title": "...", "body": "..." }, ... ]
    // Recibe una cadena JSON (formato anterior) que representa un array de objetos de post y devuelve una lista de objetos PostModel.
    public static List<RutasModel> parseArrayRutasPosts(String strJson) {
        List<RutasModel> list = new ArrayList();
        try {
            JSONArray arrayPosts = new JSONArray(strJson);
            for(int i=0; i<arrayPosts.length(); i++) {
                list.add(parsePostRuta(arrayPosts.get(i).toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static UserModel parseUserPosts(String strJson) {
        UserModel usuario = null;
        try {
            JSONObject arrayPosts = new JSONObject(strJson);
            usuario = parsePostUser(arrayPosts.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return usuario;
    }

    // Formato: { "id": 1, "userId": 1, "title": "...", "body": "..." }
    // Recibe una cadena JSON (formato anterior) que representa un objeto de post y devuelve un objeto PostModel.
    public static RutasModel parsePostRuta(String strJson) {
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

    public static UserModel parsePostUser(String strJson) {
        UserModel user = null;
        try {
            JSONObject jsonObject = new JSONObject(strJson);
            user = new UserModel();
            user.setId(jsonObject.optInt("id", -1));
            user.setUsername(jsonObject.optString("username", "").trim());
            user.setPassword(jsonObject.optString("password", "").trim());
            user.setName(jsonObject.optString("name", "").trim());
            user.setSurname(jsonObject.optString("surname", "").trim());
            user.setEmail(jsonObject.optString("email", "").trim());
            user.setCity(jsonObject.optString("city", "").trim());
            user.setPostalCode(jsonObject.optString("postalCode", "").trim());
            user.setImage(jsonObject.optString("image", "").trim());
            // Esta parte puede necesitar una conversión si LocalDateTime no se admite directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                user.setCreationDate(LocalDateTime.parse(jsonObject.optString("creationDate", "")));
            }
            // Suponiendo que "roles" es un array JSON de roles
            user.setRoles(jsonObject.optInt("roles", -1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
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
