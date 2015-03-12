package com.example.fifatournament;

import java.util.ArrayList;

import com.rolled.fifatournament.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.*;

public class crearTorneo extends Activity {
	Button añadir,crear;
	ListView lista;
	EditText entrada;
	int idTorneo;
	ArrayList<String> elementos = new ArrayList<String>();
	ArrayList<String> partidos = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.creartorneo);
		lista = (ListView) findViewById(R.id.lista);
		entrada = (EditText) findViewById(R.id.entrada);
		
		//String strings[] = new String[2];
		/*elementos.add("Sin equipos");
				 
		lista.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, elementos));*/

		añadir = (Button) findViewById(R.id.BAnadir);
		añadir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*if (elementos.get(0)=="Sin equipos"){
					elementos.remove(0);
				}*/
				String nuevo = entrada.getText().toString();
				if (nuevo.length()>0){
					elementos.add(nuevo);
					lista.setAdapter(new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, elementos));
					entrada.setText("");
				}
			}
		});
		
		lista.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> a, final View v, final int position, long id) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb=new AlertDialog.Builder(v.getContext());
		        adb.setTitle("¡Cuidado!");
		        adb.setMessage("¿Está seguro de eliminar el equipo seleccionado?");
		        adb.setNegativeButton("No", null);
		        adb.setPositiveButton("Si", new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						elementos.remove(position);
						lista.setAdapter(new ArrayAdapter<String>(v.getContext(), android.R.layout.simple_list_item_1, elementos));

					}});
		        adb.show();
				return false;
			}
		});
		
		crear = (Button) findViewById(R.id.BConfirmacion);
		crear.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (elementos.size()>2){
					Toast.makeText(getApplicationContext(), "Crear torneo", 
			                Toast.LENGTH_SHORT).show();
					Dialog dialog = new Dialog(v.getContext());
					dialog.setContentView(R.layout.ligatorneo);
					dialog.setTitle("Elige");
					Button liga = (Button)dialog.findViewById(R.id.liga);
					liga.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							generarPartidos(0);
							almacenar();
							iniciar_actividad();
						}
					});
					
					Button torneo = (Button)dialog.findViewById(R.id.torneo);
					torneo.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							generarPartidos(1);
							almacenar();
							iniciar_actividad();
						}
					});
					dialog.show();
					//generarPartidos();
					
					
				}else{
					Toast.makeText(getApplicationContext(), "El minimo de participantes son 3", 
			                Toast.LENGTH_SHORT).show();
				}
				
			}
		});
			
			
	}
	
	public void generarPartidos(int tipo){
		
		switch (tipo){
		case 0: //LIGA
			int jornadas = (elementos.size()*2)-2;
			int num =0,impar=0;
			String[][] part;
			if ((elementos.size() % 2)==0){//PAR
				num = elementos.size()/2;
				part= new String [num][2];
			}else{
				impar = 1;
				num = (elementos.size()/2)+1;
				part = new String [num][2];
			}
			
			for (int i=0;i<elementos.size();i++){
				for (int j=i+1;j<elementos.size();j++){
					if (j!=i)
						partidos.add(elementos.get(i)+"-"+elementos.get(j));
				}
			}
			
			for (int i=0;i<partidos.size();i++)
				Log.v("Partido "+i, partidos.get(i));
			/*//Generamos matriz
			int p=0;
			for (int i=0;i<num;i++)
				for (int j=0;j<2;j++){
					if (impar==1 && p>=elementos.size()) //El partido de descanso
						part[i][j]="D";
					else
						part[i][j]=elementos.get(p);
					p++;
				}
			
			int j=0,i;
			String aux;
			for (int numJ=0;numJ<jornadas;numJ++){
				for (i=0;i<num;i++){
						j=0;
						partidos.add(part[i][j]+"-"+part[i][j+1]); //Primera jornada
				}
				//i = num-1 y j =0
				aux = part[i][j];
				
			}*/
			
			/*for (int i=0;i<num;i++)
				for (int j=0;j<2;j++){
					Log.v("Posicion: "+i+" "+j,part[i][j] );	
				}*/
		break;
		case 1: //TORNEO
			
		break;
		}
	}
	
	public void almacenar(){
		long id;
		Cursor c;
		//Almacenamos
		adaptadorBD db = new adaptadorBD(this);
		db.abrir();
		
		//Añadimos nuevo torneo
		id = db.insertarTorneo();
		c = db.obtenerTorneo(id);
		idTorneo = c.getInt(0);
		
		//Añadimos participantes

		for (int i=0;i<elementos.size();i++)
			 id= db.insertarParticipante(elementos.get(i),idTorneo, 0, 0, 0, 0);
		
		//Añadimos partidos
		for (int i=0;i<partidos.size();i++){
			String local,visitante;
			local = partidos.get(i).split("-")[0];
			visitante = partidos.get(i).split("-")[1];
			id = db.insertarPartido(idTorneo,local, visitante, "missed");
		}
		
		//elementos = new ArrayList<String>();
		partidos = new ArrayList<String>();
		Log.v("BD","OK");
		db.cerrar();
	}
	
	public void iniciar_actividad(){
		
		Intent intent = new Intent(crearTorneo.this, torneo.class);
		 
		// damos valor al parámetro a pasar
		intent.putExtra("idTorneo", idTorneo);
		/*
		* Inicia una actividad que devolverá un resultado cuando
		* haya terminado. Cuando la actividad termina, se llama al método
		* onActivityResult() con el requestCode dado.
		* El uso de un requestCode negativo es lo mismo que llamar a 
		* startActivity(intent) (la actividad no se iniciará como una*/
		startActivityForResult(intent, 10);
	}

}
