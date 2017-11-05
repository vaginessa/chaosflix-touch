package de.nicidienase.chaosflix.touch.sync

import de.nicidienase.chaosflix.common.entities.ChaosflixDatabase
import de.nicidienase.chaosflix.common.entities.recording.Conference
import de.nicidienase.chaosflix.common.entities.recording.ConferencesWrapper
import de.nicidienase.chaosflix.common.entities.recording.Event
import de.nicidienase.chaosflix.common.entities.recording.persistence.ConferenceGroup
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentConference
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentEvent
import de.nicidienase.chaosflix.common.entities.recording.persistence.PersistentRecording
import de.nicidienase.chaosflix.common.network.RecordingService
import de.nicidienase.chaosflix.touch.Util
import io.reactivex.schedulers.Schedulers

class Downloader(val recordingApi: RecordingService,
                 val database: ChaosflixDatabase) {

    private fun updateEverything() {
        updateConferencesAndGroups { conferenceIds ->
            for(id in conferenceIds){
                updateEventsForConference(id){ eventIds ->
                    for(id in eventIds){
                        updateRecordingsForEvent(id)
                    }
                }
            }
        }
    }

    fun updateConferencesAndGroups(listener: ((List<Long>) -> Unit)? = null) {
        recordingApi.getConferencesWrapper()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { con: ConferencesWrapper? -> saveConferences(con, listener) }
    }

    fun updateEventsForConference(conferenceId: Long, listener: ((List<Long>) -> Unit)? = null) {
        if(conferenceId < 0)
            return
        recordingApi.getConference(conferenceId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { conference: Conference? ->
                    saveEvents(conference, listener)
                }

    }

    fun updateRecordingsForEvent(eventId: Long, listener: ((List<Long>) -> Unit)? = null) {
        if(eventId < 0)
            return
        recordingApi.getEvent(eventId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe { event: Event? ->
                    saveRecordings(event, listener)
                }
    }

    fun saveConferences(con: ConferencesWrapper?, listener: ((List<Long>) -> Unit)?) {
        if (con != null) {
            con.conferenceMap.map { entry ->
                val conferenceGroup: ConferenceGroup?
                        = database.conferenceGroupDao().getConferenceGroupByName(entry.key)
                val groupId: Long
                if (conferenceGroup != null) {
                    groupId = conferenceGroup.conferenceGroupId
                } else {
                    val group = ConferenceGroup(entry.key)
                    val index = Util.orderedConferencesList.indexOf(group.name)
                    if (index != -1)
                        group.index = index
                    else if (group.name == "other conferences")
                        group.index = 1_000_001
                    groupId = database.conferenceGroupDao().addConferenceGroup(group)[0]
                }
                val conferenceList = entry.value
                        .map { PersistentConference(it) }
                        .map { it.conferenceGroupId = groupId; it }.toTypedArray()
                val insertConferences = database.conferenceDao().insertConferences(*conferenceList)
                listener?.invoke(con.conferences.map { it.conferenceID })
            }
        }
    }

    private fun saveEvents(conference: Conference?, listener: ((List<Long>) -> Unit)?) {
        conference?.events.let {
            val events = conference?.events
                    ?.map { PersistentEvent(it) }?.toTypedArray()
            if (events != null) {
                val insertEvents = database.eventDao().insertEvent(*events)
                listener?.invoke(events.map { it.eventId })
            }
        }
    }

    private fun saveRecordings(event: Event?, listener: ((List<Long>) -> Unit)?) {
        val recordings = event?.recordings
                ?.map { PersistentRecording(it) }
                ?.toTypedArray()
        if (recordings != null) {
            val insertRecordings = database.recordingDao().insertRecording(*recordings)
            listener?.invoke(recordings.map { it.recordingId })
        }
    }

}