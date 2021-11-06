package com.eskimobile.jetvpn.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.eskimobile.jetvpn.R
import kotlinx.android.synthetic.main.dialog_confirm_fragment.view.*

class ConfirmDialogFragment : DialogFragment() {
    companion object {
        fun cancelPremium(context: Context, fragmentManager: FragmentManager, onPositiveClickListener: (() -> Unit)? = null) {
            val dialog = ConfirmDialogFragment()
                .apply {
                    title = context.getString(R.string.cancel_premium)
                    message = context.getString(R.string.message_cancel_premium)
                    textNegative = context.getString(R.string.cancel)
                    textPositive = context.getString(R.string.confirm)
                    this.onPositiveClickListener = onPositiveClickListener
                }
            dialog.show(fragmentManager, "cancelPremium")
        }

        fun resetPassword(context: Context, fragmentManager: FragmentManager, onPositiveClickListener: (() -> Unit)? = null) {
            val dialog = ConfirmDialogFragment()
                .apply {
                    title = context.getString(R.string.reset_password_email)
                    message = context.getString(R.string.message_reset_password_email)
                    textPositive = context.getString(R.string.ok)
                    this.onPositiveClickListener = onPositiveClickListener
                }
            dialog.show(fragmentManager, "resetPassword")
        }

        fun changeVpn(context: Context, fragmentManager: FragmentManager, onPositiveClickListener: (() -> Unit)? = null) {
            val dialog = ConfirmDialogFragment()
                .apply {
                    title = context.getString(R.string.change_vpn_dialog_title)
                    message = context.getString(R.string.change_vpn_dialog_message)
                    textPositive = context.getString(R.string.ok)
                    this.onPositiveClickListener = onPositiveClickListener
                }
            dialog.show(fragmentManager, "changeVpn")
        }
    }


    lateinit var title: String
    lateinit var message: String

    var textNegative: String? = null
    private var onNegativeClickListener: (() -> Unit)? = null

    lateinit var textPositive: String
    private var onPositiveClickListener: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_confirm_fragment, null)

        view.imgClose.setOnClickListener { dismiss() }
        view.tvNegative.setOnClickListener { handleNegative() }
        view.tvPositive.setOnClickListener { handlePositive() }

        view.tvTitle.text = title
        view.tvMessage.text = message

        textNegative?.let {
            view.tvNegative.visibility = View.VISIBLE
            view.tvNegative.text = it
        }

        view.tvPositive.text = textPositive

        val builder = AlertDialog.Builder(view.context, R.style.AlertDialogTheme)
            .setView(view)
        return builder.create()
    }


    private fun handlePositive() {
        dismiss()
        onPositiveClickListener?.invoke()
    }

    private fun handleNegative() {
        dismiss()
        onNegativeClickListener?.invoke()
    }
}