/*
 * CS 193A, Marty Stepp
 * This program is a quick demo of the Firebase remote database system.
 * The program fetches all of a student's grades and displays them in a ListView.
 */

package cs193a.stanford.edu.simpsongrades;

import android.content.Intent;
import android.os.Bundle;
import com.google.firebase.database.*;
import java.util.*;
import stanford.androidlib.*;

public class GradesActivity extends SimpleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grades);

        // extract parameters from intent
        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", /* default */ 0);
        final String name = intent.getStringExtra("name");
        $TV(R.id.heading).setText(name + "'s Grades");

        // TODO: look up grades for this student
        // SQL:  SELECT * FROM grades WHERE student_id = id;
        final DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        Query query = fb.child("simpsons/grades")
                .orderByChild("student_id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot data) {
                // each child: key   = -KBNIF3XuUYw81zdzQmB,
                //             value = {course_id: 10001, course_name: "Computer Science 142", grade: "B-",  student_id: 123}
                ArrayList<String> items = new ArrayList<>();
                for (DataSnapshot child : data.getChildren()) {
                    Grade grade = child.getValue(Grade.class);
                    items.add(grade.grade + " in " + grade.course_name);
                }
                SimpleList.with(GradesActivity.this)
                        .setItems(R.id.gradelist, items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("onCancelled: " + databaseError);
            }
        });
    }

    // the data looks something like this:
    // {
    //   -KBNIF3XuUYw81zdzQmC={student_id=123, grade="C", course_id=10002, course_name="GSB 143"},
    //   -KBNIF3XuUYw81zdzQmB={student_id=123, grade="B-", course_id=10001, course_name="Computer Science 142"}
    // }
}
