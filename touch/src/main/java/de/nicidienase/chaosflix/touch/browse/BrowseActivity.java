package de.nicidienase.chaosflix.touch.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import de.nicidienase.chaosflix.R;
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentEvent;
import de.nicidienase.chaosflix.touch.OnEventSelectedListener;
import de.nicidienase.chaosflix.touch.activities.AboutActivity;
import de.nicidienase.chaosflix.touch.eventdetails.EventDetailsActivity;

public class BrowseActivity extends AppCompatActivity implements
		ConferencesTabBrowseFragment.OnConferenceListFragmentInteractionListener,
		EventsListFragment.OnEventsListFragmentInteractionListener,
		OnEventSelectedListener {

	private static final String TAG = BrowseActivity.class.getSimpleName();
	private Toolbar toolbar;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);

		toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setLogo(R.drawable.icon_notext);
		getSupportActionBar().setDisplayUseLogoEnabled(true);
		getSupportActionBar().setTitle(R.string.app_name);


		if (savedInstanceState == null) {
			ConferencesTabBrowseFragment browseFragment
					= ConferencesTabBrowseFragment.newInstance(getNumColumns());
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.fragment_container, browseFragment)
					.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.browse_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_show_bookmarks:
				showBookmarksFragment();
				return true;
			case R.id.action_show_inprogress:
				showInProgressFragment();
				return true;
			case R.id.action_about:
				showAboutPage();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private int getNumColumns() {
		return getResources().getInteger(R.integer.num_columns);
	}

	@Override
	public void onConferenceSelected(long conferenceId) {
		EventsListFragment eventsListFragment = EventsListFragment.newInstance(conferenceId, getNumColumns());
		showEventsFragment(eventsListFragment);
	}

	private void showBookmarksFragment() {
		EventsListFragment bookmarksFragment = EventsListFragment.newInstance(EventsListFragment.BOOKMARKS_LIST_ID, getNumColumns());
		showEventsFragment(bookmarksFragment);
	}

	private void showInProgressFragment() {
		EventsListFragment progressEventsFragment = EventsListFragment.newInstance(EventsListFragment.IN_PROGRESS_LIST_ID, getNumColumns());
		showEventsFragment(progressEventsFragment);
	}

	private void showEventsFragment(EventsListFragment eventsListFragment) {
		FragmentManager fm = getSupportFragmentManager();
		Fragment oldFragment = fm.findFragmentById(R.id.fragment_container);

		TransitionInflater transitionInflater = TransitionInflater.from(this);
		oldFragment.setExitTransition(
				transitionInflater.inflateTransition(android.R.transition.fade));
		eventsListFragment.setEnterTransition(
				transitionInflater.inflateTransition(android.R.transition.slide_right));

		Slide slideTransition = new Slide(Gravity.RIGHT);
		eventsListFragment.setEnterTransition(slideTransition);

		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(R.id.fragment_container, eventsListFragment);
		ft.setReorderingAllowed(true);
		ft.addToBackStack(null);
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.commit();
	}

	@Override
	public void onEventSelected(PersistentEvent event, View v) {
		EventDetailsActivity.Companion.launch(this, event.getEventId());
//		EventDetailsFragment detailsFragment = EventDetailsFragment.Companion.newInstance(event.getEventId());
//		FragmentManager fm = getSupportFragmentManager();
//
//		detailsFragment.setAllowEnterTransitionOverlap(true);
//		detailsFragment.setAllowReturnTransitionOverlap(true);
//
//		FragmentTransaction ft = fm.beginTransaction();
//		ft.replace(R.id.fragment_container, detailsFragment);
//		ft.addToBackStack(null);
//		ft.setReorderingAllowed(true);
//
//		View thumb = v.findViewById(R.id.imageView);
//		ft.addSharedElement(thumb, ViewCompat.getTransitionName(thumb));
//
//		ft.commit();
	}

	@Override
	public void setToolbarTitle(String title) {
		toolbar.setTitle(title);
	}

	private void showAboutPage() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
}
