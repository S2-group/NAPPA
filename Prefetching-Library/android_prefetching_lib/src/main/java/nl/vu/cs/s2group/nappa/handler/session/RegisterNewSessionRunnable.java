package nl.vu.cs.s2group.nappa.handler.session;

import java.util.Date;
import java9.util.function.Consumer;

import nl.vu.cs.s2group.nappa.room.NappaDB;
import nl.vu.cs.s2group.nappa.room.data.Session;

/**
 * Defines a Runnable to register a new session into the database. If successful, the
 * registered session is returned via the callback. The session time is obtained when
 * running this runnable using {@code new Date().getTime()}
 */
public class RegisterNewSessionRunnable implements Runnable {
    Consumer<Session> callback;

    public RegisterNewSessionRunnable(Consumer<Session> callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        Session session = new Session(new Date().getTime());
        session.id = NappaDB.getInstance().
                sessionDao().
                insertSession(session);
        callback.accept(session);
    }
}
