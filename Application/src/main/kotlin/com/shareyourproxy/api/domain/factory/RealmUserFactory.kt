package com.shareyourproxy.api.domain.factory

import com.shareyourproxy.api.domain.factory.RealmChannelFactory.getRealmChannels
import com.shareyourproxy.api.domain.factory.RealmContactFactory.getRealmContacts
import com.shareyourproxy.api.domain.factory.RealmGroupFactory.getRealmGroups
import com.shareyourproxy.api.domain.model.User
import com.shareyourproxy.api.domain.realm.RealmUser
import io.realm.RealmList

/**
 * Factory for creating [RealmUser]s.
 */
object RealmUserFactory {
    /**
     * Convert User to RealmUser
     * @param user to convert
     * @return RealmUser
     */
    fun createRealmUser(user: User): RealmUser {
        return RealmUser(user.id,user.first,user.last,user.fullName,user.email,user.profileURL,user.coverURL,getRealmChannels(user.channels),getRealmContacts(user.contacts),getRealmGroups(user.groups),user.androidVersion)
    }

    fun createRealmUsers(users: Map<String, User>): RealmList<RealmUser> {
        val realmUsers = RealmList<RealmUser>()
        for (user in users.entries) {
            realmUsers.add(createRealmUser(user.value))
        }
        return realmUsers
    }
}