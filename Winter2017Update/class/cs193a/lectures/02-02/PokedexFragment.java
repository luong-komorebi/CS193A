/*
 * CS 193A, Winter 2017, Marty Stepp
 * This fragment stores the code that used to be in PokedexActivity.
 * We have moved it here so that the Pokedex and the pokemon details
 * can be displayed side-by-side using fragments.
 */

package cs193a.stanford.edu.pokedex;

import android.os.Bundle;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;

import stanford.androidlib.*;

public class PokedexFragment extends SimpleFragment {

    /* auto-generated code */
    public PokedexFragment() {
        // Required empty public constructor
    }

    /* auto-generated code */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pokedex, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // set up event listeners (must loop over all of
        // the TableLayout -> TableRow -> ImageButton)
        SimpleActivity activity = getSimpleActivity();
        TableLayout layout = (TableLayout) activity.findViewById(R.id.table_layout);
        for (int i = 0; i < layout.getChildCount(); i++) {
            TableRow row = (TableRow) layout.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                ImageButton button = (ImageButton) row.getChildAt(j);
                button.setOnClickListener(this);
            }
        }
    }

    /*
     * Called when the user clicks on any of the Pokemon image buttons.
     * Displays a DetailsActivity when in portrait mode, and
     * displays a DetailsFragment when in landscape mode.
     */
    @Override
    public void onClick(View view) {
        ImageButton button = (ImageButton) view;
        String tag = button.getTag().toString();

        SimpleActivity activity = getSimpleActivity();
        if (activity.isPortrait()) {
            // jump to DetailsActivity
            activity.startActivity(DetailsActivity.class, "pokemon_name", tag);
        } else {
            // both fragments are in the same activity;
            // set the pokemon to be displayed on the right side
            DetailsFragment frag = (DetailsFragment) activity.getFragmentManager().findFragmentById(R.id.frag_details);
            frag.setPokemonName(tag);
        }
    }
}
