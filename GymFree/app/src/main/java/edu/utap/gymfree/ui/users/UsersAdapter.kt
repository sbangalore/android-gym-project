package edu.utap.gymfree.ui.users

import android.icu.text.SimpleDateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.gymfree.R
import edu.utap.gymfree.ui.timeslot.Timeslot
import java.util.*

class UsersAdapter(private var viewModel: UsersViewModel)
    : ListAdapter<User, UsersAdapter.VH>(UsersAdapter.Diff()) {
    private val TAG = "XXX-UsersAdapter"
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uId == newItem.uId
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uId == newItem.uId
                    && oldItem.name == newItem.name
        }
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul
        private var nameText = itemView.findViewById<TextView>(R.id.nameText)

        private fun bindElements(item: User,
                                 nameText: TextView
        ) {
            Log.i(TAG, item.toString())
            nameText.text = item.name
            // throws error
//            nameText.setOnClickListener {
//                val navHostFragment = (itemView.context as FragmentActivity).supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
//                val navController = navHostFragment.navController
//                navController.navigate(R.id.navigation_chat)
//            }
        }

        fun bind(item: User?) {
            Log.i(TAG, "bind")
            if (item == null) {
                Log.i(TAG, "NULL!")
                return
            }
            bindElements(
                    item,
                    nameText,
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        Log.d(TAG, "Create VH")
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_user, parent, false)
        Log.d(TAG, "Inflated VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i(TAG, getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}