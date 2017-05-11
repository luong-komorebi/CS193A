/*
 * CS 193A, Marty Stepp
 * This activity is the main activity that first loads.
 * It shows a grid of available Pokemon and lets the user click on them
 * to see details about each Pokemon using a details activity or fragment.
 */

package cs193a.stanford.edu.pokedex;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import stanford.androidlib.*;

public class PokedexActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);
    }

}
