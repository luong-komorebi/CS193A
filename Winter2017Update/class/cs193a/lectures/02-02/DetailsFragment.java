/*
 * CS 193A, Winter 2017, Marty Stepp
 * This fragment represents the details about one Pokemon on the screen.
 * The bulk of this code used to be in DetailsActivity, but we refactored it by moving
 * it here so that the details could be shown side-by-side with the Pokedex.
 */

package cs193a.stanford.edu.pokedex;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.*;
import stanford.androidlib.*;

public class DetailsFragment extends SimpleFragment {

    /* auto-generated code */
    public DetailsFragment() {
        // Required empty public constructor
    }

    /* auto-generated code */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    /*
     * Called when the activity this fragment is inside of has been created.
     * This is where we put our initialization code that would have been in 'onCreate'.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SimpleActivity activity = getSimpleActivity();
        String pokemonName = activity.getStringExtra("pokemon_name", "Pikachu");
        setPokemonName(pokemonName);
    }

    /*
     * Sets this fragment to display the pokemon with the given name.
     * Shows that pokemon's name, image, and text about that pokemon
     * from a file with a corresponding name.
     */
    public void setPokemonName(String pokemonName) {
        SimpleActivity activity = getSimpleActivity();
        int imageID = activity.getResourceId(pokemonName.toLowerCase(), "drawable");
        int fileID = activity.getResourceId(pokemonName.toLowerCase(), "raw");
        String fileText = activity.readFileText(fileID);
        activity.$TV(R.id.pokemon_name).setText(pokemonName);
        activity.$IV(R.id.pokemon_image).setImageResource(imageID);
        activity.$TV(R.id.pokemon_details).setText(fileText);
    }
}
