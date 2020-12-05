package edu.utap.gymfree.ui.dashboard_chat

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.firestore.FirebaseFirestore
import edu.utap.gymfree.R
import edu.utap.gymfree.ui.chat.ChatFragment
import edu.utap.gymfree.ui.users.User
import java.util.*


class ChatDashboardAdapter(private var viewModel: ChatDashboardViewModel)
    : ListAdapter<User, ChatDashboardAdapter.VH>(Diff()) {
    private val TAG = "XXX-ChatDashboardAdapter"
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

    companion object {
        private val dateFormat = SimpleDateFormat("dd MMM, yyyy at hh:mm")
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul

        private var rowHolder = itemView.findViewById<MaterialCardView>(R.id.userRow)
        private var nameText = itemView.findViewById<TextView>(R.id.nameText)

        private fun bindElements(
            item: User,
            nameText: TextView,
            rowHolder: MaterialCardView
        ) {
            Log.i(TAG, item.toString())
            nameText.text = item.name

            rowHolder.setOnClickListener {
                Log.d(TAG, "XXX clicked on user: ${item.name}")
                val userId = item.uId

                val bundle = Bundle()
                bundle.putString("memberUid", item.uId)
                bundle.putString("memberName", item.name)
                Log.i(TAG, "ITEM.NAME ${item.name}")
                val chatFragment = ChatFragment()
                chatFragment.arguments = bundle

                (itemView.context as FragmentActivity)
                        .supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, chatFragment)
                        .addToBackStack("select")
                        .commit()
            }

        }

        fun bind(item: User) {
            if (item == null) {
                Log.i("XXX-ADAPTER-bind", "NULL!")
                return
            }

            val dateFormat = SimpleDateFormat("MM dd HH:mm:ss z yyyy", Locale.ENGLISH)
            bindElements(
                item,
                nameText,
                rowHolder
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_user, parent, false)
        Log.d(TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        Log.i("XXX-adapter", getItem(holder.adapterPosition).toString())
        holder.bind(getItem(holder.adapterPosition))
    }
}
