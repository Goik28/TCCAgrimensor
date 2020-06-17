package samuelbabinski.tcc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GerenciaLista extends AppCompatActivity {

    List<String> listaOrg = new ArrayList<String>();
    ListView lista;
    ArrayAdapter<String> adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerencia_lista);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (!MainActivity.posicoes.isEmpty()) {
            organizaLista();
            adaptador = new ArrayAdapter<String>(this, R.layout.item_layout, listaOrg);
            lista = (ListView) findViewById(R.id.listView);
            lista.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            lista.setAdapter(adaptador);
            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    lista.setItemChecked(position, true);
                }
            });
        }
    }

    public void organizaLista() {
        for (int i = 0; i < MainActivity.posicoes.size(); i++) {
            String s = "Nº de satelites: " + MainActivity.posicoes.get(i).getExtras().getInt("satellites") + "\nMargem de erro: " + MainActivity.posicoes.get(i).getAccuracy() + " m" +
                    "\nLatitude: " + MainActivity.posicoes.get(i).getLatitude() + "\nLongitude: " + MainActivity.posicoes.get(i).getLongitude();
            listaOrg.add(s);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gerencia_lista, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sobre:
                AlertDialog alertDialog = new AlertDialog.Builder(GerenciaLista.this).create();
                alertDialog.setTitle("Sobre");
                alertDialog.setMessage("    Esta é a lista que contem as coordenadas obtidas anteriormente.\n" +
                        "   Seu objetivo é mostrar as posições e permitir que o usuário exclua alguma posição incorreta.\n" +
                        "   Para isso acontecer, selecione a posição desejada e clique no botão 'Deletar Posição'.\n" +
                        "   Atenção, as posições irão estar listadas conforme a ordem em que foram obtidas.");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deletaPos(View view) {
        if (MainActivity.posicoes.isEmpty()) {
            Toast.makeText(this, "A lista está vazia.", Toast.LENGTH_LONG).show();
        } else {
            if (lista.getCheckedItemPosition() != -1) {
                listaOrg.remove(lista.getCheckedItemPosition());
                MainActivity.posicoes.remove(lista.getCheckedItemPosition());
                lista.clearChoices();
                adaptador.notifyDataSetChanged();
            }
        }
    }
}



