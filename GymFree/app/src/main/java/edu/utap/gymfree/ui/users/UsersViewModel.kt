package edu.utap.gymfree.ui.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.firechat.FirestoreAuthLiveData

class UsersViewModel : ViewModel() {
    private val TAG = "XXX-TimeslotViewModel"
    private val OWNER_EMAIL = "owner@example.com"

    private var users = MutableLiveData<List<User>>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()
    private var locID = MutableLiveData<String>()
    private var timeID = MutableLiveData<String>()

    fun myUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun myUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }

    fun observeLocID(): MutableLiveData<String> {
        return locID
    }

    fun observTimeID(): MutableLiveData<String> {
        return timeID
    }

    fun observeUsers(): LiveData<List<User>> {
        return users
    }

    fun getUsers(locationId: String, timeslotId: String){
        locID.value = locationId
        timeID.value = timeslotId
        val reservations = db
                .collection("locations")
                .document(locationId)
                .collection("timeslots")
                .document(timeslotId)
                .collection("reservations")
                .orderBy("name", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w(TAG, "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                    users.value = querySnapshot.documents.mapNotNull {
                        Log.i(TAG, it.data.toString())
                        it.toObject(User::class.java)
                    }
                }
    }
}
