package com.example.btssio.projet3_gamba;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.db4o.foundation.Runnable4;

import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.util.Log;


public class ActImport extends AppCompatActivity {


    private String[] mesparams = null;
    private AsyncTask<String, String, Boolean> mThreadCon = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_import);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button bImp = (Button) findViewById(R.id.btnImp);


        bImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast unToast = Toast.makeText(getApplicationContext(), "Erreur ! Problème d'importation", Toast.LENGTH_SHORT);
                Toast.makeText(getApplicationContext(), "clic sur Import", Toast.LENGTH_SHORT
                ).show();

                String url = "http://www.btssio-carcouet.fr/ppe4/public/mesvisites/3";
                url = ((String) url);
                String[] imp = {url};
                mesparams = imp;
                mThreadCon = new Async(ActImport.this).execute(mesparams);
            }

        });

    }

    public void alertmsg(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setMessage(msg)
                .setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.
                TYPE_SYSTEM_ALERT);
        dialog.show();
    }


    public void retourImport(StringBuilder sb) {
        //alertmsg("retour Connexion", sb.toString());
        ArrayList<Integer> lesPatients = new ArrayList<Integer>();

        try {
            Modele vmodel = new Modele();
            JsonElement json = new JsonParser().parse(sb.toString());
            JsonArray varray = json.getAsJsonArray();
            Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss"
            ).create();

            ArrayList<Visite> listeVisite = new ArrayList<Visite>();

            for (JsonElement obj : varray) {
                Visite visite = gson.fromJson(obj.getAsJsonObject(), Visite.class);
                visite.setCompte_rendu_infirmiere("");
                visite.setDate_reelle(visite.getDate_prevue());
                listeVisite.add(visite);

                if(!lesPatients.contains(visite.getPatient())){
                        lesPatients.add(visite.getPatient());
                    }

            }

            vmodel.deleteVisite();
            vmodel.addVisite(listeVisite);
            vmodel.deletePatient();

            //recherche patients pour les visites
            for (Integer p : lesPatients) {
                String[] mesparams = {"http://www.btssio-carcouet.fr/ppe4/public/personne/".concat(p.toString()), "Patient"};
                mThreadCon = new AsyncPatient().execute(mesparams);
            }
            vmodel.deleteVisiteSoin();
            for (Visite v : listeVisite) {
                String[] mesparams = {"http://www.btssio-carcouet.fr/ppe4/public/visitesoins/".concat(Integer.toString(v.getId())), "VisiteSoin"};
                mThreadCon = new AsyncPatient().execute(mesparams);
            }
            if (vmodel.listeSoin().size()==0) {

                String[] mesparams = {"http://www.btssio-carcouet.fr/ppe4/public/soins/", "Soin"};
                mThreadCon = new AsyncPatient().execute(mesparams);
            }

            alertmsg("Retour", "Vos informations ont bien été importé avec succès !");
        } catch (Exception e) {

            alertmsg("Erreur retour import", e.getMessage());

        }


    }

    private class DateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                return null;
            }
        }
    }
    public void retourImportPlus(StringBuilder sb, String demande) {

        Modele vmodel = new Modele();
        // si je reçois Patient
        if( demande.equals("Patient"))
        {
            JsonElement json = new JsonParser().parse(sb.toString());
            JsonArray varray = json.getAsJsonArray();
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateDeserializer()).serializeNulls().create();


            ArrayList<Patient> listePatient = new ArrayList<Patient>();

            for (JsonElement obj : varray) {
                Patient unpatient = gson.fromJson(obj.getAsJsonObject(), Patient.class);


                listePatient.add(unpatient);
              }
              vmodel.addPatient(listePatient);
        }

            // si je reçois Visite
        if (demande.equals("VisiteSoin")) {

                JsonElement json = new JsonParser().parse(sb.toString());
                JsonArray varray = json.getAsJsonArray();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss"
                ).create();

                ArrayList<VisiteSoin> listeVisiteSoin = new ArrayList<VisiteSoin>();

                for (JsonElement obj : varray) {
                    VisiteSoin visite = gson.fromJson(obj.getAsJsonObject(), VisiteSoin.class);
                    listeVisiteSoin.add(visite);

                }
                vmodel.addVisiteSoin(listeVisiteSoin);
            }

            // Si je reçois Soin

            if( demande.equals("Soin")){

                JsonElement json = new JsonParser().parse(sb.toString());
                JsonArray varray = json.getAsJsonArray();
                Gson gson = new GsonBuilder().setDateFormat("yyyy-mm-dd HH:mm:ss"
                ).create();

                ArrayList<Soin> listeSoin = new ArrayList<Soin>();

                for (JsonElement obj : varray) {
                    Soin soin = gson.fromJson(obj.getAsJsonObject(), Soin.class);
                    listeSoin.add(soin);

                }
                vmodel.addSoin(listeSoin);
            }





   }


        public class AsyncPatient extends AsyncTask<String, String, Boolean> {
        private StringBuilder stringBuilder = new StringBuilder();
        private String demande;

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                retourImportPlus(stringBuilder, demande);
            } else {
                Toast.makeText(getApplicationContext(), "Fin ko retour patient",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {// Exécution en arrière plan
            String vurl = "";
            vurl = params[0];
            demande = params[1];
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(vurl);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
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

        @Override
        protected void onProgressUpdate(String... param) {
            // utilisation de on progress pour afficher des message pendant le doInBackground
            //Toast.makeText(getApplicationContext(), param[1].toString(),Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onCancelled() {
            //Toast.makeText(getApplicationContext(), "Annulation patient", Toast.LENGTH_SHORT).show();
        }
    }


}


        //json array c'est quand on a plusieurs objet json ( tableaux )
        // Permet d'afficher toutes les infos du JsonObject
        // alertmsg("retour Connexion", sb.toString());





