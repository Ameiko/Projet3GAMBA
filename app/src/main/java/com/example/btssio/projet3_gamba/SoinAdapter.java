package com.example.btssio.projet3_gamba;


import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.util.Log;
import java.util.ArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;

public class SoinAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<VisiteSoin> listSoin;
    private LayoutInflater layoutInflater; //Cet attribut a pour mission de charger notre fichier XML de la vue pour l'item.
    private DateFormat df = new DateFormat();
    private Modele vmodel=new Modele();
    public SoinAdapter() {
        super();
    }


    public SoinAdapter(Context context, ArrayList<VisiteSoin> vListSoin) {
        super();
        layoutInflater = LayoutInflater.from(context);
        listSoin = vListSoin;

    }
    @Override
    public int getCount() {
        return listSoin.size();
    }

    @Override
    public Object getItem(int position) {
        return listSoin.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    private class ViewHolder {
        TextView textViewSoin;
        CheckBox checkRealise;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.vueunevisite, null);
            // convertView = layoutInflater.inflate(R.layout.vueunevisite,parent,false);
            holder.textViewSoin = (TextView) convertView.findViewById(R.id.vuesoinslibel);
            holder.checkRealise = (CheckBox) convertView.findViewById(R.id.vuesoinsrealise);

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
        Soin p=vmodel.trouveSoin(listSoin.get(position).getId_categ_soins(),listSoin.get(position).getId_type_soins(),listSoin.get(position).getId_soins());
        //Log.d("Soins", "Recherche soins" + String.valueOf(listSoin.get(position).getId_categ_soins())+"/"+String.valueOf(listSoin.get(position).getId_type_soins())+"/"+String.valueOf(listSoin.get(position).getId_soins()));
        holder.textViewSoin.setText(p.getLibel());
        holder.checkRealise.setChecked(listSoin.get(position).isRealise());


        /********* COULEURS DU TEXTE DE LA LISTVIEW ******************/

        holder.textViewSoin.setTextColor(Color.BLACK);

        /******* Taille du texte de la listView ********************/
        holder.textViewSoin.setTextSize(15);

        holder.checkRealise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb =  v.findViewById(R.id.vuesoinsrealise);
                listSoin.get(position).setRealise(cb.isChecked());
                vmodel.saveVisiteSoin(listSoin.get(position));
            }
        });
        return convertView;
    }

}

