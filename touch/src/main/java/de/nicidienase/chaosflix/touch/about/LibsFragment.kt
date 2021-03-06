package de.nicidienase.chaosflix.touch.about

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsSupportFragment
import de.nicidienase.chaosflix.R

class LibsFragment: DialogFragment(){
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val layout = inflater.inflate(R.layout.fragment_libs,container, false)
		childFragmentManager.beginTransaction()
				.replace(R.id.layout_container,getLibsFragment())
				.commit()
		return layout
	}

	private fun getLibsFragment(): LibsSupportFragment {
		val aboutLibs: LibsSupportFragment = LibsBuilder()
				//				.withAboutIconShown(true)
				//				.withAboutVersionShown(true)
				//				.withAboutDescription(resources.getString(R.string.description))
				.supportFragment()
		return aboutLibs
	}
}