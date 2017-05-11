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

public class LoginActivity extends SimpleActivity {
    public static final String FIREBASE_USERNAME = "stepp@stanford.edu";
    public static final String FIREBASE_PASSWORD = "csroxx";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // TODO: connect to Firebase
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(FIREBASE_USERNAME, FIREBASE_PASSWORD);

    }

    public void loginClick(View view) {
        String name = $ET(R.id.name).getText().toString();
        final String password = $ET(R.id.password).getText().toString();

        // TODO: look up this person's password in Firebase
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        // "Bart" => {id=123, ...}
        DatabaseReference students = fb.child("simpsons/students");
        Query bart = students.orderByChild("name").equalTo(name);
        bart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                // data has arrived!
                // data: key=123, value={name="bart", id=123, email="bart@..", password="..."}}
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("onCancelled: " + databaseError);
            }
        });
    }
}
