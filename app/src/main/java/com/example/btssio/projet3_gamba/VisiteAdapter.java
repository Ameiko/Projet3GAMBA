package com.example.btssio.projet3_gamba;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.LayoutInflater;
import android.content.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.widget.TextView;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.example.btssio.projet3_gamba.R;
import com.example.btssio.projet3_gamba.Visite;


public class VisiteAdapter extends BaseAdapter {
    private List<Visite> listVisite;
    private HashMap<Integer,Patient> DicoPatient;

    private LayoutInflater layoutInflater; //Cet attribut a pour mission de charger notre fichier XML de la vue pour l'item.

    private DateFormat df = new DateFormat();

    @Override
    public int getCount() {
        return listVisite.size();
    }

    @Override
    public Object getItem(int position) {
        return listVisite.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    // Permet de mémoriser les éléments de la liste en mémoire
    private class ViewHolder {
        TextView textViewVisite;
        TextView textViewPatient;
        TextView textViewDate;
        TextView textViewDuree;
    }

    public VisiteAdapter(Context context, List<Visite> vListVisite) {
        super();
        layoutInflater = LayoutInflater.from(context);
        listVisite = vListVisite;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.vuevisite, null);
            holder.textViewVisite = (TextView) convertView.findViewById(R.id.vuevisite);
            holder.textViewPatient = (TextView) convertView.findViewById(R.id.vuepatient);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.vuedateprevue);
            holder.textViewDuree = (TextView) convertView.findViewById(R.id.vueduree);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.rgb(238, 233, 233));
        } else {
            convertView.setBackgroundColor(Color.rgb(255, 255, 255));
        }
        /*****Affichage des propriétés dans la ligne de la listView ****/

        Modele vmodel = new Modele();
        Patient p = vmodel.trouvePatient((listVisite.get(position).getPatient()));

        holder.textViewVisite.setText("Visite ID : " + listVisite.get(position).getId() + ", ");
        holder.textViewPatient.setText("Avec le patient : " + listVisite.get(position).getPatient() + ", ");



        // Nom et Prenom du patient ayant l'id correspondant a listVisite.get(position).getPatient()
        holder.textViewPatient.setText("Avec le patient : " + p.getPrenom()+ " " + p.getNom());

        holder.textViewDate.setText("Date :"+ df.format("dd/MM/yyyy",listVisite.get(position).getDate_reelle()).toString().concat(" à ").concat(df.format("HH:mm",listVisite.get(position).getDate_reelle()).toString()));
        holder.textViewDuree.setText("Durée : "+listVisite.get(position).getDuree()+" min");

        /********* COULEURS DU TEXTE DE LA LISTVIEW ******************/
        holder.textViewVisite.setTextColor(Color.BLACK);
        holder.textViewPatient.setTextColor(Color.BLACK);
        holder.textViewDate.setTextColor(Color.BLACK);
        holder.textViewDuree.setTextColor(Color.BLACK);


        /******* Taille du texte de la listView ********************/
        holder.textViewVisite.setTextSize(17);
        holder.textViewPatient.setTextSize(17);
        holder.textViewDate.setTextSize(17);
        holder.textViewDuree.setTextSize(17);


        return convertView;
    }




}
