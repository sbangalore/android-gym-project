package edu.utap.gymfree.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.Barrier
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GithubAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PlayGamesAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import com.google.firebase.ktx.Firebase
import edu.utap.gymfree.R
import edu.utap.gymfree.ui.book.SelectFragment
import kotlinx.android.synthetic.main.fragment_dashboard.*



class DashboardFragment : Fragment() {
    private val TAG = "XXX-DashboardFragment"
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var locationAdapter: FireStoreLocationAdapter

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun initRecyclerView()  {
        locationAdapter = FireStoreLocationAdapter(viewModel)
        locationRV.adapter = locationAdapter
        locationRV.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        viewModel.getLocations()

        viewModel.observeLocations().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe Chat $it")
            Log.i("XXX-DBFragment", it.toString())
            locationAdapter.submitList(it)
        })

        // NOTE: AFAIK, the admin SDK has to be handled through a seperate app entirely
        //       => we can't remove or add users here

//        addUserButton.setOnClickListener {
//            val emailToAdd = addEmail.editText?.text.toString()
//            if (emailToAdd == "") {
//                addEmail.error = "Email is required."
//            }
//
//            val settings = ActionCodeSettings.newBuilder()
//                    .setHandleCodeInApp(true)
//                    .setUrl("https://www.google.com/")
//                    .setAndroidPackageName(
//                            "edu.utap.gymfree",
//                            true,
//                            "27"
//                    )
//                    .build()
//
//            FirebaseAuth.getInstance().sendSignInLinkToEmail(emailToAdd, settings)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Log.d(TAG, "Email sent.")
//                            Toast.makeText(activity?.applicationContext, "Added $emailToAdd.", Toast.LENGTH_SHORT).show()
//                        }
//                        }
//
//        }

//        removeUserButton.setOnClickListener {
//            val emailToRemove = removeEmail.editText?.text.toString()
//            if (emailToRemove == "") {
//                addEmail.error = "Email is required."
//            } else if (emailToRemove == resources.getString(R.string.owner_email)) {
//                removeEmail.error = "Please contact support to delete your account."
//            } else {
//                Log.i(TAG,"Do this properly...")
//                Toast.makeText(activity?.applicationContext, "Removed $emailToRemove.", Toast.LENGTH_SHORT).show()
//            }
//        }
        // inputfield to add user name
        val user = viewModel.getCurrentUser()

        if (user != null) {
            db
                .collection("users")
                .document(user.uid)
                .addSnapshotListener { value, error ->
                    var name = value?.getString("name").toString()
                    Log.d(TAG, "XXX NAME: $name")
                    if (name == "null") {
                        Log.d(TAG, "XXX NAME IS EMPTY: $name")
                        addUserName.visibility = View.VISIBLE
                        addNameButton.setOnClickListener {
                            val nameToAdd = addName.editText?.text.toString()
                            if (nameToAdd == ""){
                                addName.error = "Name is required"
                            }
                            else{
                                val rowID = db.collection("users").document().id
                                val uid = user.uid

                                val usersData = hashMapOf(
                                        "uid" to user.uid,
                                        "email" to user.email,
                                        "name" to nameToAdd,
                                        "rowID" to rowID
                                )

                                db
                                        .collection("users")
                                        .document(uid)
                                        .set(usersData, SetOptions.merge())
                                        .addOnSuccessListener { Log.d(edu.utap.gymfree.TAG, "User successfully written!") }
                                        .addOnFailureListener { e -> Log.w(edu.utap.gymfree.TAG, "Error writing document", e) }
                            }
                            addUserName.visibility = View.GONE
                        }
                    }
                }
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getLocations()
    }
}