package com.example.fifatournament;



import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.rolled.fifatournament.R;

public class MainActivity extends Activity {
	protected static final int REQUEST_CODE = 10;
	Button misTorneos,crearTorneo;
	ArrayList<String> arrayFechaTorneos;
	ArrayList<Integer> arrayIdTorneos;
	int idTorneo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		misTorneos = (Button) findViewById(R.id.misTorneos);
		crearTorneo = (Button) findViewById(R.id.crearTorneo);
	
		misTorneos.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.mistorneos);
				dialog.setTitle("Mis torneos");
				
				Button cargar = (Button)dialog.findViewById(R.id.BCargar);
				final Spinner lista = (Spinner)dialog.findViewById(R.id.listaTorneos);
				Cursor c;
				String array_spinner[]=new String[5];
				/*for (int i=0;i<5;i++)
					array_spinner[i] = "Torneo "+i;*/
				adaptadorBD db = new adaptadorBD(v.getContext());
				db.abrir();
				//Recuperamos los torneos
				c = db.obtenerTodosLosTorneos();
				arrayIdTorneos = new ArrayList<Integer>();
				arrayFechaTorneos = new ArrayList<String>();
				
				if (c.moveToFirst()){
					do{
						arrayIdTorneos.add(c.getInt(0));
						arrayFechaTorneos.add(c.getString(1));
					}while (c.moveToNext());
				}
				db.cerrar();
				if (arrayIdTorneos.size()>0){ //Si hay torneos
					ArrayAdapter adapter = new ArrayAdapter(v.getContext(),android.R.layout.simple_spinner_item,arrayFechaTorneos);
					lista.setAdapter(adapter);
				}else //Si no hay torneos, desactivo boton de cargar
					cargar.setEnabled(false);
				
				cargar.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						idTorneo=arrayIdTorneos.get(lista.getSelectedItemPosition());
						iniciar_torneo();
					}
				});
				
				
				dialog.show();
			}
		});
		
		crearTorneo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				/*Dialog dialog = new Dialog(v.getContext());
				dialog.setContentView(R.layout.creartorneo);
				dialog.setTitle("Crear torneo");
				dialog.show();*/
				Intent intent = new Intent(MainActivity.this, crearTorneo.class);
				 
		            // damos valor al parámetro a pasar
		            //intent.putExtra("param1", "valor del parámetro 1 (viene de mainActivity)");
		            /*
		             * Inicia una actividad que devolverá un resultado cuando
		             * haya terminado. Cuando la actividad termina, se llama al método
		             * onActivityResult() con el requestCode dado.
		             * El uso de un requestCode negativo es lo mismo que llamar a 
		             * startActivity(intent) (la actividad no se iniciará como una
		             * sub-actividad).
		             */
		            startActivityForResult(intent, REQUEST_CODE);
			}
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void iniciar_torneo(){
		
		Intent intent = new Intent(MainActivity.this, torneo.class);
		 
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
