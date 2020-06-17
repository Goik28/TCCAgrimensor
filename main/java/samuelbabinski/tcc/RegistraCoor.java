package samuelbabinski.tcc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RegistraCoor extends AppCompatActivity implements LocationListener {

    private final List<Location> locais = new ArrayList<>();
    private LocationManager locationManager;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_coor);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        chronometer.start();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //noinspection ResourceType
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final TextView textViewToChange = (TextView) findViewById(R.id.textView6);
            textViewToChange.setText("Inicializando interface GPS e procurando satelites.\nEste processo pode demorar.");
        } else {
            final TextView textViewToChange = (TextView) findViewById(R.id.textView6);
            textViewToChange.setText("A interface GPS está desabilitada.\nVá em opções de locais para ativar.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registra_coor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sobre:
                AlertDialog alertDialog = new AlertDialog.Builder(RegistraCoor.this).create();
                alertDialog.setTitle("Sobre");
                alertDialog.setMessage("    Este é o lugar em que se obtem as coordenadas para uso no aplicativo.\n" +
                        "   Seu objetivo é obter coordenadas e adiciona-las a lista de coordenadas para posterior visualização no mapa.\n" +
                        "   Assim que esta tela é aberta, o dispositivo tenta se conectar a rede GPS para começar a adquirir coordenadas.\n" +
                        "   A conexão com a rede GPS pode demorar ou até ser impossivel dependendo das condições na hora do uso.\n" +
                        "   Ao clicar no botão 'Registrar Coordenada' o aplicativo adquiri a melhor posição entre registrada desde o incio da captura.\n" +
                        "   A margem de erro informada é considerando uma confiabilidade de 68%.");
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

    public void pegaPonto(View view) {
        if (!locais.isEmpty()) {
            Location melhor;
            chronometer.stop();
            locationManager.removeUpdates(this);
            melhor = locais.get(0);
            for (int i = 0; i < locais.size() - 1; i++) {
                if (melhor.getAccuracy() > locais.get(i + 1).getAccuracy()) {
                    melhor = locais.get(i + 1);
                }
                if (melhor.getAccuracy() == locais.get(i + 1).getAccuracy()) {
                    if (melhor.getExtras().getInt("satellites") <= locais.get(i + 1).getExtras().getInt("satellites")) {
                        melhor = locais.get(i + 1);
                    }
                }
            }
            MainActivity.posicoes.add(melhor);
            final TextView textViewToChange = (TextView) findViewById(R.id.textView7);
            textViewToChange.setText("Coordenada registrada e adicionada a lista.");
            final Button botao = (Button) findViewById(R.id.b_Registra);
            botao.setEnabled(false);
        } else {
            final TextView textViewToChange = (TextView) findViewById(R.id.textView7);
            textViewToChange.setText("Não foi possivel registrar nenhuma coordenada.");
        }
    }


    public void onLocationChanged(Location location) {
        if (location.getExtras().getInt("satellites") > 4) {
            String str = "\nNº de satelites: " + location.getExtras().getInt("satellites") + "\nMargem de erro: " + location.getAccuracy() + "m" +
                    "\nLatitude: " + location.getLatitude() + "\nLongitude: " + location.getLongitude();
            final TextView textViewToChange = (TextView) findViewById(R.id.textView6);
            textViewToChange.setText(str);
            locais.add(location);
        } else {
            String str = "Número de satelites insuficientes para obter uma coordenada. Aguarde.";
            final TextView textViewToChange = (TextView) findViewById(R.id.textView6);
            textViewToChange.setText(str);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        //nada
    }

    @Override
    public void onProviderDisabled(String provider) {
        //nada
    }
}
