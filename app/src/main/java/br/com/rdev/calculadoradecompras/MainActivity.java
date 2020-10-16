package br.com.rdev.calculadoradecompras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.pattern.MaskPattern;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import br.com.rdev.calculadoradecompras.arrayadapter.ProdutoAdapter;
import br.com.rdev.calculadoradecompras.banco.daos.ProdutoDAO;
import br.com.rdev.calculadoradecompras.models.Produto;

public class MainActivity extends AppCompatActivity {
    private InterstitialAd interstitialAd;
    private Button adicionar, limpar, popBntAtualizar, popBtnRemover;
    private EditText produto, qtd, precoUnd, popNome,popQtd,popPreco;
    private TextView totalLabel, popTotal;
    private ListView lista;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ProdutoDAO produtoDAO = new ProdutoDAO(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gerarAdTelaToda();

        setTitle("Crie sua lista");

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        gerarLista();


        produto = (EditText) findViewById(R.id.produto);
        qtd = (EditText) findViewById(R.id.quantidade);
        precoUnd = (EditText) findViewById(R.id.preco);

        adicionar = (Button) findViewById(R.id.btnAdd);
        adicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (produto.length() == 0 || qtd.length() == 0 || precoUnd.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Erro preencha os campos e tente novamente", Toast.LENGTH_LONG).show();
                } else {
                    Produto p = new Produto(0, produto.getText().toString().trim(),
                            Integer.parseInt(qtd.getText().toString().trim()),
                            Double.parseDouble(precoUnd.getText().toString().trim().replaceAll(",", ".")));
                    produtoDAO.addProduto(p);
                }
            }
        });

        limpar = (Button) findViewById(R.id.btnLimpar);
        limpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produto.setText("");
                qtd.setText("1");
                precoUnd.setText("");
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        gerarAdTelaToda();
    }
    public void gerarAdTelaToda(){
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemDelete:
                produtoDAO.deleteProdutos();
                gerarLista();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void gerarLista(){
        lista = (ListView) findViewById(R.id.lista);
        final ArrayList<Produto> produtos = produtoDAO.listProdutos();
        ArrayAdapter adapter = new ProdutoAdapter(this, produtos);
        double total = 0;
        totalLabel = (TextView)findViewById(R.id.mainTotalLab);
        if (produtos.isEmpty()){
            totalLabel.setText("Total");
        } else {
            for (Produto p: produtos) {
                total = total + (p.getQuantidade() * p.getPreco());
            }
            DecimalFormat df = new DecimalFormat("#.00");
            totalLabel.setText(df.format(total)+" R$");
        }
        lista.setAdapter(adapter);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?>adapter, View view, int position, long id) {
                editProdutoDialog(produtos.get(position));
            }
        });
    }
    public void editProdutoDialog(final Produto produtoDialog){
        dialogBuilder = new AlertDialog.Builder(this);
        final View editPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        dialogBuilder.setView(editPopupView);
        dialog = dialogBuilder.create();

        popNome = (EditText) editPopupView.findViewById(R.id.popProduto);
        popQtd = (EditText) editPopupView.findViewById(R.id.popQtd);
        popPreco = (EditText) editPopupView.findViewById(R.id.popPreco);
        popTotal = (TextView) editPopupView.findViewById(R.id.popTotalLab);

        AdView adViewPopUp = editPopupView.findViewById(R.id.adViewPopUp);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewPopUp.loadAd(adRequest);


        popNome.setText(produtoDialog.getNome());
        popQtd.setText(Integer.toString(produtoDialog.getQuantidade()));

        final DecimalFormat df = new DecimalFormat("#0.00");

        popPreco.setText(df.format(produtoDialog.getPreco()).replaceAll(",","."));

        popTotal.setText("Total: R$ "+  df.format(produtoDialog.getQuantidade() * produtoDialog.getPreco()));
        dialog.show();

        popBntAtualizar = (Button)editPopupView.findViewById(R.id.popBtnAtt);
        popBntAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Produto p = new Produto(produtoDialog.getId(),popNome.getText().toString().trim(),
                        Integer.parseInt(popQtd.getText().toString().trim()),
                        Double.parseDouble(popPreco.getText().toString().trim().replaceAll(",",".")));
                if (p.getNome().length() == 0 || p.getPreco() <= 0 || p.getQuantidade() <= 0){
                    Toast.makeText(getApplicationContext(), "Erro preencha os campos e tente novamente", Toast.LENGTH_LONG).show();
                } else {
                    produtoDAO.updateProduto(p);
                    Toast.makeText(getApplicationContext(), "Produto atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    fechar(dialog);
                }
            }
        });

        final Produto produtoDelete = produtoDialog;
        popBtnRemover = (Button)editPopupView.findViewById(R.id.popBtnRemover);
        popBtnRemover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                produtoDAO.deleteProdutoId(produtoDelete);
                fechar(dialog);
            }
        });
    }
    public void fechar(Dialog dialog){
        dialog.dismiss();
        gerarLista();
    }
}