package com.example.btssio.projet3_gamba;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import java.util.List;


public class AfficheListeVisite extends AppCompatActivity {

    private List<Patient> listePatient;
    private ListView listView;
    private List<Visite> listeVisite;
    protected Modele vmodele;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_liste_visite);

        vmodele=new Modele();
        listeVisite = vmodele.listeVisite();

        Toast.makeText(this.getApplicationContext(), vmodele.countPatient().toString(), Toast.LENGTH_LONG).show();

        listView = (ListView)findViewById(R.id.lvListe);
        VisiteAdapter visiteAdapter = new VisiteAdapter(this, listeVisite);
        listView.setAdapter(visiteAdapter);

        // Si on clique sur un patient alors affichage de AfficheVisite
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                try{

                    Toast.makeText(getApplicationContext(), "Clic sur élément de List", Toast.LENGTH_LONG).show();
                    Intent AfficheUneVisite = new Intent(AfficheListeVisite.this, com.example.btssio.projet3_gamba.AfficheVisite.class);
                    AfficheUneVisite.putExtra("idVisite", listeVisite.get(position).getId());

                    startActivity(AfficheUneVisite);


                }catch( Exception e)
                {

                    AlertDialog alertMsg = new AlertDialog.Builder(AfficheListeVisite.this).create();
                    alertMsg.setTitle("Erreur");
                    alertMsg.setMessage(e.getMessage());
                    alertMsg.show();

                }

            }
        });


    }



}
