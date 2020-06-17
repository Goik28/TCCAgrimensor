package samuelbabinski.tcc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        switch (item.getItemId()) {
            case R.id.action_sobre:
                alertDialog.setTitle("Sobre");
                alertDialog.setMessage("    Esta é a tela inicial do aplicativo.\n" +
                        "   Ela contém as principais funções do aplicativo.\n" +
                        "   O botão 'Salvar' permite salvar a lista de coordenadas para uso posterior.\n" +
                        "   O botão 'Carregar' permite carregar uma lista de coordenadas salva previamente pelo botão 'Salvar'.\n" +
                        "   Atenção, o item 'Sobre' no menu de cada função contém dicas de como utilizar tal função.\n");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                return true;
            case R.id.action_licenca:
                alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Licença");
                alertDialog.setMessage("    Este aplicativo foi desenvolvido como um 'Trabalho de Conclusão de Curso'" +
                        " do curso de 'Sistemas de Informação', 'Turma de 2012' da 'Faculdade Assis Gurgacz' pelo aluno 'Samuel Babinski'.\n" +
                        "\n" +
                        "   Copyright 2015 Samuel Babinski\n" +
                        "   Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "\n" +
                        "   O icone deste aplicativo foi feito por 'SimpleIcon', disponivel em 'http://www.flaticon.com/authors/simpleicon' e distribuído sob a CC BY 3.0.\n" +
                        "\n" +
                        "   Este aplicativo usa icones na barra de menu feitos por 'Google', disponíveis em 'https://www.google.com/design/icons/index.html' e distribuídos sob CC BY 4.0.\n" +
                        "\n" +
                        "   Este aplicativo usa a biblioteca 'Graphview' disponivel em 'http://www.android-graphview.org/' e distribuída sob GNU 2.0 with GPL linking exception");
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

    public static ArrayList<Location> posicoes = new ArrayList<Location>();

    public void Registra(View view) {
        Intent intent = new Intent(MainActivity.this, RegistraCoor.class);
        startActivity(intent);
    }

    public void Gerencia(View view) {
        Intent intent = new Intent(MainActivity.this, GerenciaLista.class);
        startActivity(intent);
    }

    public void Abremapa(View view) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        startActivity(intent);
    }

    public void PerfilTopo(View view) {
        Intent intent = new Intent(MainActivity.this, PerfilTopo.class);
        startActivity(intent);
    }

    public void Salva(View view) throws IOException {
        if (posicoes.isEmpty()) {
            final TextView textViewToChange = (TextView) findViewById(R.id.textView10);
            textViewToChange.setText("Não há nada para salvar.");
        } else {
            String temp = "";
            SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy_HH:mm:ss");
            String nomeArquivo = "Coordenadas_" + sdf.format(new Date());
            for (int i = 0; i < posicoes.size(); i++) {
                temp = temp + (posicoes.get(i).getExtras().getInt("satellites") + "\n" + posicoes.get(i).getAccuracy() + "\n" + posicoes.get(i).getLatitude() + "\n" + posicoes.get(i).getLongitude() + "\n");
            }
            FileOutputStream fos = openFileOutput(nomeArquivo, Context.MODE_PRIVATE);
            fos.write(temp.getBytes());
            fos.close();
            final TextView textViewToChange = (TextView) findViewById(R.id.textView10);
            textViewToChange.setText("O arquivo " + nomeArquivo + " foi salvo.");
        }
    }


    public void Carrega(View view) throws IOException, ClassNotFoundException {
        List<String> listaArq = new ArrayList<String>();
        for (int i = 0; i < fileList().length; i++) {
            if (fileList()[i].startsWith("Coordenadas_")) {
                listaArq.add(fileList()[i]);
            }
        }
        if (!listaArq.isEmpty()) {
            final String[] nomeArquivo = new String[1];
            final ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, listaArq);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            nomeArquivo[0] = adaptador.getItem(0);
            builder.setTitle("Projetos Salvos")
                    .setSingleChoiceItems(adaptador, 0,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    nomeArquivo[0] = adaptador.getItem(which);
                                }
                            })
                    .setPositiveButton("Carregar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Location loc;
                            String temp = "";
                            List<String> asd = new ArrayList<String>();
                            if (posicoes.isEmpty()) {
                                FileInputStream fis = null;
                                try {
                                    fis = openFileInput(nomeArquivo[0]);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                StringBuffer buffer = new StringBuffer();
                                BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
                                if (fis != null) {
                                    try {
                                        while ((temp = reader.readLine()) != null) {
                                            asd.add(temp);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < asd.size(); i++) {
                                        loc = new Location("arquivo");
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("satellites", Integer.parseInt(asd.get(i)));
                                        loc.setExtras(bundle);
                                        i++;
                                        loc.setAccuracy(Float.parseFloat(asd.get(i)));
                                        i++;
                                        loc.setLatitude(Float.parseFloat(asd.get(i)));
                                        i++;
                                        loc.setLongitude(Float.parseFloat(asd.get(i)));
                                        posicoes.add(loc);
                                    }
                                }
                                try {
                                    fis.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                final TextView textViewToChange = (TextView) findViewById(R.id.textView10);
                                textViewToChange.setText("Lista Carregada");
                            } else {
                                final TextView textViewToChange = (TextView) findViewById(R.id.textView10);
                                textViewToChange.setText("Para carregar o projeto, a lista atual deve estar vazia.");
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            final TextView textViewToChange = (TextView) findViewById(R.id.textView10);
            textViewToChange.setText("Não existe arquivo para carregar.");
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
