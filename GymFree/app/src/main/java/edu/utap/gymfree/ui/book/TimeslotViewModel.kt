package edu.utap.gymfree.ui.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
    private var locID = MutableLiveData<String>()

    fun myUid(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun myUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
//    val stamp1 = mapOf(
//            "startTime" to startTime,
//            "endTime" to endTime,
//            "rowId" to db.collection("timeslots").document().id
//    )
//    // add subcollection timeslot
//    db
//    .collection("locations")
//    .document(location.rowID)
//    .collection("timeslots")
//    .add(stamp1)

    fun addReservation(timeslot: Timeslot, locationId: String){
        Log.d(TAG, "inside addreservation timeslot rowid: ${timeslot.rowId}")
        Log.d(TAG, "XXX addReservation: $locationId timeslot rowid: ${timeslot.rowId}")
        val ref = db.collection("users").document(myUid().toString()).get()
        ref.addOnSuccessListener {
            val name = it.getString("name")
            Log.d(TAG, "NAME OF THE USER: $name UID: ${myUid()}")
            val resID = db.collection("reservations").document().id
            val reservation = mapOf(
                "name" to name,
                "rowId" to resID,
                "userId" to myUid(),
                "startTime" to timeslot.startTime,
                "endTime" to timeslot.endTime,
                "timeslotId" to timeslot.rowId,
                "locationId" to locationId

            )
            db
                .collection("locations")
                .document(locationId)
                .collection("timeslots")
                .document(timeslot.rowId)
                .collection("reservations")
                .document(myUid()!!)
                .set(reservation)
        }
    }

    fun checkReservation(timeslot: Timeslot, locationId: String){
        db
                .collection("locations")
                .document(locationId)
                .collection("timeslots")
                .document(timeslot.rowId)
                .collection("reservations")
                .document(myUid()!!)
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                        Log.d(TAG, "checkReservation: ")

                    }
                }

    }
    fun numReservations(timeslot: Timeslot, locationId: String){
        val numReservations = db
                .collection("locations")
                .document(locationId)
                .collection("timeslots")
                .document(timeslot.rowId)
                .collection("reservations")
                .get()
        numReservations.addOnSuccessListener {
            it.size()
        }

    }


    fun observeTimeslots(): LiveData<List<Timeslot>> {
        return timeslots
    }

    fun observeLocID(): MutableLiveData<String> {
        return locID
    }

    fun getTimeslots(locationID: String) {
        locID.value = locationID
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
                .orderBy("startTime", Query.Direction.ASCENDING)
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