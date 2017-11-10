package de.nicidienase.chaosflix.touch.adapters

import com.squareup.picasso.Picasso

import java.util.ArrayList

import de.nicidienase.chaosflix.R
import de.nicidienase.chaosflix.common.entities.recording.Conference
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentConference
import de.nicidienase.chaosflix.touch.fragments.ConferencesTabBrowseFragment

class ConferenceRecyclerViewAdapter(private val mListener: ConferencesTabBrowseFragment.OnConferenceListFragmentInteractionListener?) : ItemRecyclerViewAdapter<PersistentConference>() {

    override fun getFilteredProperties(item: PersistentConference): List<String> {
        return listOf(item.title)
    }

    override val layout = R.layout.conference_cardview_item

    override fun onBindViewHolder(holder: ItemRecyclerViewAdapter<PersistentConference>.ViewHolder, position: Int) {
        holder.mTitleText.setText(items[position].title)
        holder.mSubtitle.setText(items[position].acronym)
        Picasso.with(holder.mIcon.context)
                .load(items[position].logoUrl)
                .fit()
                .centerInside()
                .into(holder.mIcon)

        holder.mView.setOnClickListener { v ->
            mListener?.onConferenceSelected((items[position]).conferenceId)
        }
    }
}
