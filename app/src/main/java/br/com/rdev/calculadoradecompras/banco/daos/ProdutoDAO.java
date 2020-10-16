package br.com.rdev.calculadoradecompras.banco.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import java.util.ArrayList;

import br.com.rdev.calculadoradecompras.banco.GerenciarBanco;
import br.com.rdev.calculadoradecompras.models.Produto;

public class ProdutoDAO {
    private SQLiteDatabase banco;
    private GerenciarBanco gerenciarBanco;
    private static final String[] camposTodos = {"id", "nome","quantidade", "preco"};
    private static final String nomeTabela = "produtos";

    public ProdutoDAO(Context context){
        gerenciarBanco = new GerenciarBanco(context);
    }
    public void addProduto(Produto produto){
        banco = gerenciarBanco.getWritableDatabase();
        ContentValues dados = new ContentValues();
        dados.put(camposTodos[1], produto.getNome());
        dados.put(camposTodos[2], produto.getQuantidade());
        dados.put(camposTodos[3], produto.getPreco());
        banco.insert(nomeTabela,null, dados);
        banco.close();
    }
    public ArrayList<Produto> listProdutos(){
        ArrayList<Produto> produtos = new ArrayList<Produto>();
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        Cursor cursor = db.query(nomeTabela,camposTodos,null,null,null,null,"id ASC");
        while (cursor.moveToNext()){
            Produto p = new Produto(cursor.getInt(0),cursor.getString(1),
                    cursor.getInt(2),cursor.getFloat(3));
            produtos.add(p);
        }
        db.close();
        return produtos;
    }
    public void deleteProdutoId(Produto produto){
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        String where = camposTodos[0]+" = "+ produto.getId();
        db.delete(nomeTabela,where,null);
        db.close();
    }
    public void deleteProdutos(){
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        db.delete(nomeTabela,null,null);
        db.close();
    }
    public void updateProduto(Produto produto){
        SQLiteDatabase db = gerenciarBanco.getReadableDatabase();
        String where = camposTodos[0]+" = "+ produto.getId();
        ContentValues dados = new ContentValues();
        dados.put(camposTodos[1], produto.getNome());
        dados.put(camposTodos[2], produto.getQuantidade());
        dados.put(camposTodos[3], produto.getPreco());
        db.update(nomeTabela,dados,where,null);
        db.close();
    }
}
