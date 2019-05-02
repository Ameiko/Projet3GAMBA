package com.example.btssio.projet3_gamba;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.content.Intent;

import org.w3c.dom.Text;
//import android.widget.

public class AfficheVisite extends AppCompatActivity {

    private TextView tdater;
    private Date ddatereelle = new Date();
    private Calendar myCalendar = Calendar.getInstance();
    private ArrayList<VisiteSoin>LesSoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affiche_visite);
        Bundle maValise = getIntent().getExtras();

        int idVisite = maValise.getInt("idVisite");
        final DateFormat df = new DateFormat();
        final Modele vmodel = new Modele();


        final Visite laVisite = vmodel.trouveVisite(idVisite);
        Patient lePatient = vmodel.trouvePatient(laVisite.getPatient());

        String NomP = lePatient.getNom();
        String PrenomP = lePatient.getPrenom();
        String AdresseP1 = lePatient.getAd1();
        Integer Cp = lePatient.getCp();
        String ville = lePatient.getVille();
        String telPort = lePatient.getTel_port();
        String telFixe = lePatient.getTel_fixe();



        // TextView.setText => Attention au format de date

        TextView tdatep = findViewById(R.id.visiteDatePrevue);
        tdatep.setText(df.format("dd MMMM yyyy",laVisite.getDate_prevue()).toString());
        tdater = findViewById(R.id.visiteDateReelle);
        tdater.setText(df.format("dd MMMM yyyy",laVisite.getDate_reelle()).toString());

        TextView tnom = findViewById(R.id.visiteNom);
        tnom.setText(NomP);
        TextView tprenom = findViewById(R.id.visitePrenom);
        tprenom.setText(PrenomP);
        TextView adr1 = findViewById(R.id.visitead1);
        adr1.setText(AdresseP1);
        TextView tcp = findViewById(R.id.visitecp);
        tcp.setText((Cp.toString()));
        TextView tville = findViewById(R.id.visiteville);
        tville.setText(ville);
        TextView telf = findViewById(R.id.visitenumfixe);
        telf.setText(telFixe);
        TextView telp = findViewById(R.id.visitenumport);
        telp.setText(telPort);


        // Pour chaque soin refaire une Liste
        LesSoins = vmodel.trouveSoinsUneVisite(idVisite);

        ListView lv = (ListView)findViewById(R.id.lvListeSoins);
        SoinAdapter soinAdapter = new SoinAdapter(this, LesSoins);
        lv.setAdapter(soinAdapter);


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tdater.setText(df.format("dd/MMMM/yyyy", myCalendar.getTime()).toString());
                ddatereelle = myCalendar.getTime();
            }
        };

        tdater.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AfficheVisite.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button bSave=(Button)this.findViewById(R.id.visitesave);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Sauvegarde du compte rendu à la date réelle et les soins réalisés par la classe soin Adapter
                laVisite.setDate_reelle(ddatereelle);
                // foreac pour chaque soins savegarde

                    for (VisiteSoin soin : LesSoins){
                        vmodel.saveVisiteSoin(soin);
                    }
                    vmodel.saveVisite(laVisite);
                 Toast.makeText(AfficheVisite.this,"Sauvegarde en cours", Toast.LENGTH_LONG).show();
              }
        });

        Button bMap=(Button)this.findViewById(R.id.visitemap);

                bMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view1){
                    try{
                        Toast.makeText(getApplicationContext(), "Clic sur Map", Toast.LENGTH_LONG).show();
                         Intent AfficheMap = new Intent(AfficheVisite.this, com.example.btssio.projet3_gamba.Map.class);
                         AfficheMap.putExtra("idVisite", laVisite.getId());

                         startActivity(AfficheMap);

                     }catch( Exception ez)
                     {
                         AlertDialog alertMsg = new AlertDialog.Builder(AfficheVisite.this).create();
                         alertMsg.setTitle("Erreur");
                         alertMsg.setMessage(ez.getMessage());
                          alertMsg.show();
                     }
                 }});

    }
}
