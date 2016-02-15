package com.shareyourproxy.api.rx.command.eventcallback

import android.os.Parcel
import android.os.Parcelable

import com.shareyourproxy.api.domain.model.Group
import com.shareyourproxy.api.domain.model.User

/**
 * Group contacts have been updated.
 */
internal final class GroupContactsUpdatedEventCallback(user: User, val contactId: String, val contactGroups: List<Group>) : UserEventCallback(user) {

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(contactId)
        dest.writeValue(contactGroups)
    }

    companion object {
        private val CL = GroupContactsUpdatedEventCallback::class.java.classLoader
        @JvmField val CREATOR: Parcelable.Creator<GroupContactsUpdatedEventCallback> = object : Parcelable.Creator<GroupContactsUpdatedEventCallback> {
            @Suppress("UNCHECKED_CAST")
            override fun createFromParcel(parcel: Parcel): GroupContactsUpdatedEventCallback {
                return GroupContactsUpdatedEventCallback(parcel.readValue(CL) as User, parcel.readValue(CL) as String, parcel.readValue(CL) as List<Group>)
            }

            override fun newArray(size: Int): Array<GroupContactsUpdatedEventCallback?> {
                return arrayOfNulls(size)
            }
        }
    }
}
