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

class SelectViewModel : ViewModel() {
    private val TAG = "XXX-DashboardViewModel"
    private val OWNER_EMAIL = "owner@example.com"

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    private var locations = MutableLiveData<List<Location>>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }

    fun observeLocations(): LiveData<List<Location>> {
        return locations
    }

    fun getLocations() {
        Log.i(TAG, FirebaseAuth.getInstance().currentUser?.email)
        if (FirebaseAuth.getInstance().currentUser?.email.equals(OWNER_EMAIL)) {
            Log.i(TAG, "Owner is logged in")
            locations.value = listOf()
            return
        } else {
            db
            .collection("locations")
            .orderBy("timeStamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, ex ->
                if (ex != null) {
                    Log.w(TAG, "listen:error", ex)
                    return@addSnapshotListener
                }
                Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                locations.value = querySnapshot.documents.mapNotNull {
                    Log.i(TAG + "XXXX", it.data.toString())
                    it.toObject(Location::class.java)
                }
            }
        }
    }
}