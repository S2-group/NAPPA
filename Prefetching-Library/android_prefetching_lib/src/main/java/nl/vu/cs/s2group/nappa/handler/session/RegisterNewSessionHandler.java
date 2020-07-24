package nl.vu.cs.s2group.nappa.handler.session;

import java9.util.function.Consumer;

import nl.vu.cs.s2group.nappa.room.data.Session;
import nl.vu.cs.s2group.nappa.util.NappaThreadPool;

/**
 * Defines a Handler to register a new session into the database. If successful, the
 * registered session is returned via the callback.
 * <p>
 * Although the class that invokes this class could directly submit the new runnable
 * to the thread pool, we opt to define it here to centralize calls to the worker
 * thread in the package {@link nl.vu.cs.s2group.nappa.handler}.
 */
public class RegisterNewSessionHandler {

    /**
     * Execute the handler
     *
     * @param callback A callback with the registered session
     */
    public static void run(Consumer<Session> callback) {
        NappaThreadPool.submit(new RegisterNewSessionRunnable(callback));
    }
}
