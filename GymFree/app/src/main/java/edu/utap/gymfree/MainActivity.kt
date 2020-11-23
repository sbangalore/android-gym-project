package edu.utap.gymfree


import android.R.attr.password
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.gymfree.ui.book.SelectFragment
import edu.utap.gymfree.ui.chat.ChatFragment
import edu.utap.gymfree.ui.create.CreateFragment
import kotlinx.android.synthetic.main.activity_main.*
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore


private lateinit var auth: FirebaseAuth
var TAG = "XXX-MainActivity"
private const val RC_SIGN_IN = 123
private var db: FirebaseFirestore = FirebaseFirestore.getInstance()


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.background = null
        navView.menu.getItem(1).isEnabled = false

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        Log.d(TAG, "XXX-USER : ${user?.displayName}; ${user?.email}")



        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_dashboard, R.id.navigation_create, R.id.navigation_chat))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fab.setOnClickListener {
            navController.navigate(R.id.navigation_create)
        }


//        if (auth.currentUser == null) {
//            createSignInIntent()
//        }
        createSignInIntent()



    }

    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(false).build())

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)
        // [END auth_fui_create_intent]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "SIGNED IN")
            Log.d(TAG, "USER : ${auth.currentUser}")
            Log.d(TAG, "name: ${auth.currentUser?.displayName}")
            Log.d(TAG, "email: ${auth.currentUser?.email}")
            val user = auth.currentUser

            val response = IdpResponse.fromResultIntent(data)

            val myNavHostFragment: NavHostFragment = nav_host_fragment as NavHostFragment
            val inflater = myNavHostFragment.navController.navInflater

            var graph = inflater.inflate(R.navigation.mobile_navigation)
            if(user?.email != "owner@example.com"){
                graph = inflater.inflate(R.navigation.mobile_navigation_member)
                Log.d(TAG, "XXX - WE ARE IN MEMBER")
            }
            else{
                Log.d(TAG, "XXX - WE ARE IN OWNER")
            }
            myNavHostFragment.navController.graph = graph

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                var rowID = db.collection("users").document().id
                val usersData = hashMapOf(
                        "uid" to user?.uid,
                        "email" to user?.email,
                        "name" to user?.displayName,
                        "rowID" to rowID
                )

                db
                        .collection("users")
                        .document(rowID)
                        .set(usersData)
                        .addOnSuccessListener { Log.d(TAG, "User successfully written!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }
            else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Log.d(TAG, "ERROR: Sign in Failed")
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    fun updateUI(currentUser: FirebaseUser?){
        if (currentUser?.email.equals(resources.getString(R.string.owner_email))) {
            Log.i(TAG, "Owner signed in")


//            fab.setOnClickListener {
//                supportFragmentManager
//                    .beginTransaction()
//                    .add(R.id.nav_host_fragment, CreateFragment.newInstance())
//                    .addToBackStack(null)
//                    .commit()
//            }

        } else {
//            Log.d(TAG, "member user")
//            supportFragmentManager
//                    .beginTransaction()
//                    .replace(R.id.nav_host_fragment, SelectFragment.newInstance())
//                    .commit()
        }
    }


}