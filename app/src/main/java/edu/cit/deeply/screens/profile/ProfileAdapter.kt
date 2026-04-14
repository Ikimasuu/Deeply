package edu.cit.deeply.screens.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import edu.cit.deeply.R

sealed class ProfileRow {
    data class SectionHeader(val title: String) : ProfileRow()
    data class Navigation(
        val id: String,
        val iconRes: Int,
        val title: String,
        val subtitle: String? = null,
        val showChevron: Boolean = true,
        val textColorRes: Int = R.color.text_primary
    ) : ProfileRow()
    data class Toggle(
        val id: String,
        val iconRes: Int,
        val title: String,
        val isChecked: Boolean = false
    ) : ProfileRow()
}

class ProfileAdapter(
    private val onRowClicked: (String) -> Unit,
    private val onToggleChanged: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_SECTION_HEADER = 0
        private const val TYPE_NAVIGATION = 1
        private const val TYPE_TOGGLE = 2
    }

    private val items = mutableListOf<ProfileRow>()

    fun submitList(rows: List<ProfileRow>) {
        items.clear()
        items.addAll(rows)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is ProfileRow.SectionHeader -> TYPE_SECTION_HEADER
        is ProfileRow.Navigation -> TYPE_NAVIGATION
        is ProfileRow.Toggle -> TYPE_TOGGLE
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SECTION_HEADER -> SectionHeaderViewHolder(
                inflater.inflate(R.layout.item_profile_section_header, parent, false)
            )
            TYPE_NAVIGATION -> NavigationViewHolder(
                inflater.inflate(R.layout.item_profile_navigation, parent, false)
            )
            TYPE_TOGGLE -> ToggleViewHolder(
                inflater.inflate(R.layout.item_profile_toggle, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ProfileRow.SectionHeader -> (holder as SectionHeaderViewHolder).bind(item)
            is ProfileRow.Navigation -> (holder as NavigationViewHolder).bind(item)
            is ProfileRow.Toggle -> (holder as ToggleViewHolder).bind(item)
        }
    }

    inner class SectionHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tvSectionTitle)

        fun bind(item: ProfileRow.SectionHeader) {
            tvTitle.text = item.title
        }
    }

    inner class NavigationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val tvSubtitle: TextView = view.findViewById(R.id.tvSubtitle)
        private val ivChevron: ImageView = view.findViewById(R.id.ivChevron)

        fun bind(item: ProfileRow.Navigation) {
            ivIcon.setImageResource(item.iconRes)
            tvTitle.text = item.title
            tvTitle.setTextColor(itemView.context.getColor(item.textColorRes))

            if (item.subtitle != null) {
                tvSubtitle.text = item.subtitle
                tvSubtitle.visibility = View.VISIBLE
            } else {
                tvSubtitle.visibility = View.GONE
            }

            ivChevron.visibility = if (item.showChevron) View.VISIBLE else View.GONE

            itemView.setOnClickListener { onRowClicked(item.id) }
        }
    }

    inner class ToggleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivIcon: ImageView = view.findViewById(R.id.ivIcon)
        private val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        private val switchToggle: MaterialSwitch = view.findViewById(R.id.switchToggle)

        fun bind(item: ProfileRow.Toggle) {
            ivIcon.setImageResource(item.iconRes)
            tvTitle.text = item.title
            switchToggle.isChecked = item.isChecked
            switchToggle.setOnCheckedChangeListener { _, isChecked ->
                onToggleChanged(item.id, isChecked)
            }
        }
    }
}
