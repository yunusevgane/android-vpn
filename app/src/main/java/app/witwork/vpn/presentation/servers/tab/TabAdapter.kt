package com.eskimobile.jetvpn.presentation.servers.tab

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eskimobile.jetvpn.R
import com.eskimobile.jetvpn.common.utils.Util
import com.eskimobile.jetvpn.common.widget.SelectionItemView
import com.eskimobile.jetvpn.domain.model.Server
import com.steve.utilities.core.extensions.inflate

class TabAdapter(private val serverDraft: Server?, private val onItemClicked: ((Server?) -> Unit)? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEW_TYPE_PADDING = 0
        const val VIEW_TYPE_ITEM = 1
    }

    var servers = listOf<Server>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_ITEM) {
            val view = parent.inflate(R.layout.item_vpn_server)
            return ViewHolder(view)
        }
        val view = parent.inflate(R.layout.item_padding)
        return PaddingViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if (servers.count() > 0)
            servers.count() + 2
        else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || position == itemCount - 1) VIEW_TYPE_PADDING else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            val originPos = position - 1
            holder.bindData(servers[originPos])
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var server: Server? = null

        init {
            itemView.setOnClickListener {
                onItemClicked?.invoke(server)
            }
        }

        fun bindData(server: Server) {
            this.server = server
            (itemView as? SelectionItemView)?.apply {
                val resId = Util.getResId(server.countryCode) ?: -1
                setFlag(resId)
                setTitle(server.country)
                setDescription(server.state)

                val drawResId = if (server.premium == true) SelectionItemView.ACTION_PREMIUM else SelectionItemView.ACTION_CHECK_BOX
                setEndAction(drawResId)
                isSelected = serverDraft == server
            }
        }
    }

    inner class PaddingViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}