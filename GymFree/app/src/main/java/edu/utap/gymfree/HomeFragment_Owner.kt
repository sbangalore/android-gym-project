package edu.utap.gymfree

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment

class HomeFragment_Owner : Fragment(R.layout.fragment_create) {
}
//    private val viewModel: MainViewModel by activityViewModels()
//    private lateinit var locationAdapter: FirestoreLocationAdapter
//    private var fragmentUUID: String? = null
//
//    private fun initRecyclerView()  {
//        locationAdapter = LocationAdapter(viewModel)
//        locationRV.adapter = LocationAdapter
//        locationRV.layoutManager = LinearLayoutManager(context)
//    }
//
//    private fun initComposeAddLocation() {
//        // Send message button
//        composeSendIB.setOnClickListener {
//            if(composeMessageET.text.isNotEmpty()) {
//                val location = Location().apply {
//                    val cUser = currentUser
//                    if(cUser == null) {
//                        name = "unknown"
//                        ownerUid = "unknown"
//                        Log.d("HomeFragment", "XXX, currentUser null!")
//                    } else {
//                        name = cUser.displayName
//                        ownerUid = cUser.uid
//                    }
//                    message = composeMessageET.text.toString()
//                    pictureUUID = fragmentUUID
//                    clearCompose()
//                }
//                viewModel.saveLocation(location)
//            }
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        initAuth()
//        initComposeSendIB()
//        initRecyclerView()
//
//        viewModel.observeChat().observe(viewLifecycleOwner, Observer {
//            Log.d(javaClass.simpleName, "Observe Chat $it")
//            locationAdapter.submitList(it)
//        })
//
//        composeMessageET.setOnEditorActionListener { /*v*/_, actionId, event ->
//            // If user has pressed enter, or if they hit the soft keyboard "send" button
//            // (which sends DONE because of the XML)
//            if ((event != null
//                        &&(event.action == KeyEvent.ACTION_DOWN)
//                        &&(event.keyCode == KeyEvent.KEYCODE_ENTER))
//                || (actionId == EditorInfo.IME_ACTION_DONE)) {
//                (requireActivity() as MainActivity).hideKeyboard()
//                composeSendIB.callOnClick()
//            }
//            true
//        }
//        composeCameraIB.setOnClickListener {
//            viewModel.takePhoto {
//                Log.d(javaClass.simpleName, "uuid $it")
//                fragmentUUID = it
//                composePreviewIV.doOnLayout {view ->
//                    view.updateLayoutParams {
//                        width = viewModel.fourFifthWidthPx
//                    }
//                }
//                composePreviewIV.visibility = View.VISIBLE
//                viewModel.glideFetch(it, composePreviewIV)
//            }
//        }
//        composePreviewIV.visibility = View.GONE
//    }
//
//}