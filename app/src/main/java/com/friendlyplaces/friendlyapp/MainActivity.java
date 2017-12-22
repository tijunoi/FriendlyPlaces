package com.friendlyplaces.friendlyapp;

import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private NavigationView navView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.content_frame, new HomeFragment());
        tx.commit();

        android.support.v7.widget.Toolbar appbar = (android.support.v7.widget.Toolbar) findViewById(R.id.appbar);
        setSupportActionBar(appbar);

        //afegim la hamburguesita a la toolbar
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navview);


        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        boolean fragmentTransaction = false;
                       android.support.v4.app.Fragment fragment = null;

                        switch (menuItem.getItemId()){
                            case R.id.op_home:
                                fragment = new HomeFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.op_pos_rated:
                                fragment = new PositiveFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.op_neg_rated:
                                fragment = new NegativeFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.op_rated_places:
                                fragment = new RatedPlacesFragment();
                                fragmentTransaction = true;
                                break;
                            case R.id.op_logoff:
                                //aqui s'haurà de cambiar aixo i ficar un popup que
                                //et digui "Quieres cerrar sesión?" SI/NO o algo aixi
                                Log.i("NavigationView", "Pulsado cerrar sesión");
                                break;
                        }
                        if (fragmentTransaction){
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, fragment)
                                    .commit();

                            menuItem.setChecked(true);
                            //aixo es per mostrar el titul de cada opcio del menu
                            //pero es super feo asi que lo quitaré seguro
                            getSupportActionBar().setTitle(menuItem.getTitle());
                        }

                        //amb aquest metode al clicar la opció es tanca el menú
                        drawerLayout.closeDrawers();

                        return true;
                    }
                }
        );

    }
}
