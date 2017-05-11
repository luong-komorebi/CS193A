/*
 * SimpleFirebase library, by Marty Stepp
 *
 * This library is intended to make it easier to use the Firebase remote database system.
 * This incomplete library resource is a work in progress.
 * Please report any bugs/issues to the author.
 * 
 * ===============================================================================================
 * Installation/usage instructions:
 * - Create a folder in your project named:
 *     app/src/main/java/stanford/androidlib/data/firebase/
 * - Save this file into that folder.
 *
 * Documentation available at:
 * - http://web.stanford.edu/class/cs193a/lib/
 *
 * @author Marty Stepp (stepp AT stanford)
 * @version 2017/02/21
 * - initial version (CS 193A 17wi)
 * ===============================================================================================
 *
 * TODO: add signInWithGoogleAccount()
 */

package stanford.androidlib.data.firebase;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.google.android.gms.tasks.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

/**
 * This class provides a simplified interface to the Firebase remote database system.
 * Methods require the context such as an Activity, so you
 * must call {@code SimpleFirebase.with(yourActivity)} first.
 *
 * By design of Firebase, most methods (get, set, signIn, watch) are asynchronous;
 * they return immediately and will notify your code when their results are available by contacting
 * an appropriate listener.
 *
 * The most common intended usage pattern is to make your Activity implement GetListener,
 * SetListener, SignInListener, etc. or to write small inner classes that implement these interfaces.
 * We recommend you also implement ErrorListener so that you can see when operations fail on
 * your database, or at least call setLogging(true) to turn on a default error listener.
 */
public final class SimpleFirebase {
    /**
     * An event listener that can respond to database errors.
     */
    public interface ErrorListener {
        public void onError(DatabaseError error);
    }

    /**
     * An event listener that can respond to the result of get() calls.
     */
    public interface GetListener {
        public void onGet(String path, DataSnapshot data);
    }

    /**
     * An event listener that can respond to the result of set() calls.
     */
    public interface SetListener {
        public void onSet(String path);
    }

    /**
     * An event listener that can respond to the result of signIn() calls.
     */
    public interface SignInListener {
        public void onSignIn(boolean successful);
    }

    /**
     * An event listener that can respond to the result of watch() calls.
     */
    public interface WatchListener {
        public void onDataChange(DataSnapshot data);
    }

    // tag for debug logging
    private static final String LOG_TAG = "SimpleFirebase";

    // whether the Firebase db has been initialized
    private static boolean initialized = false;

    private Context context;                         // activity/fragment used to load resources
    private FirebaseAuth mAuth = null;               // authentication/signin object
    private FirebaseUser user = null;                // currently signed in user (null if none)
    private ErrorListener errorListener;             // responds to database errors (null if none)
    private DatabaseError lastDatabaseError = null;  // last database error that occurred (null if none)
    private boolean signInComplete = false;          // true if finished signing in to db
    private boolean logging = false;                 // true if we should Log various debug messages

    /**
     * Returns a new SimpleFirebase instance using the given activity or other context.
     */
    public static SimpleFirebase with(Context context) {
        SimpleFirebase fb = new SimpleFirebase();
        fb.context = context;

        if (!initialized) {
            synchronized (SimpleFirebase.class) {
                if (!initialized) {
                    FirebaseApp.initializeApp(context);
                    initialized = true;
                }
            }
        }

        return fb;
    }

    /**
     * Returns a child of the overall database; equivalent to Firebase's child() method.
     */
    public DatabaseReference child(String query) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        return fb.child(query);
    }

    /**
     * Clears this object's record of any past database error.
     * If there was no past error, has no effect.
     */
    public void clearLastDatabaseError() {
        lastDatabaseError = null;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase get(String path) {
        return get(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase get(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);
        return getWatchHelper(path, child, listener, /* watch */ false);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     * @param ref a Query object containing an absolute database reference
     */
    public SimpleFirebase get(Query ref) {
        return get(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * The given GetListener will be notified when the data has arrived.
     * @param ref a Query object containing an absolute database reference
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase get(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ false);
    }

    // common helper code for all overloads of get() and watch()
    private SimpleFirebase getWatchHelper(String path, Query ref, GetListener listener, boolean watch) {
        if (ref == null) {
            return this;
        } else if (path == null) {
            path = ref.toString();
        }
        if (logging) { Log.d(LOG_TAG, "get/watch: path=" + path); }

        // listen to the data coming back
        if (listener == null && context instanceof GetListener) {
            listener = (GetListener) context;
        }
        InnerValueEventListener valueListener = new InnerValueEventListener();
        valueListener.path = path;
        valueListener.getListener = listener;

        // either listen once (get) or keep listening (watch)
        if (watch) {
            ref.addValueEventListener(valueListener);
        } else {
            ref.addListenerForSingleValueEvent(valueListener);
        }
        return this;
    }

    /**
     * Returns the user who is currently signed in, or null if no user is signed in.
     */
    public FirebaseUser getCurrentUser() {
        return user;
    }

    /**
     * Returns true if there has been a database error that has not been cleared.
     */
    public boolean hasLastDatabaseError() {
        return lastDatabaseError != null;
    }

    /**
     * Returns true if a user is currently signed in.
     */
    public boolean isSignedIn() {
        return signInComplete;
    }

    /**
     * Returns the last database error that occurred, or null if no error has occurred.
     */
    public DatabaseError lastDatabaseError() {
        return lastDatabaseError;
    }

    /**
     * Signs in with the given username and password; an alias for signIn().
     */
    public SimpleFirebase login(String username, String password) {
        return signIn(username, password);
    }

    /**
     * Performs a query on the Firebase database.
     * Similar to the Firebase child() method.
     * Common intended usage:
     *
     * <pre>
     * SimpleFirebase fb = SimpleFirebase.with(this);
     * fb.get(fb.query("foo/bar/baz")
     *     .orderByChild("quux")
     *     .limitToFirst(1));
     * </pre>
     *
     * @param query absolute path in database such as "foo/bar/baz"
     */
    public DatabaseReference query(String query) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        return fb.child(query);
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, Object value) {
        return setHelper(path, /* key */ "", value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SetListener interface, it will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param key child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, String key, Object value) {
        return setHelper(path, key, value, /* listener */ null);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, Object value, SetListener listener) {
        return setHelper(path, /* key */ "", value, /* listener */ listener);
    }

    /**
     * Sets the given location in the database to store the given value.
     * The given SetListener will be notified when the data has been synced.
     * @param path an absolute database path such as "foo/bar/baz"
     * @param key child key name within that path, such as "quux" to indicate "foo/bar/baz/quux"
     * @param value the value to store there in the database
     */
    public SimpleFirebase set(String path, String key, Object value, SetListener listener) {
        return setHelper(path, key, value, /* listener */ listener);
    }

    // helper for common set() code
    private SimpleFirebase setHelper(String path, String key, Object value, SetListener listener) {
        if (listener == null && context instanceof SetListener) {
            listener = (SetListener) context;
        }

        if (logging) { Log.d(LOG_TAG, "set: path=" + path + ", key=" + key + ", value=" + value); }

        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = null;
        if (key == null || key.isEmpty()) {
            child = fb.child(path);
        } else {
            if (!path.endsWith("/")) {
                path += "/";
            }
            path += key;
            child = fb.child(path);
        }
        if (child == null) {
            return this;
        }

        if (listener != null) {
            InnerCompletionListener myListener = new InnerCompletionListener();
            myListener.path = path;
            myListener.set = listener;
            child.setValue(value, listener);
        } else {
            child.setValue(value);
        }
        return this;
    }

    /**
     * Sets the given listener object to be notified of future database errors.
     * Pass null to disable listening for database errors.
     * If the context passed to with() implements ErrorListener, it will be automatically
     * notified of database errors even if you don't call setErrorListener.
     */
    public SimpleFirebase setErrorListener(ErrorListener listener) {
        this.errorListener = listener;
        return this;
    }

    /**
     * Sets whether the SimpleFirebase library should print log messages for debugging.
     */
    public SimpleFirebase setLogging(boolean logging) {
        this.logging = logging;
        if (errorListener == null) {
            // set up a default error logging listener if there is none
            errorListener = new InnerErrorListener();
        }
        return this;
    }

    /**
     * Signs in with the given username and password.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SignInListener interface, it will be notified when the sign-in is complete.
     * @param username user name such as "jsmith12"
     * @param password user password such as "abc123"
     */
    public SimpleFirebase signIn(String username, String password) {
        return signInHelper(username, password, /* listener */ null);
    }

    /**
     * Signs in with the given username and password.
     * The given SignInListener will be notified when the user has finished signing in.
     * @param username user name such as "jsmith12"
     * @param password user password such as "abc123"
     */
    public SimpleFirebase signIn(String username, String password, SignInListener listener) {
        return signInHelper(username, password, listener);
    }

    /**
     * Signs in to the database anonymously.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the SignInListener interface, it will be notified when the sign-in is complete.
     */
    public SimpleFirebase signInAnonymously() {
        return signInHelper(/* username */ null, /* password */ null, /* listener */ null);
    }

    /**
     * Signs in to the database anonymously.
     * The given SignInListener will be notified when the user has finished signing in.
     */
    public SimpleFirebase signInAnonymously(SignInListener listener) {
        return signInHelper(/* username */ null, /* password */ null, listener);
    }

    // helper for common code in sign-in process
    private SimpleFirebase signInHelper(final String username, final String password, final SignInListener listener) {
        if (logging) { Log.d(LOG_TAG, "signIn: username=" + username + ", password=" + password); }
        InnerAuthListener innerListener = new InnerAuthListener();
        if (listener != null) {
            innerListener.signin = listener;
        } else if (context instanceof SignInListener) {
            innerListener.signin = (SignInListener) context;
        }

        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(innerListener);
        }
        signInComplete = false;
        if (username == null || username.isEmpty()) {
            // anonymous sign-in
            if (context instanceof Activity) {
                mAuth.signInAnonymously()
                        .addOnCompleteListener((Activity) context, innerListener);
            } else {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(innerListener);
            }
        } else {
            // regular sign-in
            if (context instanceof Activity) {
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener((Activity) context, innerListener);
            } else {
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(innerListener);
            }
        }
        return this;
    }

    /**
     * Signs out of the database, if currently signed in.
     * If not signed in, has no effect.
     */
    public SimpleFirebase signOut() {
        if (mAuth != null) {
            mAuth.signOut();
            this.user = null;
            this.signInComplete = false;
            mAuth = null;
        }
        return this;
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * @param path absolute database path such as "foo/bar/baz"
     */
    public SimpleFirebase watch(String path) {
        return watch(path, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * @param path absolute database path such as "foo/bar/baz"
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase watch(String path, final GetListener listener) {
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference child = fb.child(path);
        return getWatchHelper(path, child, listener, /* watch */ true);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * If the context passed to with() when creating this SimpleFirebase object implements
     * the GetListener interface, it will be notified when the data arrives.
     * @param ref a Query object representing an absolute database path
     */
    public SimpleFirebase watch(Query ref) {
        return watch(ref, /* listener */ null);
    }

    /**
     * Initiates a request to retrieve the data at the given path in the database.
     * @param ref a Query object representing an absolute database path
     * @param listener object to notify when the data has arrived
     */
    public SimpleFirebase watch(final Query ref, final GetListener listener) {
        return getWatchHelper(/* path */ null, ref, listener, /* watch */ true);
    }

    /*
     * Helper class that listens for authentication results; used by signin()
     */
    private class InnerAuthListener implements FirebaseAuth.AuthStateListener,
            OnCompleteListener<AuthResult> {
        private SignInListener signin;

        @Override
        public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
            if (logging) { Log.d(LOG_TAG, "onAuthStateChanged: " + firebaseAuth); }

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser != null) {
                // User is signed in
                user = currentUser;
                if (logging) { Log.d(LOG_TAG, "signed in UID: " + currentUser.getUid()); }
            } else {
                // User is signed out
                if (logging) { Log.d(LOG_TAG, "signed out"); }
            }
            signInComplete = true;
            if (signin != null) {
                signin.onSignIn(signInComplete);
            }
        }

        @Override
        public void onComplete(Task<AuthResult> task) {
            Log.d(LOG_TAG, "signin complete: successful? " + task.isSuccessful());
            signInComplete = true;
            if (signin != null) {
                signin.onSignIn(task.isSuccessful());
            }
        }
    }

    /*
     * Helper class that listens for database task completion results; used by set().
     */
    private class InnerCompletionListener implements DatabaseReference.CompletionListener {
        private boolean complete = false;
        private DatabaseError error;
        private SetListener set;
        private String path;

        @Override
        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
            complete = true;
            this.error = databaseError;
            lastDatabaseError = this.error;
            if (set != null) {
                set.onSet(path);
            }
            if (errorListener != null) {
                errorListener.onError(databaseError);
            }
        }
    }

    /*
     * Helper class that listens for database errors and logs them to the Android Studio console.
     */
    private class InnerErrorListener implements ErrorListener {
        @Override
        public void onError(DatabaseError error) {
            Log.d(LOG_TAG, " *** DATABASE ERROR: " + error);
        }
    }

    /*
     * Helper class that listens for data arrival results; used by get() and watch().
     */
    private class InnerValueEventListener implements ValueEventListener {
        private String path = null;
        private GetListener getListener;

        @Override
        public void onDataChange(DataSnapshot data) {
            if (getListener != null) {
                getListener.onGet(path, data);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            lastDatabaseError = databaseError;
            if (errorListener != null) {
                errorListener.onError(databaseError);
            }
        }
    }
}
