package com.example.btssio.projet3_gamba;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import android.location.Geocoder;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.IOException;

import android.location.Criteria;

import android.graphics.drawable.Drawable;

import java.util.Locale;

import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;



import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.overlay.TilesOverlay;

import java.lang.Object;
import android.content.Context;

import android.preference.PreferenceManager;
import     java.io.File;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;

import org.osmdroid.tileprovider.MapTileProviderBasic;

public class Map extends AppCompatActivity implements LocationListener {

    private MapView myOpenMapView=null;
    private double positionClient_latt, positionClient_long, positionAgent_latt, positionAgent_long;
    private boolean reussiGeolocalisationAgent = false, reussiGeolocalisationClient = false;
    private String provider, adresseClient;
    private AsyncTask<String, String, Boolean> mThreadCon = null;
    private File appDir;
    private double minLat = Integer.MAX_VALUE;
    private double maxLat = Integer.MIN_VALUE;
    private double minLong = Integer.MAX_VALUE;
    private double maxLong = Integer.MIN_VALUE;
    private Context ctx;
    private MapTileProviderBasic mProvider;
    private TilesOverlay mTilesOverlay;
    private IMapController mapController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_basicmap);
        myOpenMapView = (MapView) findViewById(R.id.map);
        myOpenMapView.setTileSource(TileSourceFactory.MAPNIK);
        myOpenMapView.setMultiTouchControls(true);
        mapController = myOpenMapView.getController();
        Bundle b = getIntent().getExtras();
        adresseClient = b.getString("param1");
        Log.d("map", "adresse " + adresseClient);
        recupPositionClient();
        if (!reussiGeolocalisationClient) {
            try {
                adresseClient = URLEncoder.encode(adresseClient, "UTF-8");
                // FatalError

            } catch (java.io.UnsupportedEncodingException e1) {
            }
            String vurl = "http://maps.google.com/maps/api/geocode/json?address=" + adresseClient + ",france&sensor=false";
            String[] mesparams = {vurl};
            mThreadCon = new Async(Map.this).execute(mesparams);
        }
        recupPositionAgent();
        affiche();

        Modele vmodel = new Modele();

        Intent myIntent = new Intent(getApplicationContext(), Map.class);

        Bundle maValise = getIntent().getExtras();

        int idVisite = maValise.getInt("idVisite");
        Visite laVisite = vmodel.trouveVisite(idVisite);

        Patient p=vmodel.trouvePatient(laVisite.getPatient());
        myIntent.putExtra("param1", p.getAd1()+ " " + p.getCp()+ " " + p.getVille()+ " FRANCE");
        startActivity(myIntent);
    }
    public void affiche()
    {
        myOpenMapView.setUseDataConnection(false);
        if (reussiGeolocalisationClient) {

        //    Drawable icon = getResources().getDrawable(R.drawable.logo2);
            Marker marker = new Marker(myOpenMapView);
            marker.setTitle("Patient");
            GeoPoint g=new GeoPoint(positionClient_latt, positionClient_long);
            marker.setPosition(g);
            changeminmax(g);
            marker.setPosition(g);
            Log.d("map", "posiclientok- " + String.valueOf(positionClient_latt) + "/" + String.valueOf(positionClient_long));
        //    marker.setIcon(icon);
            myOpenMapView.getOverlays().add(marker);
        }
        if (reussiGeolocalisationAgent) {

        //    Drawable icon = getResources().getDrawable(R.drawable.pointer);
            Marker marker = new Marker(myOpenMapView);
            marker.setTitle("Vous");
            GeoPoint g=new GeoPoint(positionAgent_latt, positionAgent_long);
            marker.setPosition(g);
            changeminmax(g);
            Log.d("map", "posiAgentok- " + String.valueOf(positionAgent_latt) + "/" + String.valueOf(positionAgent_long));
          //  marker.setIcon(icon);
            myOpenMapView.getOverlays().add(marker);
        }
        Log.d("map", "min max- " + String.valueOf(minLat) + "-" + String.valueOf(maxLat)+ String.valueOf(minLong) + "-" + String.valueOf(maxLong));
        BoundingBox boundingBox = new BoundingBox(maxLat, maxLong, minLat, minLong);

        mapController.setCenter(boundingBox.getCenterWithDateLine());
        mapController.setZoom(18.0);
    }
    public void changeminmax(GeoPoint point)
    {
        if (point.getLatitude() < minLat)
            minLat = point.getLatitude();
        if (point.getLatitude() > maxLat)
            maxLat = point.getLatitude();
        if (point.getLongitude() < minLong)
            minLong = point.getLongitude();
        if (point.getLongitude() > maxLong)
            maxLong = point.getLongitude();
    }
    public void recupPositionClient() {
        if (!Geocoder.isPresent()) {
            Log.d("map", "geocoder absent ");
        } else {
            Log.d("map", "geocoder ok ");
            Geocoder fwdGeocoder = new android.location.Geocoder(this, Locale.FRANCE);
            List<Address> locations = null;
            try {
                locations = fwdGeocoder.getFromLocationName(adresseClient, 10);
            } catch (IOException e) {

                //"Pbs geocoder adresse client
            }
            if ((locations == null) || (locations.isEmpty())) {
                //"Adresse client inconnu !"

            } else {
                positionClient_latt = locations.get(0).getLatitude();
                positionClient_long = locations.get(0).getLongitude();
                reussiGeolocalisationClient = true;
            }
        }
    }

    public void recupPositionAgent() {
        LocationManager locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;

        } else {
            provider = locationManager.getBestProvider(criteria, false);
        }
        Log.d("map", "provider :" + provider.toString());
        if (!(provider == null || provider.equals(""))) {

            try {
                locationManager.requestLocationUpdates(provider, 2000, 0, this);
                Location location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    positionAgent_latt = location.getLatitude();
                    positionAgent_long = location.getLongitude();
                    reussiGeolocalisationAgent = true;
                } else {
                    provider = LocationManager.NETWORK_PROVIDER;
                    locationManager.requestLocationUpdates(provider, 0, 0, this);
                    location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        positionAgent_latt = location.getLatitude();
                        positionAgent_long = location.getLongitude();
                        reussiGeolocalisationAgent = true;
                    }
                    //"Erreur dans la géolocalisation"
                }
            } catch (SecurityException ex) {

            }

        } else {
            //géoloc impossible
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void alertmsg(String title, String msg) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());
        builder.setMessage(msg)
                .setTitle(title);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.
                TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    public void retourImport(StringBuilder sb) {

        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(sb.toString());
            } catch (JSONException e) {
                alertmsg("Pbs JSON builder adresse", sb.toString());
                return;
            }
            Double lon = new Double(0);
            Double lat = new Double(0);

            try {

                lon = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");
                positionClient_long=lon;
                positionClient_latt=lat;
                reussiGeolocalisationClient=true;
                Log.d("map", "posiclientok " + String.valueOf(positionClient_latt) + "/" + String.valueOf(positionClient_long));
                affiche();
            } catch (JSONException e) {
                alertmsg("Pbs JSON latt lon", jsonObject.toString());
                return;
            }

        } catch (Exception e) {
            alertmsg("Erreur retour import", e.getMessage());
        }

    }
    @Override

    public void onPause(){

        super.onPause();

        myOpenMapView.onPause();

    }



    @Override

    public void onResume(){

        super.onResume();

        myOpenMapView.onResume();

    }
}

