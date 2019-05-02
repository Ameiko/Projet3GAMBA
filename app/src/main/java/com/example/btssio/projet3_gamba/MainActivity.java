package com.example.btssio.projet3_gamba;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.media.Image;
import android.opengl.Visibility;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.support.v4.app.Fragment;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.net.Uri; //Permet de faire fonctionner le implements BlankFragment
import android.support.v4.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.view.WindowManager;

import java.lang.annotation.Target;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import android.view.MenuInflater;


public class MainActivity  extends AppCompatActivity implements BlankFragment.OnFragmentInteractionListener {
    Fragment leFragment;
    ImageView limage;

    Menu lemenu;
    /*MenuItem export;
    MenuItem import1;
    MenuItem deco;
    MenuItem connect;
    MenuItem list;*/
    private boolean connexion = false;
    private String loginG;  // Création de Variable Login et Password qui vont recupérer ce que contient les editTexts pour le changement de préférences
    private String passwordG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermissionAlert();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if( MULTIPLE_PERMISSIONS == 10)
        {
        leFragment = (Fragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        leFragment.getView().setVisibility(View.GONE);
        limage= (ImageView) findViewById(R.id.imageView);
        Button bOk=(Button) leFragment.getView().findViewById(R.id.bFragOk);


        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etId = (EditText)leFragment.getView().findViewById(R.id.etFragId);
                EditText etPwd = (EditText)leFragment.getView().findViewById(R.id.etFragPassword);

                Toast unToast = Toast.makeText(getApplicationContext(), "Erreur ! L'identifiant et le Mot de Passe doivent avoir une longueur de 2 minimum !", Toast.LENGTH_SHORT);

                if( etId.getText().length() < 2 || etPwd.getText().length() <2)
                {
                    unToast.show();
                   closeContextMenu();
                }
                else
                {
                        EditText login=(EditText)findViewById(R.id.etFragId);
                        EditText pass=(EditText)findViewById(R.id.etFragPassword);
                        String[] mesparams = null;

                        loginG = login.getText().toString();
                        passwordG = pass.getText().toString();

                    //   ??? est ce que les identifiants correspondent au shared si oui on se connecte sinon appel d'url
                    if(etId.getText().toString().equals(getSharedPreferences("userdetails", MODE_PRIVATE).getString("login", "blbl")) && etPwd.getText().toString().equals(getSharedPreferences("userdetails", MODE_PRIVATE).getString("pass", "password")))
                    {
                        alertmsg("connexion", "Vous êtes bien connecté !");

                    }
                    else
                    {
 //---------------------------------------- // Afficher une message box Oui/Non


                       showAlertDialogButtonClicked( view);


                        String url="http://www.btssio-carcouet.fr/ppe4/public/connect2/";
                        url=((String) url).concat(login.getText().toString().trim()).concat("/").concat(pass.getText().toString().trim()).concat("/infirmiere");
                        String[] blbl = {url};
                        mesparams = blbl;
                        mThreadCon = new Async (MainActivity.this).execute(mesparams);
                    }
                    leFragment.getView().setVisibility(View.GONE);
                    limage.setVisibility(View.VISIBLE);




//---------------------------------------------------------------------------------------------------------

                    // Masquer le bouton Se connecter et afficher Se deconnecter

                    /*
                    *   Public void seDeconnecter
                    *
                    *
                    * */



 //---------------------------------------------FICHIER DE PREFERENCE----------------------------------------------------------\\
                    SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);

                    SharedPreferences.Editor edit = userDetails.edit() ;
                    edit.putString("login", login.getText().toString()); // On veut récupérer les DONNEES contenues dans les editTEXT
                    edit.putString("pass", getMd5Hash(pass.getText().toString()));
                    edit.commit();
                    connexion = true;
                    affiche();



//-------------------------------------------------------------------------------------------------------------------------------\\
                }


            }
        });


        }else {

            AlertDialog alertMsg = new AlertDialog.Builder(MainActivity.this).create();
            alertMsg.setTitle("ALERTE");
            alertMsg.setMessage("Les permissions refusées ne permettent pas d'utiliser l'application correctement");
            alertMsg.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
            { public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); } });
            alertMsg.show();
        }

        Button bCancel=(Button) leFragment.getView().findViewById(R.id.bFragCancel);
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                leFragment.getView().setVisibility(View.GONE); //masque le fragment
                limage.setVisibility(View.VISIBLE);


            }
        });
    }
//---------------------------------------------------Methode-Message Box----------------------------------------------------------
    public void showAlertDialogButtonClicked(View view) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Changement de Login");
        builder.setMessage("Attention ! Ce n'est pas le login habituel. Voulez vous enregistrer vos identifiants dans les préférences ?");
        // add the buttons
        builder.setPositiveButton("Oui, je souhaite enregistrer mes identifiants dans les préférences", new DialogInterface.OnClickListener()
        {

            @Override public void onClick(DialogInterface dialog, int which) {

            SharedPreferences userDetails = getSharedPreferences("userdetails", MODE_PRIVATE);
            SharedPreferences.Editor edit = userDetails.edit() ;


            edit.putString("login", loginG); // On veut récupérer les DONNEES de connexion saisies
            edit.putString("pass", passwordG );
            edit.commit();
            } });
        builder.setNegativeButton("Non, je ne souhaite pas enregistrer mes identifiants dans les préférences", null);
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show(); }

public void affiche()
    {
        // Ne fonctionne pas avec menu Items
        if(connexion) {

            lemenu.findItem(R.id.menu_connect).setVisible(false);
            lemenu.findItem(R.id.menu_list).setVisible(true);
            lemenu.findItem(R.id.menu_deconnect).setVisible(true);
            lemenu.findItem(R.id.menu_import).setVisible(true);
            lemenu.findItem(R.id.menu_export).setVisible(true);
        }
        else
        {
            lemenu.findItem(R.id.menu_connect).setVisible(true);
            lemenu.findItem(R.id.menu_list).setVisible(false);
            lemenu.findItem(R.id.menu_deconnect).setVisible(false);
            lemenu.findItem(R.id.menu_import).setVisible(false);
            lemenu.findItem(R.id.menu_export).setVisible(false);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        lemenu = menu;

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:

                leFragment.getView().setVisibility(View.VISIBLE);
                limage.setVisibility(View.GONE);


                return true;
            case R.id.menu_deconnect:
                leFragment.getView().setVisibility(View.GONE);
                limage.setVisibility((View.VISIBLE));
                connexion = false;
                affiche();

                return true;
            case R.id.menu_list:
                //afficher la vue activity_affiche_liste_visite

                Intent AfficheListe = new Intent(MainActivity.this, com.example.btssio.projet3_gamba.AfficheListeVisite.class);
                startActivity( AfficheListe);

                return true;
            case R.id.menu_import:
                // afficher la vue content_act_import

                Intent ActImport = new Intent(MainActivity.this, com.example.btssio.projet3_gamba.ActImport.class);
                startActivity( ActImport);

                return true;
            default:
                return false;
        }
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }
//methode pour accepter les alerts windows
    public static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 5469;
    public void checkPermissionAlert() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // on regarde quelle Activity a répondu

        switch (requestCode) {
            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        alertmsg("Permission ALERT","Permission OK");
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Pbs demande de permissions"
                                , Toast.LENGTH_SHORT).show();
                    }
                }

        }



    }
@Override
    public void onStart() {
        super.onStart();
        checkPermissionAlert();
        demanderPermission();
    }

//  GESTION DES PERMISSIONS

    private boolean permissionOK=false;
    private static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS};
    private List<String> listPermissionsNeeded;

    public void demanderPermission() {
        int result ;
        listPermissionsNeeded = new ArrayList<>();
        for (String p:permissions) {


//--------------------------------------------------------------------------------------------------
            /* On veut verifier que la permission est accordée */
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) // Ajout dref stack overflow
            { result = checkSelfPermission(p);
            } else { result = 0;}
//--------------------------------------------------------------------------------------------------

            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            for (String permission : listPermissionsNeeded) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission) == true) {
                    //Autorisations déja demandées et refusées
                    // explique pourquoi l'autorisation est nécessaire
                    new AlertDialog.Builder(this)
                            .setTitle("Permissions")
                            .setMessage("Les permissions internet et écriture sont nécessaires pour le bon fonctionnement de l'application")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    // si ok on demande l'autorisation
                                    ActivityCompat.requestPermissions(MainActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Toast.makeText(getApplicationContext(), "Nous ne pouvons pas continuer l'application car ces permissions sont nécéssaires",Toast.LENGTH_LONG).show();

                                    // Perform Your Task Here--When No is pressed
                                    dialog.cancel();
                                }
                            }).show();
                } else {
                    // autorisations jamais demandées on demande l'autorisation
                    ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
                }
            }
        }
        else {
            // toutes les permissions sont ok
            permissionOK=true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                    // Show permissionsDenied
                    if(permissionsDenied.length()>0) {
                        Toast.makeText(getApplicationContext(),    "Nous ne pouvons pas continuer l'application car ces permissions sont nécéssaires : \n"+permissionsDenied,Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        permissionOK=true;
                    }
                }
                return;
            }
        }
    }
    // appel internet
    private AsyncTask<String, String, Boolean> mThreadCon = null;
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



    public void retourConnexion(StringBuilder sb)
    {
        JSONObject unjsonObject=null;
        String nom = null;
        try {
            unjsonObject = new JSONObject(sb.toString());
        }catch (JSONException e){
            alertmsg("Erreur conversion JSON", "Erreur sur la consersion sb");
        }
        if(unjsonObject.has("status")){
            alertmsg("Erreur de Connexion !", "Votre identifiant et votre mot de passe sont incorrects !");
        }else{

            try {

                alertmsg("Connexion réussie !", "Bienvenue "+ unjsonObject.getString("nom") + unjsonObject.getString("prenom"));





            }catch (JSONException e){
                alertmsg("Erreur conversion JSON", "Erreur sur la consersion sb :" + nom);
            }
        }


        //json array c'est quand on a plusieurs objet json ( tableaux )
        // Permet d'afficher toutes les infos du JsonObject
       // alertmsg("retour Connexion", sb.toString());
    }


    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

}