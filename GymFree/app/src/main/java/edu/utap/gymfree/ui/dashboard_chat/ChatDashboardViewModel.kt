package edu.utap.gymfree.ui.dashboard_chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import edu.utap.gymfree.ui.users.User

class ChatDashboardViewModel : ViewModel() {
    private val TAG = "XXX-ChatDashboardViewModel"

    private var users = MutableLiveData<List<User>>()
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun observeUsers(): LiveData<List<User>> {
        return users
    }

    fun getUsers(){
        val users = db
                .collection("users")
                .orderBy("lastMessageTime", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot, ex ->
                    if (ex != null) {
                        Log.w(TAG, "listen:error", ex)
                        return@addSnapshotListener
                    }
                    Log.d(TAG, "fetch ${querySnapshot!!.documents.size}")
                    users.value = querySnapshot.documents.mapNotNull {
                        Log.i(TAG, it.data.toString())
                        it.toObject(User::class.java)
                    }.distinct()
                }
    }
}
