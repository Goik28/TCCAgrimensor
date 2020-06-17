package samuelbabinski.tcc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PerfilTopo extends AppCompatActivity implements LocationListener {

    boolean imprime = false;
    GraphView graph;
    LineGraphSeries<DataPoint> series = null;
    private LocationManager locationManager;
    private final List<Location> local = new ArrayList<>();
    private final List<Double> altura = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_topo);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("Perfil Topográfico");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Distancia (m)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Altura (m)");
        graph.getGridLabelRenderer().setHighlightZeroLines(true);
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
                AlertDialog alertDialog = new AlertDialog.Builder(PerfilTopo.this).create();
                alertDialog.setTitle("Sobre");
                alertDialog.setMessage("    Este é o grafico que representará o perfil topográfico de um caminho.\n" +
                        "   Seu objetivo é mostrar a altura de um caminho conforme a distancia percorrida em tempo real.\n" +
                        "   Para isso acontecer, clique no botão 'Começar Registro' e percorra o caminho desejado.\n" +
                        "   Atenção, a margem de erro para o componente 'Altura' de uma coordenada GPS é superior a 10 metros.\n" +
                        "   Atenção, a altura é dada considerando o nivel do mar como base em zero.\n" +
                        "   Ao finalizar, pressione novamento o botão, que agora mudou de descrição, para exportar uma imagem do gráfico.\n" +
                        "   Essa imagem será salva na pasta padrão de imagens do seu dispositivo.");
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

    public void ativaGPS(View view) throws IOException {
        final Button botao = (Button) findViewById(R.id.b_RegTopo);
        if (imprime) {
            locationManager.removeUpdates(this);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy_HH:mm:ss");
            String nomeArquivo = sdf.format(new Date()) + "_Topo.png";
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(dir, nomeArquivo);
            FileOutputStream fos = new FileOutputStream(file);
            graph.setDrawingCacheEnabled(true);
            graph.setBackgroundColor(Color.WHITE);
            Bitmap b = graph.getDrawingCache();
            b.compress(Bitmap.CompressFormat.PNG, 40, fos);
            fos.close();
            MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
            Toast.makeText(this, "Imagem Salva.", Toast.LENGTH_LONG).show();
            botao.setEnabled(false);
        } else {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            //noinspection ResourceType
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
            imprime = true;
            botao.setText("Clique para salvar a imagem do perfil.");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (local.isEmpty() && altura.isEmpty()) {
            altura.add(location.getAltitude());
            series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(0, altura.get(0))
            });
            series.setDrawBackground(true);
            series.setBackgroundColor(Color.argb(50, 150, 70, 50));
            series.setDrawDataPoints(true);
            graph.addSeries(series);
            local.add(location);
        } else {
            local.add(location);
            altura.add(location.getAltitude());
            series.appendData(new DataPoint((local.get(local.size() - 2).distanceTo(location) + series.getHighestValueX()), altura.get(altura.size() - 1)), false, 1000);
            graph.addSeries(series);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
