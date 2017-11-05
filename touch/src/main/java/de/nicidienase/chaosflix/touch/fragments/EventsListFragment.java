package de.nicidienase.chaosflix.touch.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import de.nicidienase.chaosflix.R;
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentEvent;
import de.nicidienase.chaosflix.touch.adapters.EventRecyclerViewAdapter;

public class EventsListFragment extends ChaosflixFragment {

	private static final String ARG_COLUMN_COUNT = "column-count";
	private static final String ARG_CONFERENCE = "conference";
	private static final String LAYOUTMANAGER_STATE = "layoutmanager-state";
	private static final String TAG = EventsListFragment.class.getSimpleName();

	private int columnCount = 1;
	private OnEventsListFragmentInteractionListener listener;

	private Toolbar toolbar;
	private Context context;
	private EventRecyclerViewAdapter eventAdapter;
	private long conferenceId;

	private LinearLayoutManager layoutManager;

	public static EventsListFragment newInstance(long conferenceId, int columnCount) {
		EventsListFragment fragment = new EventsListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_COLUMN_COUNT, columnCount);
		args.putLong(ARG_CONFERENCE, conferenceId);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		setHasOptionsMenu(true);
		this.context = context;
		if (context instanceof OnEventsListFragmentInteractionListener) {
			listener = (OnEventsListFragmentInteractionListener) context;
		} else {
			throw new RuntimeException(context.toString()
					+ " must implement OnListFragmentInteractionListener");
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			columnCount = getArguments().getInt(ARG_COLUMN_COUNT);
			conferenceId = getArguments().getLong(ARG_CONFERENCE);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recycler_view_toolbar_layout, container, false);
		// Set the adapter
		Context context = view.getContext();
		RecyclerView recyclerView = view.findViewById(R.id.list);
		if (columnCount <= 1) {
			layoutManager = new LinearLayoutManager(context);
		} else {
			layoutManager = new GridLayoutManager(context, columnCount);
		}
		recyclerView.setLayoutManager(layoutManager);

		eventAdapter = new EventRecyclerViewAdapter(listener);
		recyclerView.setAdapter(eventAdapter);

		toolbar = view.findViewById(R.id.toolbar);
		((AppCompatActivity) this.context).setSupportActionBar(toolbar);

		getViewModel().getConference(conferenceId)
				.observe(this, conference -> toolbar.setTitle(conference.getTitle()));

		getViewModel().getEventsforConference(conferenceId)
				.observe(this, persistentEvents -> {
					eventAdapter.setItems(persistentEvents);
					layoutManager.onRestoreInstanceState(getArguments().getParcelable(LAYOUTMANAGER_STATE));
				});
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		getArguments().putParcelable(LAYOUTMANAGER_STATE, layoutManager.onSaveInstanceState());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.events_menu, menu);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		listener = null;
	}

	public interface OnEventsListFragmentInteractionListener {
		void onEventSelected(PersistentEvent event, View v);
	}

}