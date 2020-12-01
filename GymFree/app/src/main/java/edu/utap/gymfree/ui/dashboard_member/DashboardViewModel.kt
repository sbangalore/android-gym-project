package edu.utap.gymfree.ui.dashboard_member

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.firechat.FirestoreAuthLiveData
import edu.utap.gymfree.Location
import edu.utap.gymfree.MainActivity
import edu.utap.gymfree.R
import edu.utap.gymfree.Reservation

class DashboardViewModel : ViewModel() {
    private val TAG = "XXX-DashboardViewModel"
    private val OWNER_EMAIL = "owner@example.com"

    private val _text = MutableLiveData<String>().apply {
        value = "This is dashboard Fragment"
    }
    val text: LiveData<String> = _text
    private var reservations = MutableLiveData<List<Reservation>>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseAuthLiveData = FirestoreAuthLiveData()

    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }
    fun emailUid(): String? {
        return firebaseAuthLiveData.value?.email
    }

    fun observeReservations(): LiveData<List<Reservation>> {
        return reservations
    }
    fun deleteReservations(reservation: Reservation) {
//        db.collection("locations")
//                .document(location.rowID)
//                .delete()
//                .addOnSuccessListener {
//                    Log.d(javaClass.simpleName, "Deleted ${location.name}")
//                }
//                .addOnFailureListener { e ->
//                    Log.w(javaClass.simpleName, "Error deleting document", e)
//                }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }


    fun getReservations() {
//        Log.i(TAG, FirebaseAuth.getInstance().currentUser?.email)
        val user = FirebaseAuth.getInstance().currentUser
        if (!FirebaseAuth.getInstance().currentUser?.email.equals(OWNER_EMAIL)) {
            if (user != null) {
                db
                    .collectionGroup("reservations")
                    .whereEqualTo("userId", user.uid)
                    .addSnapshotListener { querySnapshot, ex ->
                        if (ex != null) {
                            Log.w(TAG, "listen:error", ex)
                            return@addSnapshotListener
                        }
                        Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                        reservations.value = querySnapshot.documents.mapNotNull {
                            Log.i(TAG + "XXXX", it.data.toString())
                            it.toObject(Reservation::class.java)
                        }
                    }

//                db
//                    .collection("locations")
//                    .document()
//                    .collection("timeslots")
//                    .document()
//                    .collection("reservations")
//                    .document(user.uid)
//                        .addSnapshotListener { value, error ->
//                            Log.d(TAG, "getLocations: ${value}")
//                        }
            }

        } else {
            reservations.value = listOf()
        }
    }
}