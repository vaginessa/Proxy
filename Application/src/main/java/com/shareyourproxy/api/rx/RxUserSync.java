package com.shareyourproxy.api.rx;

import android.content.Context;

import com.shareyourproxy.api.RestClient;
import com.shareyourproxy.api.domain.model.User;
import com.shareyourproxy.api.rx.command.eventcallback.EventCallback;
import com.shareyourproxy.api.rx.command.eventcallback.LoggedInUserUpdatedEventCallback;
import com.shareyourproxy.api.rx.command.eventcallback.UsersDownloadedEventCallback;
import com.shareyourproxy.api.rx.event.SyncAllUsersSuccessEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.functions.Func1;

import static com.shareyourproxy.api.RestClient.getUserService;
import static com.shareyourproxy.api.rx.RxHelper.updateRealmUser;

/**
 * Cold Observables to sync users to firebase and realm.
 */
public class RxUserSync {

    /**
     * Private constructor.
     */
    private RxUserSync() {
    }

    /**
     * Download All Users from firebase and sync them to realm. Used for swipe refresh in the {@link
     * com.shareyourproxy.app.fragment.MainContactsFragment} and {@link
     * com.shareyourproxy.app.fragment.MainGroupFragment}'s {@link android.support.v4.widget
     * .SwipeRefreshLayout}s
     *
     * @param context        for realm instance
     * @param loggedInUserId to identify the logged in user
     * @return {@link UsersDownloadedEventCallback} to rxBus
     */
    public static List<EventCallback> syncAllUsers(
        Context context, RxBusDriver rxBus, String loggedInUserId) {
        return getFirebaseUsers(context, rxBus)
            .map(saveRealmUsers(context))
            .map(usersDownloaded(loggedInUserId))
            .compose(RxHelper.<List<EventCallback>>applySchedulers()).toBlocking().single();
    }

    public static List<EventCallback> saveUser(
        Context context, RxBusDriver rxBus, User newUser) {
        return RestClient.getUserService(context, rxBus).updateUser(newUser.id(), newUser)
            .map(saveRealmUser(context))
            .toList()
            .compose(RxHelper.<List<EventCallback>>applySchedulers())
            .toBlocking().single();
    }

    private static rx.Observable<HashMap<String, User>> getFirebaseUsers(
        Context context, RxBusDriver rxBus) {
        return getUserService(context, rxBus).listUsers();
    }

    private static Func1<HashMap<String, User>, HashMap<String, User>> saveRealmUsers(
        final Context context) {
        return new Func1<HashMap<String, User>, HashMap<String, User>>() {
            @Override
            public HashMap<String, User> call(HashMap<String, User> users) {
                updateRealmUser(context, users);
                return users;
            }
        };
    }

    private static Func1<User, EventCallback> saveRealmUser(final Context context) {
        return new Func1<User, EventCallback>() {
            @Override
            public LoggedInUserUpdatedEventCallback call(User user) {
                updateRealmUser(context, user);
                return new LoggedInUserUpdatedEventCallback(user);
            }
        };
    }

    private static Func1<HashMap<String, User>, List<EventCallback>> usersDownloaded(
        final String loggedInUserId) {
        return new Func1<HashMap<String, User>, List<EventCallback>>() {
            @Override
            public List<EventCallback> call(HashMap<String, User> users) {
                User loggedInUser = users.get(loggedInUserId);
                UsersDownloadedEventCallback usersCallback =
                    new UsersDownloadedEventCallback(loggedInUser, users);
                LoggedInUserUpdatedEventCallback loggedInUserCallback =
                    new LoggedInUserUpdatedEventCallback(loggedInUser);
                ArrayList<EventCallback> list = new ArrayList<>();
                list.add(usersCallback);
                list.add(loggedInUserCallback);
                list.add(new SyncAllUsersSuccessEvent());
                return list;
            }
        };
    }
}
