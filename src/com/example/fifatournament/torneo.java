package com.example.fifatournament;

import java.util.ArrayList;

import com.rolled.fifatournament.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class torneo extends Activity {
	Button clasificacion,partidos,avanzar;
	int index=0;
	TextView prox;
	int idTorneo;
	ArrayList<String> arrayPartidos = new ArrayList<String>();
	ArrayList<String> arrayIdPartido = new ArrayList<String>();
	ArrayList<String> arrayLocales = new ArrayList<String>();
	ArrayList<String> arrayVisitantes = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.torneo);
		Cursor c;
		clasificacion = (Button) findViewById(R.id.BClasi);
		partidos = (Button) findViewById(R.id.BPartidos);
		avanzar = (Button) findViewById(R.id.BAvanzar);
		prox = (TextView) findViewById(R.id.proxpartido);
		
		//Parametros desde creartorneo.java
		Bundle parametro = getIntent().getExtras();
		idTorneo = parametro.getInt("idTorneo");
		adaptadorBD db = new adaptadorBD(this);
		db.abrir();
		
		//Miramos si el torneo ya ha finalizado
		c = db.obtenerTorneo(idTorneo);
		
		if (c.getInt(2) != 1){
			//Recuperamos los partidos no jugados
			c = db.obtenerPartidoNJ(idTorneo);
			
			if (c.moveToFirst()){
				do{
					arrayIdPartido.add(c.getString(0));
					arrayLocales.add(c.getString(2));
					arrayVisitantes.add(c.getString(3));
					arrayPartidos.add(c.getString(2)+"-"+c.getString(3));
				}while (c.moveToNext());
			}
			//if (arrayPartidos.size()>0)
			prox.setText(arrayPartidos.get(index));
		}else{

			//Sacar campeon
			prox.setText("Fin del torneo");
			avanzar.setEnabled(false);
		}
		db.cerrar();


			avanzar.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub7
						recogerResultado();

				}
			});
			
			clasificacion.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Dialog dialog = new Dialog(v.getContext());
					dialog.setContentView(R.layout.clasificacion);
					dialog.setTitle("Clasificación");
					
					ArrayList<String> DataSource = new ArrayList<String>();
					
					Cursor c;
					GridView clasif = (GridView) dialog.findViewById(R.id.gridClasif);
	
					DataSource.add("Nombre");
					DataSource.add("       PJ");
					DataSource.add("       GA");
					DataSource.add("       GC");
					DataSource.add("       Ptos");
					
					adaptadorBD db = new adaptadorBD(v.getContext());
					db.abrir();
					//Recuperamos los participantes
					c = db.obtenerParticipante(idTorneo);
					
	
					if (c.moveToFirst()){
						do{
							DataSource.add(c.getString(2));
							DataSource.add("       "+c.getString(3));
							DataSource.add("       "+c.getString(4));
							DataSource.add("       "+c.getString(5));
							DataSource.add("       "+c.getString(7));
						}while (c.moveToNext());
					}
					db.cerrar();

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_spinner_item,DataSource);
					clasif.setAdapter(adapter);

					dialog.show();
				}
			});
			
			partidos.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Dialog dialog = new Dialog(v.getContext());
					dialog.setContentView(R.layout.partidos);
					dialog.setTitle("Partidos");
					
					ArrayList<String> DataSource = new ArrayList<String>();
					
					Cursor c;
					GridView partidos = (GridView) dialog.findViewById(R.id.gridPartidos);
					DataSource.add("Equipos");
					DataSource.add("       Resultado");
					
					adaptadorBD db = new adaptadorBD(v.getContext());
					db.abrir();
					//Recuperamos los participantes
					c = db.obtenerPartido(idTorneo);
					if (c.moveToFirst()){
						do{
							DataSource.add(c.getString(2)+"-"+c.getString(3));
							if (c.getString(4).equals("missed"))
								DataSource.add("       "+"No jugado");
							else
								DataSource.add("       "+c.getString(4));
						}while (c.moveToNext());
					}
					db.cerrar();

					ArrayAdapter<String> adapter = new ArrayAdapter<String>(v.getContext(),android.R.layout.simple_spinner_item,DataSource);
					partidos.setAdapter(adapter);

					dialog.show();
					 
				}
			});
	}
	
	public void recogerResultado(){
		final Dialog dialog = new Dialog(this);
		dialog.setContentView(R.layout.resultado);
		dialog.setTitle("Resultado");
		
		final EditText golesLocal = (EditText)dialog.findViewById(R.id.golesLocal);
		final EditText golesVisitante=(EditText)dialog.findViewById(R.id.golesVisitante);
		final TextView local=(TextView)dialog.findViewById(R.id.textLocal);
		final TextView visitante=(TextView)dialog.findViewById(R.id.textVisitante);
		Button enviar = (Button) dialog.findViewById(R.id.enviar);
		
		local.setText(arrayLocales.get(index));
		visitante.setText(arrayVisitantes.get(index));
		
		enviar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adaptadorBD db = new adaptadorBD(v.getContext());
				db.abrir();
				
				if (Integer.parseInt(golesLocal.getText().toString()) > Integer.parseInt(golesVisitante.getText().toString())) //Gana local
					db.actualizarClasif(idTorneo, local.getText().toString(), visitante.getText().toString(),golesLocal.getText().toString(),golesVisitante.getText().toString(), 1);
				else if (Integer.parseInt(golesVisitante.getText().toString()) > Integer.parseInt(golesLocal.getText().toString())) //Gana vis
					db.actualizarClasif(idTorneo, visitante.getText().toString(),local.getText().toString(),golesVisitante.getText().toString(),golesLocal.getText().toString(), 1);
				else //Empate
					db.actualizarClasif(idTorneo, visitante.getText().toString(),local.getText().toString(),golesLocal.getText().toString(),golesVisitante.getText().toString(), -1);
				
				db.actualizarResultado(idTorneo, local.getText().toString(), visitante.getText().toString(),golesLocal.getText().toString(),golesVisitante.getText().toString());
				db.cerrar();
				index++;
				if (index >= arrayPartidos.size()){
					prox.setText("Fin del torneo");
					adaptadorBD db1 = new adaptadorBD(v.getContext());
					db1.abrir();
					db1.finalizarTorneo(idTorneo);
					db1.cerrar();
					avanzar.setEnabled(false);
				}else
					prox.setText(arrayPartidos.get(index));
				
				dialog.cancel();
			}
		});
		dialog.show();
		
	}

}
