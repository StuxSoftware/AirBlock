package net.stuxcrystal.airblock.commands.backend;

import lombok.NonNull;
import net.stuxcrystal.airblock.commands.Executor;
import net.stuxcrystal.airblock.commands.contrib.threads.FallbackThreadSystem;
import net.stuxcrystal.airblock.commands.contrib.threads.ThreadManager;
import net.stuxcrystal.airblock.commands.core.settings.Environment;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.logging.Logger;

/**
 * Class for the implementation of a backend.
 */
public class Backend extends HandleWrapper<BackendHandle<?,?>> {
    /**
     * Contains the handle for the backend.
     * @param handle The handle for the backend.
     */
    public Backend(@NonNull BackendHandle<?, ?> handle, @NonNull Environment environment) {
        super(handle, environment);
    }

    /**
     * Wraps an executor.
     * @param handle The handle to wrap.
     * @return The wrapped executor.
     */
    Executor wrap(ExecutorHandle<?> handle) {
        return new Executor(handle, this.getEnvironment());
    }

    /**
     * Returns the console object.
     * @return The console object.
     */
    @NonNull
    public Executor getConsole() {
        return this.wrap(this.getHandle().getConsole());
    }

    /**
     * Returns all executor entities that are logged in.
     * @return All executor entities that are logged in.
     */
    @NonNull
    public Executor[] getExecutors() {
        ExecutorHandle<?>[] handles = this.getHandle().getExecutors();
        Executor[] result = new Executor[handles.length];
        for (int i = 0; i<result.length;i++)
            result[i] = this.wrap(handles[i]);
        return result;
    }

    /**
     * Returns the executed that exactly matches the given name.
     * @param name The name of the executor.
     * @return The executor.
     */
    @Nullable
    public Executor getExecutorExact(@Nullable String name) {
        if (StringUtils.isEmpty(name))
            return this.getConsole();

        ExecutorHandle<?>[] handles = this.getHandle().getExecutors();
        for (ExecutorHandle handle : handles)
            if (handle.getName().equals(name))
                return this.wrap(handle);
        return null;
    }
    /**
     * Returns the logger for the backend.
     * @return The logger for the backend.
     */
    @NonNull
    public Logger getLogger() {
        return this.getHandle().getLogger();
    }

    /**
     * Runs the runnable later in the main thread.
     * @param runnable Runs the runnable later.
     */
    public void runLater(@NonNull Runnable runnable) {
        this.getHandle().runLater(runnable);
    }

    private ThreadManager getThreadManager() {
        if (!this.hasComponent(ThreadManager.class)) {
            this.getEnvironment().getComponentManager().register(this.getClass(), new FallbackThreadSystem());
        }
        return this.getComponent(ThreadManager.class);
    }

    /**
     * Runs the runnable in a new thread.
     * @param runnable Runs the runnable in a separate thread.
     */
    public void runAsynchronously(@NonNull Runnable runnable) {
        this.getThreadManager().runAsynchronously(runnable);
    }

    /**
     * Are we currently in the main thread.
     * @return {@code true} if we are in main.
     */
    public boolean isInMainThread() {
        return this.getThreadManager().isInMainThread();
    }

}
