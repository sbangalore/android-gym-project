package edu.utap.gymfree.ui.dashboard_member

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.widget.Toast
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
import java.util.*

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
    private val sdf = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)

    fun myUid(): String? {
        return firebaseAuthLiveData.value?.uid
    }
    fun emailUid(): String? {
        return firebaseAuthLiveData.value?.email
    }

    fun observeReservations(): LiveData<List<Reservation>> {
        return reservations
    }
    fun deleteReservation(reservation: Reservation) {
        val resId = reservation.rowId
        val locId = reservation.locationId!!

        db
            .collection("locations")
            .document(locId)
            .collection("timeslots")
            .get()
            .addOnSuccessListener { timeslots ->
                for (time in timeslots) {
                    val timeID = time.getString("rowId")
                    if (timeID != null) {
                        db.collection("locations")
                            .document(locId)
                            .collection("timeslots")
                            .document(timeID)
                            .collection("reservations")
                            .get()
                            .addOnSuccessListener { reservations ->
                                for (r in reservations) {
                                    val docId = r.getString("userId")

                                    val rowId = r.getString("rowId")
                                    if (rowId == resId && docId != null){
                                        db.collection("locations")
                                            .document(locId)
                                            .collection("timeslots")
                                            .document(timeID)
                                            .collection("reservations")
                                            .document(docId)
                                            .delete()
                                            .addOnSuccessListener {
                                                Log.d(javaClass.simpleName, "Deleted ${reservation.name}")
                                            }
                                    }
                                }
                            }
                    }
                }
            }
    }

    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }


    fun getReservations() {
        val currentTime = Calendar.getInstance()
        val reservationTime = Calendar.getInstance()

//        Log.i(TAG, FirebaseAuth.getInstance().currentUser?.email)
        val user = FirebaseAuth.getInstance().currentUser
        if (!FirebaseAuth.getInstance().currentUser?.email.equals(OWNER_EMAIL)) {
            if (user != null) {
                db
                    .collectionGroup("reservations")
                    .whereEqualTo("userId", user.uid)
                    .orderBy("startTime", Query.Direction.ASCENDING)
                    .addSnapshotListener { querySnapshot, ex ->
                        if (ex != null) {
                            Log.w(TAG, "listen:error", ex)
                            return@addSnapshotListener
                        }
                        Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                        reservations.value = querySnapshot.documents.mapNotNull {
                            Log.i(TAG + "XXXX", it.data.toString())
                            val endTime = it.getString("endTime")
                            reservationTime.time = sdf.parse(endTime)
                            if (currentTime < reservationTime){
                                it.toObject(Reservation::class.java)
                            }
                            else{
                                null
                            }
                        }
                    }
            }

        } else {
            reservations.value = listOf()
        }
    }
}