package edu.utap.gymfree.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import edu.utap.gymfree.R

import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import edu.utap.gymfree.ui.dashboard_chat.ChatDashboardFragment
import edu.utap.gymfree.ui.timeslot.TimeslotFragment
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : Fragment() {
    private val TAG = "XXX-ChatFragment"

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var chatAdapter: FirestoreChatAdapter
    private var currentUser: FirebaseUser? = null
    private var fragmentUUID: String? = null
    private var receiverUid: String? = null
    private val OWNER_EMAIL = "owner@example.com"
    private val OWNER_UID = "x3or1FXSFFNyylZZeq4BkQnG3sf2"
    private var memberUid: String? = null
    private var memberName: String? = null

    companion object {
        fun newInstance(): ChatFragment {
            return ChatFragment()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        chatViewModel =
            ViewModelProvider(this).get(ChatViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_chat, container, false)
        return root
    }



//    private fun scrollToEnd() =
//        (chatAdapter.itemCount - 1).takeIf { it > 0 }?.let(chatRV::smoothScrollToPosition)
    private fun initRecyclerView()  {
        chatAdapter = FirestoreChatAdapter(chatViewModel)
        chatRV.adapter = chatAdapter
        chatRV.layoutManager = LinearLayoutManager(context)
        //https://stackoverflow.com/questions/26580723/how-to-scroll-to-the-bottom-of-a-recyclerview-scrolltoposition-doesnt-work
//        chatRV.viewTreeObserver.addOnGlobalLayoutListener {
//            scrollToEnd()
//        }
        // Dividers not so nice in chat
    }
    private fun clearCompose() {
        // XXX Write me
        composeMessageET.text.clear()
//        composePreviewIV.setImageResource(android.R.color.transparent)


    }
    private fun initAuth() {
        chatViewModel.observeFirebaseAuthLiveData().observe(viewLifecycleOwner, Observer {
            currentUser = it
        })

    }
    // For our phone, translate dp to pixels
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun initComposeSendIB() {
        // Send message button
        composeSendIB.setOnClickListener {
            if( composeMessageET.text.isNotEmpty()) {
                val chatRow = ChatRow().apply {
                    val cUser = currentUser
                    if(cUser == null) {
                        name = "unknown"
                        receiverName = "unknown"
                        ownerUid = "unknown"
                        receiverUid = "unknown"
                        memberUid = "unknown"
                        Log.d("ChatFragment", "XXX, currentUser null!")
                    } else {
                        // Member chat information
                        if (cUser.email.equals(OWNER_EMAIL)){
                            receiverUid = memberUid
                            receiverName = memberName
                            memberUid = OWNER_UID
                            name = "Owner"
                            Log.i(TAG, "NAME: $name")
                        } else {
                            receiverUid = OWNER_UID
                            receiverName = "Owner"
                            memberUid = cUser.uid
                            name = cUser.displayName
                        }
                        ownerUid = cUser.uid
                    }
                    message = composeMessageET.text.toString()
                    pictureUUID = fragmentUUID
                    clearCompose()
                }
                chatViewModel.saveChatRow(chatRow)
                chatViewModel.saveChatTime(chatRow)

            }
        }
    }

    // Something might have changed.  Redo query
    override fun onResume() {
        super.onResume()

        val bundle = this.arguments
        if (bundle != null) {
            memberUid =  bundle.getString("memberUid")
            memberName = bundle.getString("memberName")
        } else {
            memberUid = FirebaseAuth.getInstance().currentUser?.uid
        }

        if (memberUid != null) {
            chatViewModel.getChat(memberUid!!)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAuth()
        initComposeSendIB()
        initRecyclerView()

        chatViewModel.observeChat().observe(viewLifecycleOwner, Observer {
            Log.d(javaClass.simpleName, "Observe Chat $it")
            chatAdapter.submitList(it)
        })

        composeMessageET.setOnEditorActionListener { /*v*/_, actionId, event ->
            // If user has pressed enter, or if they hit the soft keyboard "send" button
            // (which sends DONE because of the XML)
            if ((event != null
                        &&(event.action == KeyEvent.ACTION_DOWN)
                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
                || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                (requireActivity() as MainActivity).hideKeyboard()
                composeSendIB.callOnClick()
            }
            true
        }
//        composeCameraIB.setOnClickListener {
//            chatViewModel.takePhoto {
//                Log.d(javaClass.simpleName, "uuid $it")
//                fragmentUUID = it
//                composePreviewIV.doOnLayout {view ->
//                    view.updateLayoutParams {
//                        width = chatViewModel.fourFifthWidthPx
//                    }
//                }
//                composePreviewIV.visibility = View.VISIBLE
//                chatViewModel.glideFetch(it, composePreviewIV)
//            }
//        }
//        composePreviewIV.visibility = View.GONE
        //Log.d(javaClass.simpleName, "vm 1/5 ${viewModel.oneFifthWidthPx} mine ${(resources.displayMetrics.widthPixels / 5).toInt()}")
    }
}
