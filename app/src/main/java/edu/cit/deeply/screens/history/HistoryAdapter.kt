package edu.cit.deeply.screens.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.deeply.R

class HistoryAdapter(
    private val onSessionClicked: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_DATE_HEADER = 0
        private const val TYPE_SESSION_CARD = 1
    }

    private val items = mutableListOf<HistoryItem>()

    fun submitList(newItems: List<HistoryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (items[position]) {
        is HistoryItem.DateHeader -> TYPE_DATE_HEADER
        is HistoryItem.SessionCard -> TYPE_SESSION_CARD
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_DATE_HEADER -> DateHeaderViewHolder(
                inflater.inflate(R.layout.item_date_header, parent, false)
            )
            TYPE_SESSION_CARD -> SessionCardViewHolder(
                inflater.inflate(R.layout.item_session_card, parent, false)
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is HistoryItem.DateHeader -> (holder as DateHeaderViewHolder).bind(item)
            is HistoryItem.SessionCard -> (holder as SessionCardViewHolder).bind(item, position)
        }
    }

    inner class DateHeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvDateHeader: TextView = view.findViewById(R.id.tvDateHeader)

        fun bind(item: HistoryItem.DateHeader) {
            tvDateHeader.text = item.label
        }
    }

    inner class SessionCardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvActivityType: TextView = view.findViewById(R.id.tvActivityType)
        private val tvEnvironmentDuration: TextView = view.findViewById(R.id.tvEnvironmentDuration)
        private val tvStartTime: TextView = view.findViewById(R.id.tvStartTime)
        private val layoutMetrics: LinearLayout = view.findViewById(R.id.layoutMetrics)
        private val tvMetricFocus: TextView = view.findViewById(R.id.tvMetricFocus)
        private val tvMetricDistraction: TextView = view.findViewById(R.id.tvMetricDistraction)
        private val tvMetricSatisfaction: TextView = view.findViewById(R.id.tvMetricSatisfaction)
        private val layoutExpanded: LinearLayout = view.findViewById(R.id.layoutExpanded)
        private val tvEnergyLevel: TextView = view.findViewById(R.id.tvEnergyLevel)
        private val tvFullStartTime: TextView = view.findViewById(R.id.tvFullStartTime)
        private val tvFullEndTime: TextView = view.findViewById(R.id.tvFullEndTime)
        private val layoutExpandedScores: LinearLayout = view.findViewById(R.id.layoutExpandedScores)
        private val tvDetailFocus: TextView = view.findViewById(R.id.tvDetailFocus)
        private val tvDetailDistraction: TextView = view.findViewById(R.id.tvDetailDistraction)
        private val tvDetailSatisfaction: TextView = view.findViewById(R.id.tvDetailSatisfaction)

        fun bind(item: HistoryItem.SessionCard, position: Int) {
            val ctx = itemView.context

            tvActivityType.text = item.activityType
            tvEnvironmentDuration.text = ctx.getString(
                R.string.post_session_summary_format,
                item.environment,
                item.duration,
                ""
            ).trimEnd(' ', '·').trim()
            tvStartTime.text = item.startTime

            val hasScores = item.focusQuality != null
            if (hasScores) {
                layoutMetrics.visibility = View.VISIBLE
                tvMetricFocus.text = ctx.getString(R.string.history_focus) + " " + item.focusQuality
                tvMetricDistraction.text = ctx.getString(R.string.history_distraction) + " " + item.distractionLevel
                tvMetricSatisfaction.text = ctx.getString(R.string.history_satisfaction) + " " + item.satisfaction
            } else {
                layoutMetrics.visibility = View.GONE
            }

            layoutExpanded.visibility = if (item.isExpanded) View.VISIBLE else View.GONE

            if (item.isExpanded) {
                tvEnergyLevel.text = ctx.getString(R.string.history_energy) + ": " + item.energyLevel
                tvFullStartTime.text = ctx.getString(R.string.history_started) + ": " + item.fullStartTime
                tvFullEndTime.text = ctx.getString(R.string.history_ended) + ": " + item.fullEndTime

                if (hasScores) {
                    layoutExpandedScores.visibility = View.VISIBLE
                    tvDetailFocus.text = ctx.getString(R.string.post_session_focus_quality) + ": " + item.focusQuality + "/100"
                    tvDetailDistraction.text = ctx.getString(R.string.post_session_distraction) + ": " + item.distractionLevel + "/100"
                    tvDetailSatisfaction.text = ctx.getString(R.string.post_session_satisfaction) + ": " + item.satisfaction + "/100"
                } else {
                    layoutExpandedScores.visibility = View.GONE
                }
            }

            itemView.setOnClickListener { onSessionClicked(position) }
        }
    }
}
