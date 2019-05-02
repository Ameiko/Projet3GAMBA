package com.example.btssio.projet3_gamba;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Async extends AsyncTask<String, String, Boolean> {
    // Référence à l'activité qui appelle
    private WeakReference<Activity> activityAppelante = null;
    private String classActivityAppelante;
    private StringBuilder stringBuilder = new StringBuilder();

    public Async(Activity pActivity) {
        activityAppelante = new WeakReference<Activity>(pActivity);
        classActivityAppelante = pActivity.getClass().toString();
    }
    @Override
    protected void onPreExecute() {// Au lancement, on envoie un message à l'appelant
        if (activityAppelante.get() != null)
            Toast.makeText(activityAppelante.get(), "Thread on démarre",
                    Toast.LENGTH_SHORT).show();
    }

    //@Override
    protected void onPostExecute(Boolean result) {
        if (activityAppelante.get() != null) {
            if (result) {
                Toast.makeText(activityAppelante.get(), "Fin ok",
                        Toast.LENGTH_SHORT).show();

//pour exemple on appelle une méthode de l'appelant qui va gérer la fin ok du thread

                if (classActivityAppelante.contains("MainActivity")) {
                    ((MainActivity) activityAppelante.get()).retourConnexion(stringBuilder);
                }
//Tentative de gestion du retour de la classe ActImport
                if(classActivityAppelante.contains("ActImport")){
                    ((ActImport) activityAppelante.get()).retourImport(stringBuilder);
                }
            } else
                Toast.makeText(activityAppelante.get(), "Fin ko",

                        Toast.LENGTH_SHORT).show();
        }

    }
    //@Override
    protected Boolean doInBackground(String... params) {// Exécution en arrière plan
        String vurl = "";
        vurl = params[0];

        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(vurl);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (!classActivityAppelante.contains("MainActivity") && !classActivityAppelante.contains("ActImport")) {
                urlConnection
                        .setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");//ou put ou delete selon le web service
                OutputStreamWriter out = new OutputStreamWriter(
                        urlConnection.getOutputStream());
                //selon l'activity appelante on peut passer des paramètres en JSON exemple util pour export
                /* if (classActivityAppelante.contains("ActImport")) {
                    // Création objet jsonn clé valeur

                    jsonParam.put("xxxx", xxxx);
                    jsonParam.put("xxxx", xxxx);
                    out.write(jsonParam.toString());
                    out.flush();
                }*/
                out.close();

            }
            else
            {
                urlConnection.setRequestMethod("GET");
            }


            urlConnection.setConnectTimeout(4000);


            // récupération du serveur
            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        urlConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
                br.close();
                    /*
                    String[] vstring0 = {"Reçu du serveur",
                            stringBuilder.toString()};

                    publishProgress(vstring0);
                    */
            } else {
                String[] vstring0 = {"Erreur",
                        urlConnection.getResponseMessage()};
                publishProgress(vstring0);
            }
        } catch (MalformedURLException e) {

            String[] vstring0 = {"Erreur", "Pbs url"};
            publishProgress(vstring0);
            return false;
        } catch (java.net.SocketTimeoutException e) {
            String[] vstring0 = {"Erreur", "temps trop long"};
            publishProgress(vstring0);
            return false;
        } catch (IOException e) {

            String[] vstring0 = {"Erreur", "Pbs IO->".concat(e.getMessage())};
            publishProgress(vstring0);
            return false;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }

        return true;
    }




   // @Override
    protected void onProgressUpdate(String... param) {
        AlertDialog alertmsg ;
        // utilisation de on progress pour afficher des message pendant le doInBackground
        if (classActivityAppelante.contains("MainActivity")) {

            ((MainActivity) activityAppelante.get()).alertmsg (
                    param[0].toString(), param[1].toString());

        }
    }


    @Override
    protected void onCancelled () {
        if(activityAppelante.get() != null)
            Toast.makeText(activityAppelante.get(), "Annulation", Toast.LENGTH_SHORT).show();
    }

}
