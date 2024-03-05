package com.example.rutasmoterasapi;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/*
  Esta clase nos permite realizar una petición REST utilizando cualquiera de los 4 modos: GET, POST, PUT o DELETE
*/
public class UtilREST {

    // Interfaz OnResponseListener que define dos métodos: onSuccess() y onError(). Estos métodos se utilizarán para manejar la respuesta de la solicitud HTTP.
    public interface OnResponseListener {
        void onSuccess(Response r);
        void onError(Response r);
    }

    // Enumerador QueryType define los cuatro tipos de consultas posibles: GET, POST, PUT y DELETE.
    public enum QueryType {
        GET, POST, PUT, DELETE
    }

    // Clase Response que encapsula la información de la respuesta HTTP, como el código de respuesta, el contenido de la respuesta y cualquier excepción que pueda ocurrir durante la solicitud.
    public static class Response {
        public int responseCode = -1;
        public String content = null;
        public String exception = null;
    }

    // Clase Request que encapsula la información de la petición HTTP (consulta, url, listener y datos (si tuvieramos)).
    private static class Request {
        public QueryType type;
        public String url = null;
        public OnResponseListener callback = null;
        public String data = null;
        public Map<String, String> headers = null;
    }

    // MÉTODOS PARA LANZAR LAS CONSULTAS.
    // Acepta el tipo de consulta, una URL, datos opcionales y un OnResponseListener.
    // Este método instancia un objeto Request y ejecuta una tarea asíncrona PrvDownloadTask para realizar la solicitud HTTP.
    public static void runQuery(QueryType type, String strUrl, String data, OnResponseListener listener) {
        Request request = new Request();
        request.type = type;
        request.url = strUrl;
        request.callback = listener;
        request.data = data;
        new PrvDownloadTask().execute(request);
    }
    public static void runQuery(QueryType type, String strUrl, String data, OnResponseListener listener, Map<String, String> headers) {
        Request request = new Request();
        request.type = type;
        request.url = strUrl;
        request.callback = listener;
        request.data = data;
        request.headers = headers; // Almacenar los headers en el objeto Request
        new PrvDownloadTask().execute(request);
    }
    public static void runQuery(QueryType type, String strUrl, OnResponseListener listener) {
        runQuery(type, strUrl, null, listener);
    }

    // Clase interna que extiende AsyncTask y maneja la lógica de realizar la solicitud HTTP en segundo plano
    private static class PrvDownloadTask extends AsyncTask<Request, Void, Response> {
        private Request mRequest = null;

        //------------------------------------------------------------------------------------------
        // Creamos el hilo para realizar la petición. En esta funcion se construyen los 4 tipos
        // distintos de peticiones (GET, POST, PUT o DELETE), dependiendo de la configuración
        // recibida en el objeto Request.
        @Override
        protected Response doInBackground(Request... params) {
            HttpURLConnection http = null;
            Response response = new Response();

            try {
                mRequest = params[0];

                URL url = new URL(mRequest.url);
                http = (HttpURLConnection)url.openConnection();
                http.setRequestProperty("Accept", "application/json");

                switch (mRequest.type) {
                    case GET:   http.setRequestMethod("GET");   break;
                    case POST:  http.setRequestMethod("POST");  break;
                    case PUT:   http.setRequestMethod("PUT");   break;
                    case DELETE:  http.setRequestMethod("DELETE");  break;
                }
                Log.d(getClass().getName(), "Request " + mRequest.url);
                Log.d(getClass().getName(), " - Type " + http.getRequestMethod());

                // Post data
                if(mRequest.type == QueryType.POST || mRequest.type == QueryType.PUT) {
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Type", "application/json"); // Establecer el tipo de contenido como JSON
                    PrintWriter writer = new PrintWriter(http.getOutputStream());
                    writer.print( mRequest.data );
                    writer.flush();
                    Log.d(getClass().getName(), " - Output data: " + mRequest.data);
                }

                // Response...
                response.responseCode = http.getResponseCode();
                Log.d(getClass().getName(), " - Response code " + response.responseCode);

                // Read response content
                if( http.getResponseCode() == HttpURLConnection.HTTP_OK || response.responseCode == 201)
                    response.content = prv_readStream(http.getInputStream());
                else
                    response.content = prv_readStream(http.getErrorStream());
                Log.d(getClass().getName(), " - Response content " + response.content);
            }
            catch(Exception e) {
                e.printStackTrace();
                if( response.responseCode == -1 )
                    response.responseCode = 500;
                response.exception = e.getLocalizedMessage();
            }
            finally {
                if(http != null)
                    http.disconnect();
            }

            return response;
        }

        // Cuando finalice la descarga comprobamos si el resultado es correcto o no y llamamos a la función callback correspondiente (onSuccess o onError)
        // Se llama al método onSuccess() del OnResponseListener si la solicitud se completó correctamente, o al método onError() si se produjo algún error.
        @Override
        protected void onPostExecute(Response response) {
            if(response.responseCode == HttpURLConnection.HTTP_OK || response.responseCode == 201)  // Algunas APIs devuelven 201 cuando se añade correctamente
                mRequest.callback.onSuccess( response );
            else {
                if(response.exception == null)
                    response.exception = "Wrong server response";
                mRequest.callback.onError( response );
            }
        }

        // Maneja la lectura de la respuesta del servidor. Lee los datos del InputStream devuelto por la conexión HTTP (ya sea en el inputStream o en el errorStream)
        private String prv_readStream(InputStream in) {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = null;

            try {
                reader = new BufferedReader( new InputStreamReader( in, "UTF-8" ) );
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            }
            catch( Exception e ) {
                e.printStackTrace();
            }
            finally {
                if( reader != null ) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return sb.toString();
        }
    }
}
