/*
 * CS 193A, Marty Stepp
 * A DetailsActivity is now just a thin wrapper around a DetailsFragment
 * that is used only when in portrait orientation.
 */

package cs193a.stanford.edu.pokedex;

import android.os.Bundle;
import stanford.androidlib.SimpleActivity;

public class DetailsActivity extends SimpleActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String pokemonName = getStringExtra("pokemon_name", "Pikachu");
        setPokemonName(pokemonName);
    }

    /*
     * Sets this fragment to display the pokemon with the given name.
     * Shows that pokemon's name, image, and text about that pokemon
     * from a file with a corresponding name.
     */
    public void setPokemonName(String pokemonName) {
        int imageID = getResourceId(pokemonName.toLowerCase(), "drawable");
        int fileID = getResourceId(pokemonName.toLowerCase(), "raw");
        String fileText = readFileText(fileID);
        $TV(R.id.pokemon_name).setText(pokemonName);
        $IV(R.id.pokemon_image).setImageResource(imageID);
        $TV(R.id.pokemon_details).setText(fileText);
    }
}
