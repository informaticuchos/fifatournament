package com.example.fifatournament;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class adaptadorBD {
	//Miembros
    private static DatabaseHelper DBHelper;
    private static SQLiteDatabase db;
    
    public static final String TAG="adaptadorBD";
    public static final String NOMBRE_BD="MiBD";
    public static final String TABLA_PARTICIPANTES = "participantes";
    public static final String TABLA_PARTIDOS = "partidos";
    public static final String TABLA_TORNEOS="torneos";
    public static final int VERSION = 1;
    public static final String CREAR_PARTICIPANTES="create table participantes(idParticipante integer primary key autoincrement,idTorneo integer not null,nombre varchar(20) not null,partidosJugados integer not null,golesAF integer not null,golesEC integer not null,DFG integer not null,puntos integer not null);";
    public static final String CREAR_PARTIDOS="create table partidos(idPartido integer primary key autoincrement,idTorneo integer not null,local varchar(20) not null,visitante varchar(20) not null,resultado varchar(8) not null);";
    public static final String CREAR_TORNEOS="create table torneos (idTorneo integer primary key autoincrement,fecha varchar(21),fin integer default 0 not null);";
    public final Context context;

    
	public adaptadorBD(Context ctx) 
    {
			this.context = ctx;
            DBHelper = new DatabaseHelper(ctx);
    }
	
	/**
     * 
     * Clase usada para crear o actualizar la BD
     *
     */
    public static class DatabaseHelper extends SQLiteOpenHelper 
    {

    	DatabaseHelper (Context context){
    		super (context,NOMBRE_BD,null,VERSION);
    	}
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			try{
				db.execSQL(CREAR_PARTICIPANTES);
				db.execSQL(CREAR_PARTIDOS);
				db.execSQL(CREAR_TORNEOS);
				Log.v("TABLAS", "EXITO");
			}catch (SQLException e){
				Log.v("TABLAS", "ERROR WEY");
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS partidos");
			db.execSQL("DROP TABLE IF EXISTS participantes");
			db.execSQL("DROP TABLE IF EXISTS torneos");
			db.execSQL("DROP TABLE IF EXISTS juegaen");
			onCreate(db);
		}
		

		
    
   }
    /**
     * Abre la BD.
     * @return 
     * 
     * @return AdaptadorBD
     * @throws SQLException
     */
    public adaptadorBD abrir() throws SQLException 
    {
            //Abre la BD y si la BD no existe llama al metodo onCreate
    		db = DBHelper.getWritableDatabase();
            return this;
    }
	
	/**
     * Cierra la BD.
     * 
     * @return AdaptadorBD
     */
    public void cerrar() 
    {
            DBHelper.close();
    }
    
    //Insertar un participante
    public long insertarParticipante (String nombre,int torneo,int PJ,int GF,int GC,int puntos){
    	ContentValues valoresIniciales = new ContentValues();
    	// create table participantes(nombre varchar(20) not null,partidosJugados integer not null,golesAF integer not null,golesEC integer not null,puntos integer not null);";
    	int DFG=GF-GC;
    	valoresIniciales.put("nombre", nombre);
    	valoresIniciales.put("idTorneo", torneo);
    	valoresIniciales.put("partidosJugados", PJ);
    	valoresIniciales.put("golesAF", GF);
    	valoresIniciales.put("golesEC", GC);
    	valoresIniciales.put("puntos", puntos);
    	valoresIniciales.put("DFG", DFG);
    	return db.insert(TABLA_PARTICIPANTES, null, valoresIniciales);
    	
    }
    
    //Insertar un partido
    public long insertarPartido(int torneo,String local,String visitante,String resultado){
    	ContentValues valoresIniciales = new ContentValues();
    	//create table partidos(id integer not null primary key,local varchar(20) not null,visitante varchar(20) not null,resultado varchar(8) not null

    	//valoresIniciales.put("id", id);
    	valoresIniciales.put("idTorneo", torneo);
    	valoresIniciales.put("local", local);
    	valoresIniciales.put("visitante", visitante);
    	valoresIniciales.put("resultado", resultado);
    	return db.insert(TABLA_PARTIDOS, null, valoresIniciales);
    	
    }
    
    //Insertar un torneo
    public long insertarTorneo(){
    	ContentValues valoresIniciales = new ContentValues();
    	Date ahora = new Date();
        SimpleDateFormat fecha = new SimpleDateFormat("dd-MM-yyyy");   
        SimpleDateFormat hora = new SimpleDateFormat("HH:mm:ss");
       // Log.v("FEcha:" , fecha.format(ahora)+"/"+hora.format(ahora));
    	valoresIniciales.put("fecha", fecha.format(ahora)+"/"+hora.format(ahora));
    	return db.insert(TABLA_TORNEOS, null, valoresIniciales);	
    }
    
    
    //Borrar un participante
    public boolean borrarParticipante(long idFila){
    	return db.delete(TABLA_PARTICIPANTES, "idParticipante="+idFila,null)>0;
    }
    
    //Borrar un partido
    public boolean borrarPartido(long idFila){
    	return db.delete(TABLA_PARTIDOS, "idPartido="+idFila,null)>0;
    }
    
    //Borrar un torneo
    public boolean borrarTorneo(long idFila){
    	return db.delete(TABLA_TORNEOS, "idTorneo="+idFila,null)>0;
    }
    
    //Recuperar un participante segun el torneo y ordenado por puntos
    public Cursor obtenerParticipante(long idFila) throws SQLException{
    	Cursor mCursor = db.query(true, TABLA_PARTICIPANTES, new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, "idTorneo="+idFila+" order by puntos desc,DFG desc", null, null, null, null, null);
    	if (mCursor !=null)
    		mCursor.moveToFirst();
    	
    	return mCursor;
    }
    
    //Recuperar todos los participantes
    public Cursor obtenerTodosLosParticipantes(){
    	return db.query(TABLA_PARTICIPANTES,new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, null,null,null,null,null);
    }
    
    //Recuperar un partido
    
    public Cursor obtenerTorneo(long idFila) throws SQLException{
    	Cursor mCursor = db.query(true, TABLA_TORNEOS, new String[]{"idTorneo","fecha","fin"}, "idTorneo="+idFila, null, null, null, null, null);
    	if (mCursor !=null)
    		mCursor.moveToFirst();
    	return mCursor;
    }
    
  //Recuperar todos los torneos
    public Cursor obtenerTodosLosTorneos(){
    	return db.query(TABLA_TORNEOS, new String[]{"idTorneo","fecha","fin"}, null,null,null,null,null);
    }
    
    
    public Cursor obtenerPartido(long idFila) throws SQLException{
    	Cursor mCursor = db.query(true, TABLA_PARTIDOS, new String[]{"idPartido","idTorneo","local","visitante","resultado"}, "idTorneo="+idFila, null, null, null, null, null);
    	if (mCursor !=null)
    		mCursor.moveToFirst();
    	
    	return mCursor;
    }
    
    //Obtener partidos no jugados
    public Cursor obtenerPartidoNJ(long idFila) throws SQLException{
    	Cursor mCursor = db.query(true, TABLA_PARTIDOS, new String[]{"idPartido","idTorneo","local","visitante","resultado"}, "idTorneo="+idFila+" and resultado='missed'", null, null, null, null, null);
    	if (mCursor !=null)
    		mCursor.moveToFirst();
    	
    	return mCursor;
    }
    
  //Recuperar todos los partidos
    public Cursor obtenerTodosLosPartidos(){
    	return db.query(TABLA_PARTIDOS,new String[]{"idPartido","idTorneo","local","visitante","resultado"}, null,null,null,null,null);
    }
    
    //finalizar torneo
    public boolean finalizarTorneo(long idFila){
    	ContentValues args = new ContentValues();
    	args.put("fin",1);
    	return db.update(TABLA_TORNEOS, args, "idTorneo="+idFila, null) >0;
    }
    
    //Actualizar clasificacion
    
    public boolean actualizarClasif(int idTorneo, String ganador, String perdedor, String golesG, String golesP,int tipo){
    	
    	ContentValues args = new ContentValues();
    	Cursor mCursor;
    	int DFG;
    	if (tipo==-1){//Han empatado
    		//Actualizar primer equipo
	   		 mCursor = db.query(true, TABLA_PARTICIPANTES, new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, "idTorneo="+idTorneo+" and nombre="+"'"+ganador+"'", null, null, null, null, null);
	       	if (mCursor !=null)
	       		mCursor.moveToFirst();    		
	       	
	       	args.put("partidosJugados",mCursor.getInt(3)+1);
	       	args.put("golesAF", mCursor.getInt(4)+Integer.parseInt(golesG));
	       	args.put("golesEC", mCursor.getInt(5)+Integer.parseInt(golesP));
	       	DFG= (mCursor.getInt(4)+Integer.parseInt(golesG))-(mCursor.getInt(5)+Integer.parseInt(golesP));
	       	args.put("DFG", DFG);
	       	args.put("puntos", mCursor.getInt(7)+1);
	       	
	       	db.update(TABLA_PARTICIPANTES, args, "idTorneo="+idTorneo+" and nombre="+"'"+ganador+"'", null);
	   		//Actualizar segundo equipo
	   		mCursor = db.query(true, TABLA_PARTICIPANTES, new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, "idTorneo="+idTorneo+" and nombre="+"'"+perdedor+"'", null, null, null, null, null);
	       	if (mCursor !=null)
	       		mCursor.moveToFirst();    		
	       	
	       	args.put("partidosJugados",mCursor.getInt(3)+1);
	       	args.put("golesAF", mCursor.getInt(4)+Integer.parseInt(golesP));
	       	args.put("golesEC", mCursor.getInt(5)+Integer.parseInt(golesG));
	       	DFG = (mCursor.getInt(4)+Integer.parseInt(golesP))-(mCursor.getInt(5)+Integer.parseInt(golesG));
	       	args.put("DFG", DFG);
	       	args.put("puntos", mCursor.getInt(7)+1);
	       	return db.update(TABLA_PARTICIPANTES, args, "idTorneo="+idTorneo+" and nombre="+"'"+perdedor+"'", null)>0;
    	}else{
    		
    		//Actualizar ganador
    		 mCursor = db.query(true, TABLA_PARTICIPANTES, new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, "idTorneo="+idTorneo+" and nombre="+"'"+ganador+"'", null, null, null, null, null);
        	if (mCursor !=null)
        		mCursor.moveToFirst();    		
        	
        	args.put("partidosJugados",mCursor.getInt(3)+1);
        	args.put("golesAF", mCursor.getInt(4)+Integer.parseInt(golesG));
        	args.put("golesEC", mCursor.getInt(5)+Integer.parseInt(golesP));
        	DFG = (mCursor.getInt(4)+Integer.parseInt(golesG))-(mCursor.getInt(5)+Integer.parseInt(golesP));
        	args.put("DFG", DFG);
        	args.put("puntos", mCursor.getInt(7)+3);
        	db.update(TABLA_PARTICIPANTES, args, "idTorneo="+idTorneo+" and nombre="+"'"+ganador+"'", null);
        	
    		//Actualizar perdedor
    		mCursor = db.query(true, TABLA_PARTICIPANTES, new String[]{"idParticipante","idTorneo","nombre","partidosJugados","golesAF","golesEC","DFG","puntos"}, "idTorneo="+idTorneo+" and nombre="+"'"+perdedor+"'", null, null, null, null, null);
        	if (mCursor !=null)
        		mCursor.moveToFirst();    		
        	
        	args.put("partidosJugados",mCursor.getInt(3)+1);
        	args.put("golesAF", mCursor.getInt(4)+Integer.parseInt(golesP));
        	args.put("golesEC", mCursor.getInt(5)+Integer.parseInt(golesG));
        	DFG = (mCursor.getInt(4)+Integer.parseInt(golesP))-(mCursor.getInt(5)+Integer.parseInt(golesG));
        	args.put("DFG", DFG);
        	args.put("puntos", mCursor.getInt(7)+0);
        	return db.update(TABLA_PARTICIPANTES, args, "idTorneo="+idTorneo+" and nombre="+"'"+perdedor+"'", null)>0;
    	}
    	

    }
    
	//Actualizar clasificacion
    
    public boolean actualizarResultado(int idTorneo, String local, String visitante, String golesL, String golesV){
    	ContentValues args = new ContentValues();
    	args.put("resultado",golesL+"-"+golesV);
    	return db.update(TABLA_PARTIDOS, args, "idTorneo="+idTorneo+" and local="+"'"+local+"'"+" and visitante="+"'"+visitante+"'", null)>0;
    }




}
