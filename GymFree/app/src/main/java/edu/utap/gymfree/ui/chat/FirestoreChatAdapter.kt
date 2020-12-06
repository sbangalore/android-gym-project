package edu.utap.gymfree.ui.chat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.utap.gymfree.R

class FirestoreChatAdapter(private var viewModel: ChatViewModel)
    : ListAdapter<ChatRow, FirestoreChatAdapter.VH>(Diff()) {
    // This class allows the adapter to compute what has changed
    class Diff : DiffUtil.ItemCallback<ChatRow>() {
        override fun areItemsTheSame(oldItem: ChatRow, newItem: ChatRow): Boolean {
            return oldItem.rowID == newItem.rowID
        }

        override fun areContentsTheSame(oldItem: ChatRow, newItem: ChatRow): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.ownerUid == newItem.ownerUid
                    && oldItem.message == newItem.message
                    && oldItem.pictureUUID == newItem.pictureUUID
                    && oldItem.timeStamp == newItem.timeStamp
        }
    }
    companion object {
        private val iphoneTextBlue = Color.parseColor("#1982FC")
        private val iphoneMessageGreen = ColorDrawable(Color.parseColor("#43CC47"))
        private val dimGrey = Color.parseColor("#C5C5C5")
        private val dateFormat =
            SimpleDateFormat("hh:mm:ss MM-dd-yyyy")
        private val transparentDrawable = ColorDrawable(Color.TRANSPARENT)
    }

    // ViewHolder pattern minimizes calls to findViewById
    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // May the Lord have mercy upon my soul
        private var myTimeTV = itemView.findViewById<TextView>(R.id.chatTimeTV)
        private var myTextTV = itemView.findViewById<TextView>(R.id.chatTextTV)
        private var myTextCV = itemView.findViewById<CardView>(R.id.textCV)
        private var otherTimeTV = itemView.findViewById<TextView>(R.id.otherChatTimeTV)
        private var otherTextTV = itemView.findViewById<TextView>(R.id.otherChatTextTV)
        private var otherTextCV = itemView.findViewById<CardView>(R.id.otherTextCV)
        init {
            myTextCV.isLongClickable = true
        }
        private fun goneElements(timeTV: TextView, textTV: TextView,
                                 textCV: CardView) {
            timeTV.visibility = View.GONE
            textTV.visibility = View.GONE
            textCV.visibility = View.GONE
        }
        private fun visibleElements(timeTV: TextView, textTV: TextView,
                                 textCV: CardView) {
            timeTV.visibility = View.VISIBLE
            textTV.visibility = View.VISIBLE
            textCV.visibility = View.VISIBLE
        }
        private fun bindElements(item: ChatRow, backgroundColor: Int, textColor: Int,
                                 timeTV: TextView, textTV: TextView,
                                 textCV: CardView) {
            // Set background on CV, not TV because...layout is weird
            textCV.setCardBackgroundColor(backgroundColor)
            textTV.setTextColor(textColor)
            textTV.text = item.message
            textCV.setOnLongClickListener {
                viewModel.deleteChatRow(item)
                true
            }
            // XXX Write me, bind picIV using pictureUUID.
//            val pictureUUID = item.pictureUUID
//            if (pictureUUID != null) {
//                picIV.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
//            }


            if (item.timeStamp == null) {
                timeTV.text = ""
            } else {
                //Log.d(javaClass.simpleName, "date ${item.timeStamp}")
                timeTV.text = dateFormat.format(item.timeStamp.toDate())
            }
        }
        fun bind(item: ChatRow?) {
            if (item == null) return
            if (viewModel.myUid() == item.ownerUid) {
                goneElements(otherTimeTV, otherTextTV, otherTextCV)
                visibleElements(myTimeTV, myTextTV, myTextCV)
                bindElements(
                    item, iphoneTextBlue, Color.WHITE,
                    myTimeTV, myTextTV, myTextCV)
            } else {
                goneElements(myTimeTV, myTextTV, myTextCV)
                visibleElements(otherTimeTV, otherTextTV, otherTextCV)
                bindElements(
                    item, dimGrey, Color.BLACK,
                    otherTimeTV, otherTextTV, otherTextCV)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_chat, parent, false)
        //Log.d(MainActivity.TAG, "Create VH")
        return VH(itemView)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        //Log.d(MainActivity.TAG, "Bind pos $position")
        holder.bind(getItem(holder.adapterPosition))
    }
}
