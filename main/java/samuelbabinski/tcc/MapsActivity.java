package samuelbabinski.tcc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MapsActivity extends AppCompatActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setUpMapIfNeeded();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_maps_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tipo_mapa:
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
                return true;
            case R.id.salva_imagem:
                SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy_HH:mm:ss");
                String nomeArquivo = sdf.format(new Date()) + "_Mapa.png";
                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                final File file = new File(dir, nomeArquivo);
                GoogleMap.SnapshotReadyCallback callback = null;
                try {
                    callback = new GoogleMap.SnapshotReadyCallback() {
                        Bitmap bitmap;

                        FileOutputStream fos = new FileOutputStream(file);

                        @Override
                        public void onSnapshotReady(Bitmap snapshot) {
                            // TODO Auto-generated method stub
                            bitmap = snapshot;
                            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                        }
                    };
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null, null);
                Toast.makeText(this, "Imagem Salva.", Toast.LENGTH_LONG).show();
                mMap.snapshot(callback);
                return true;
            case R.id.action_sobre:
                AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
                alertDialog.setTitle("Sobre");
                alertDialog.setMessage("    Este é o mapa que contém as coordenadas adquiridas anteriormente.\n" +
                        "   Seu objetivo é mostrar a área e o perimetro de um poligno criado apartir da lista de coordenadas.\n" +
                        "   Para isso acontecer, deve-se ter ao menos três coordenadas para se criar um plano.\n" +
                        "   O botão na barra de menu superior com o formato de uma pequena montanha altera o tipo " +
                        "do mapa. As opções são: Viário ou Satelite.\n" +
                        "   O botão na barra de menu superior com o formato de um pequeno disquete serve para exportar " +
                        "uma imagem do mapa. Essa imagem será salva na pasta padrão de imagens do seu dispositivo.");
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

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        if (MainActivity.posicoes.size() > 2) {
            List<LatLng> locais = new ArrayList<LatLng>();
            List<LatLng> locaisperimetro = new ArrayList<LatLng>();
            PolygonOptions poligno = new PolygonOptions();
            poligno.geodesic(true);
            for (int i = 0; i < MainActivity.posicoes.size(); i++) {
                LatLng l = new LatLng(MainActivity.posicoes.get(i).getLatitude(), MainActivity.posicoes.get(i).getLongitude());
                locais.add(l);
            }
            locaisperimetro.addAll(locais);
            locaisperimetro.add(locais.get(0));
            poligno.addAll(locais);
            poligno.fillColor(Color.argb(30, 0, 255, 0));
            poligno.strokeColor(Color.argb(50, 0, 255, 0));
            poligno.strokeWidth(5);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locais.get(0), 18));
            mMap.addPolygon(poligno);
            mMap.addMarker(new MarkerOptions().position(SphericalUtil.interpolate(locais.get(0), locais.get(locais.size() - 1), 0.5)).title("Local").snippet("Area: " + String.format("%.2f", SphericalUtil.computeArea(locais)) + "m²"
                    + "\nPerimetro: " + String.format("%.2f", SphericalUtil.computeLength(locaisperimetro)) + " m").flat(true));
        } else {
            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Nenhum poligno criado.").snippet("Adicione coordenadas a lista.").flat(true));
        }
    }
}
