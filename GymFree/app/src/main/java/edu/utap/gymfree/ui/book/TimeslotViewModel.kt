package edu.utap.gymfree.ui.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.firechat.FirestoreAuthLiveData
import edu.utap.gymfree.Location
import edu.utap.gymfree.MainActivity
import edu.utap.gymfree.R

class TimeslotViewModel : ViewModel() {
    private val TAG = "XXX-TimeslotViewModel"
    private val OWNER_EMAIL = "owner@example.com"

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    private var timeslots = MutableLiveData<List<Timeslot>>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }

    fun observeLocations(): LiveData<List<Timeslot>> {
        return timeslots
    }

    fun getLocations(locationID: String) {
        Log.i(TAG, FirebaseAuth.getInstance().currentUser?.email)
        if (FirebaseAuth.getInstance().currentUser?.email.equals(OWNER_EMAIL)) {
            Log.i(TAG, "Owner is logged in")
            timeslots.value = listOf()
            return
        } else {
            db
                    .collection("locations")
                    .document(locationID)
                    .collection("timeslots")
                    .addSnapshotListener { querySnapshot, ex ->
                        if (ex != null) {
                            Log.w(TAG, "listen:error", ex)
                            return@addSnapshotListener
                        }
                        Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                        timeslots.value = querySnapshot.documents.mapNotNull {
                            Log.i(TAG + "XXXX", it.data.toString())
                            it.toObject(Timeslot::class.java)
                        }
                    }
        }
    }
}