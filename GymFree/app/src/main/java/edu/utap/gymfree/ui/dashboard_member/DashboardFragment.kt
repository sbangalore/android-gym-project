package edu.utap.gymfree.ui.dashboard_member

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import edu.utap.gymfree.R
import kotlinx.android.synthetic.main.fragment_dashboard.*



class DashboardFragment : Fragment() {
    private val TAG = "XXX-DashboardFragment"
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var reservationAdapter: FireStoreReservationAdapter

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun initRecyclerView()  {
        reservationAdapter = FireStoreReservationAdapter(viewModel)
        locationRV.adapter = reservationAdapter
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
        viewModel.getReservations()

        viewModel.observeReservations().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe Reservations $it")
            Log.i("XXX-DBFragment", it.toString())
            reservationAdapter.submitList(it)
        })

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
                    else{
                        Log.d(TAG, "NAME ALREADY EXISTS: $name")
                        addUserName.visibility = View.GONE
                    }
                }
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getReservations()
    }
}