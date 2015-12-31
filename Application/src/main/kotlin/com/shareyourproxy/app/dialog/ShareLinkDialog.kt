package com.shareyourproxy.app.dialog

import android.R.string.cancel
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatDialog
import android.view.WindowManager.LayoutParams.MATCH_PARENT
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import android.widget.TextView
import com.shareyourproxy.R
import com.shareyourproxy.R.color.common_blue
import com.shareyourproxy.R.color.common_text
import com.shareyourproxy.R.id.dialog_sharelink_message_text
import com.shareyourproxy.R.id.dialog_sharelink_recyclerview
import com.shareyourproxy.R.string.dialog_sharelink_message
import com.shareyourproxy.R.string.share
import com.shareyourproxy.R.style.Widget_Proxy_App_Dialog
import com.shareyourproxy.api.domain.model.Group
import com.shareyourproxy.api.rx.RxBusRelay.post
import com.shareyourproxy.api.rx.command.GenerateShareLinkCommand
import com.shareyourproxy.app.adapter.BaseRecyclerView
import com.shareyourproxy.app.adapter.ShareLinkAdapter
import com.shareyourproxy.util.ButterKnife.bindColor
import com.shareyourproxy.util.ButterKnife.bindView
import org.solovyev.android.views.llm.LinearLayoutManager
import java.util.*

/**
 * Share links to group channels in your web profile.
 */
class ShareLinkDialog(private val groups: HashMap<String, Group>) : BaseDialogFragment() {
    private val TAG = ShareLinkDialog::class.java.simpleName
    private val ARG_GROUPS = "com.shareyourproxy.sharelinkdialog.group"
    private val message: TextView by bindView(dialog_sharelink_message_text)
    private val recyclerView: BaseRecyclerView by bindView(dialog_sharelink_recyclerview)
    private val colorText: Int by bindColor(common_text)
    private val colorBlue: Int by bindColor(common_blue)
    @Suppress("UNCHECKED_CAST")
    private val parcelGroups: HashMap<String, Group> = arguments.getSerializable(ARG_GROUPS) as HashMap<String, Group>
    private val adapter: ShareLinkAdapter = ShareLinkAdapter(recyclerView, parcelGroups)
    private val positiveClicked: DialogInterface.OnClickListener = OnClickListener { dialogInterface, i -> post(GenerateShareLinkCommand(loggedInUser, adapter.data)) }
    private val negativeClicked: DialogInterface.OnClickListener = OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }

    init {
        arguments.putSerializable(ARG_GROUPS, groups)
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): AppCompatDialog {
        super.onCreateDialog(savedInstanceState)
        val view = activity.layoutInflater.inflate(R.layout.dialog_sharelink, null, false)

        // build dialog
        val dialog = AlertDialog.Builder(activity, Widget_Proxy_App_Dialog)
                .setTitle(R.string.dialog_sharelink_title)
                .setView(view)
                .setPositiveButton(share, positiveClicked)
                .setNegativeButton(cancel, negativeClicked)
                .create()


        message.text = getString(dialog_sharelink_message)
        // Show the SW Keyboard on dialog start. Always.
        dialog.window.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        dialog.window.attributes.width = MATCH_PARENT
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as AlertDialog
        setButtonTint(dialog.getButton(Dialog.BUTTON_POSITIVE), colorBlue)
        setButtonTint(dialog.getButton(Dialog.BUTTON_NEGATIVE), colorText)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.hasFixedSize()
        recyclerView.adapter = adapter
    }

    /**
     * Use the private string TAG from this class as an identifier.
     * @param fragmentManager manager of fragments
     */
    fun show(fragmentManager: FragmentManager): ShareLinkDialog {
        show(fragmentManager, TAG)
        return this
    }
}
