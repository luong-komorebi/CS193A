/*
 * CS 193A, Marty Stepp
 * This program is a quick demo of the Firebase remote database system.
 * The program allows a student to "log in" to a Simpsons grade database and
 * looks up their grades in the GradesActivity.
 */

package cs193a.stanford.edu.simpsongrades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import stanford.androidlib.*;
import stanford.androidlib.data.firebase.SimpleFirebase;

public class LoginActivity extends SimpleActivity
        implements SimpleFirebase.GetListener {
    public static final String FIREBASE_USERNAME = "stepp@stanford.edu";
    public static final String FIREBASE_PASSWORD = "csroxx";

    private static final boolean USE_SIMPLE_FIREBASE_LIBRARY = true;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // connect to Firebase
        if (USE_SIMPLE_FIREBASE_LIBRARY) {
            SimpleFirebase.with(this)
                    .signIn(FIREBASE_USERNAME, FIREBASE_PASSWORD);
        } else {
            mAuth = FirebaseAuth.getInstance();
            mAuth.signInWithEmailAndPassword(FIREBASE_USERNAME, FIREBASE_PASSWORD);
        }
    }

    /*
     * Called when the Login button is clicked.
     * Connects to the Firebase database and looks up the user name and password.
     */
    public void loginClick(View view) {
        String name = $ET(R.id.name).getText().toString();

        // look up this person's password in Firebase
        if (USE_SIMPLE_FIREBASE_LIBRARY) {
            startQueryWithLibrary(name);
        } else {
            startQueryWithoutLibrary(name);
        }
    }

    // contact the database to get user name and password (library version)
    private void startQueryWithLibrary(String name) {
        SimpleFirebase fb = SimpleFirebase.with(this);
        fb.get(fb.query("simpsons/students")
                .orderByChild("name").equalTo(name));
    }

    // contact the database to get user name and password (non-library version)
    private void startQueryWithoutLibrary(String name) {
        // "Bart" => {id=123, ...}
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference students = fb.child("simpsons/students");

        Query bart = students.orderByChild("name").equalTo(name);
        bart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                // data has arrived!
                processData(data);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("onCancelled: " + databaseError);
            }
        });
    }

    /*
     * SimpleFirebase library method.
     * This is called when SimpleFirebase.get() returns with the data.
     */
    @Override
    public void onGet(String path, DataSnapshot data) {
        processData(data);
    }

    // common code to examine the user name/password
    // (works for library and non-library versions)
    private void processData(DataSnapshot data) {
        final String password = $ET(R.id.password).getText().toString();

        // data: [
        //    {name="bart", id=123, email="bart@..", password="..."}}
        // ]
        log("data = " + data);
        if (!data.hasChildren()) {
            return;
        }

        Student stu = data.getChildren().iterator().next().getValue(Student.class);
        String correctPassword = stu.password;
        if (password.equals(correctPassword)) {
            log("Yay, log in successful");
            startActivity(
                    GradesActivity.class,
                    "name", stu.name,
                    "id", stu.id);
        } else {
            log("wrong, the password was: " + correctPassword + " (student: " + stu + ")");
        }
    }
}
