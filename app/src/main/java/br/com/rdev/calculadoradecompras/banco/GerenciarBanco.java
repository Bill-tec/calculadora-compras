package br.com.rdev.calculadoradecompras.banco;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GerenciarBanco extends SQLiteOpenHelper {
    public static final String NOME_BANCO = "bancoDeDados.db";
    public static final int VERSAO = 1;
    public GerenciarBanco(Context context){
        super(context,NOME_BANCO,null,VERSAO);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String produtosSql="CREATE TABLE produtos(id integer primary key autoincrement," +
                "nome text, quantidade integer,preco real)";
        db.execSQL(produtosSql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("DROP TABLE IF EXISTS produtos");
        onCreate(db);
    }}