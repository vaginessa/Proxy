package com.shareyourproxy.api.domain.factory

import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.shareyourproxy.api.domain.model.Channel
import com.shareyourproxy.api.domain.model.Group
import com.shareyourproxy.api.domain.model.User
import com.shareyourproxy.util.StringUtils.buildFullName

/**
 * The web service actually doesn't return a fullname param, we make/strip it here from the user object.
 */
internal object UserTypeAdapter : TypeAdapter<User>() {
    val gson = Gson()
    val delegate = gson.getAdapter(User::class.java)
    val elementAdapter = gson.getAdapter(JsonElement::class.java)
    override fun write(out: JsonWriter, value: User) {
        val tree = delegate.toJsonTree(value)
        beforeWrite(tree)
        elementAdapter.write(out, tree)
    }

    override fun read(input: JsonReader): User {
        input.isLenient = true
        val tree = elementAdapter.read(input)
        afterRead(tree)
        return if (!tree.isJsonNull) delegate.fromJsonTree(tree) else User()
    }

    fun beforeWrite(toSerialize: JsonElement) {
        if (toSerialize.isJsonObject) {
            removeFullName(toSerialize)
        } else if (toSerialize.isJsonArray) {
            val custom = toSerialize.asJsonArray
            custom.forEach { removeFullName(it) }
        }
    }

    fun afterRead(deserialized: JsonElement) {
        if (deserialized.isJsonObject) {
            addFullName(deserialized)
            removeNulls(deserialized)
        } else if (deserialized.isJsonArray) {
            val users = deserialized.asJsonArray
            for (user in users) {
                afterRead(user)
            }
        }else{
            User()
        }
    }

    private fun removeFullName(toSerialize: JsonElement) {
        val custom = toSerialize.asJsonObject
        custom.remove("fullName")
    }

    private fun addFullName(deserialized: JsonElement) {
        val obj = deserialized.asJsonObject
        val firstName = obj.get("first")
        val lastName = obj.get("last")
        val first = if (firstName == null) "" else firstName.asString
        val last = if (lastName == null) "" else lastName.asString
        obj.add("fullName", JsonPrimitive(buildFullName(first, last)))
    }

    private fun removeNulls(deserialized: JsonElement) {
        val obj = deserialized.asJsonObject
        if (!obj.has("channels")) {
            obj.add("channels", JsonParser().parse(Channel().toString()).asJsonObject)
        }
        if (!obj.has("contacts")) {
            obj.add("contacts", JsonParser().parse(emptySet<String>().toString()).asJsonObject)
        }
        if (!obj.has("groups")) {
            obj.add("groups", JsonParser().parse(Group().toString()).asJsonObject)
        }
    }
}