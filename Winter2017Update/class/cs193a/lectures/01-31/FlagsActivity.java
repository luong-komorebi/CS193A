/*
 * CS 193A, Winter 2017, Marty Stepp
 * This activity demonstrates dynamic UI by "stamping" many copies of a country flag
 * on the screen.  The layout for the country/flag is stored as flag.xml and is
 * replicated using the layout inflater.
 */

package edu.stanford.cs193a.flagsoftheworld;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import stanford.androidlib.*;

public class FlagsActivity extends SimpleActivity {
    // array of all countries to display
    private static final String[] COUNTRIES = {
            "Australia",
            "Brazil",
            "China",
            "Egypt",
            "France",
            "Germany",
            "Ghana",
            "Italy",
            "Japan",
            "Mexico",
            "Nepal",
            "Nigeria",
            "Spain",
            "United Kingdom",
            "United States"
    };

    // instance initializer
    // runs before any other code (on construction)
    {
        setTraceLifecycle(true);
    }

    /*
     * Called when the activity is created.
     * Adds the various flag stamps to the screen.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flags);

        for (String name : COUNTRIES) {
            addFlag(name);
        }
    }

    /*
     * This method adds a new copy of the flag widget described in flag.xml
     * to the screen, with the given country name/image.
     */
    private void addFlag(final String countryName) {
        // inflate a copy of the flag.xml layout into actual Java widgets
        View flag = getLayoutInflater()
                .inflate(R.layout.flag, /* parent */ null);

        TextView tv = (TextView) flag.findViewById(R.id.flag_text);
        tv.setText(countryName);

        // this code is to convert a string like "United States" into
        // an integer resource ID like R.drawable.unitedstates
        String countryName2 = countryName.replace(" ", "").toLowerCase();
        int flagImageID = getResources().getIdentifier(
                countryName2, "drawable", getPackageName());

        // listen for click events on the flag image button
        ImageButton img = (ImageButton) flag.findViewById(R.id.flag_image);
        img.setImageResource(flagImageID);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTheDialog(countryName);
            }
        });

        // add the flag stamp to the screen
        GridLayout layout = (GridLayout) findViewById(R.id.activity_flags);
        layout.addView(flag);
    }

    private void doTheDialog(String countryName) {
        // version without Stanford library
//        AlertDialog.Builder builder = new AlertDialog.Builder(FlagsActivity.this);
//        builder.setTitle("My Dialog");
//        builder.setMessage("You clicked " + countryName);
//        builder.setPositiveButton("OK",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        // code to run when OK is pressed
//                        Toast.makeText(FlagsActivity.this, "You clicked OK",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.show();

        // version with Stanford library
        // pop up a simple alert dialog
        SimpleDialog.with(this)
                .showAlertDialog("You REALLY clicked " + countryName);

        // pop up a dialog asking for input
        SimpleDialog.with(this)
                .showInputDialog("Type your name:", "Submit");
    }

    /*
     * This method is called when the showInputDialog call above is finished.
     * It "returns" the user's input to our code as the String input parameter below.
     * This input parameter represents the text that the user typed into the dialog.
     */
    @Override
    public void onInputDialogClose(AlertDialog dialog, String input) {
        toast("You typed: " + input);
    }
}
