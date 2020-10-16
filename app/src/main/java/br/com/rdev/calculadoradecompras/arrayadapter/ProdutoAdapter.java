package br.com.rdev.calculadoradecompras.arrayadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;

import br.com.rdev.calculadoradecompras.R;
import br.com.rdev.calculadoradecompras.models.Produto;

public class ProdutoAdapter extends ArrayAdapter<Produto> {
    private final Context context;
    private final ArrayList<Produto> produtos;
    public ProdutoAdapter(Context context, ArrayList<Produto> produtos){
        super(context, R.layout.modelo_lista, produtos);
        this.context = context;
        this.produtos = produtos;
    }
    @Override
    public View getView(int position, View contentView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.modelo_lista, parent, false);

        TextView produto = (TextView) rowView.findViewById(R.id.lstProduto);
        TextView qtd = (TextView) rowView.findViewById(R.id.lstQuantidade);
        TextView precoItemT = (TextView) rowView.findViewById(R.id.lstPreco);

        DecimalFormat df = new DecimalFormat("#.00");

        produto.setText(produtos.get(position).getNome());
        qtd.setText(Integer.toString(produtos.get(position).getQuantidade()));
        precoItemT.setText(df.format((produtos.get(position).getQuantidade()
                * produtos.get(position).getPreco()))+"R$");
        return rowView;
    }
}
