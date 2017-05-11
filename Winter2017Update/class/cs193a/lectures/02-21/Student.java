package cs193a.stanford.edu.simpsongrades;

import java.io.Serializable;

/**
 * Represents information about one student in the data set.
 */
public class Student implements Serializable {
    String email;
    int id;
    String name;
    String password;

    public Student() {}

    public String toString() {
        return "Student {id=" + id + ", name=" + name
                + ", email=" + email + ", password=" + password + "}";
    }
}
