package com.shareyourproxy.api.domain.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.shareyourproxy.api.gson.AutoGson;

import java.util.ArrayList;

import auto.parcel.AutoParcel;

/**
 * Contacts are {@link User}s that you'd like to communicate with.
 */
@AutoParcel
@AutoGson(autoValueClass = AutoParcel_Contact.class)
public abstract class Contact implements Parcelable {

    /**
     * Create a new Contact.
     *
     * @param id       unique identifier
     * @param label    name of the contact
     * @param channels list of contacts channels
     * @return Immutable contact
     */
    @SuppressWarnings("unused")
    public static Contact create(Id id, String label, ArrayList<Channel> channels) {
        return builder().id(id).label(label).channels(channels).build();
    }

    /**
     * Contact builder.
     *
     * @return this Contact.
     */
    public static Builder builder() {
        return new AutoParcel_Contact.Builder();
    }

    /**
     * Get the ID of the Contact.
     *
     * @return name
     */
    public abstract Id id();

    /**
     * Get the name of the Contact.
     *
     * @return name
     */
    public abstract String label();

    /**
     * Get the list of channels a contact has.
     *
     * @return list of contact's channels
     */
    @Nullable
    public abstract ArrayList<Channel> channels();

    /**
     * Group Builder.
     */
    @AutoParcel.Builder
    public interface Builder {

        /**
         * Set the contacts Id.
         *
         * @param id contact unique id
         * @return contact id
         */
        Builder id(Id id);

        /**
         * Set the contacts name.
         *
         * @param label contact name
         * @return label
         */
        Builder label(String label);

        /**
         * Set contact channels.
         *
         * @param channels contact channels
         * @return channels
         */
        @Nullable
        Builder channels(ArrayList<Channel> channels);

        /**
         * BUILD.
         *
         * @return Contact
         */
        Contact build();
    }

}