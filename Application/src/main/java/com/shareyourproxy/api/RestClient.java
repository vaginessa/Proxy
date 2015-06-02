package com.shareyourproxy.api;

import android.app.Activity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.shareyourproxy.R;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.gson.AutoGson;
import com.shareyourproxy.api.gson.AutoParcelAdapterFactory;
import com.shareyourproxy.api.gson.UserTypeAdapter;
import com.shareyourproxy.api.service.ChannelService;
import com.shareyourproxy.api.service.GroupService;
import com.shareyourproxy.api.service.UserService;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

/**
 * Rest client for users.
 */
public class RestClient {

    /**
     * Constructor.
     */
    private RestClient() {
    }

    /**
     * Get the {@link UserService}.
     *
     * @return userService
     */
    public static UserService getUserService(final Activity context) {
        return buildRestClient(context,
            buildGsonConverter(User.class.getAnnotation(AutoGson.class).autoValueClass(),
                UserTypeAdapter.newInstace()))
            .create(UserService.class);
    }

    public static GroupService getGroupService(Activity context) {
        return buildRestClient(context,
            buildGsonConverter()).create(GroupService.class);
    }

    public static ChannelService getChannelService(Activity context) {
        return buildRestClient(context,
            buildGsonConverter()).create(ChannelService.class);
    }

    public static RestAdapter buildRestClient(Activity context, Gson gson) {

        RestAdapter restAdapter = new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(context.getResources().getString(R.string.firebase_url))
            .setConverter(new GsonConverter(gson))
            .build();
        return restAdapter;
    }

    public static Gson buildGsonConverter(Class clazz, TypeAdapter typeAdapter) {
        return new GsonBuilder()
            .registerTypeAdapterFactory(new AutoParcelAdapterFactory())
            .registerTypeAdapter(clazz, typeAdapter)
            .create();
    }

    public static Gson buildGsonConverter() {
        return new GsonBuilder()
            .registerTypeAdapterFactory(new AutoParcelAdapterFactory())
            .create();
    }
}