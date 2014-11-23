package net.stuxcrystal.airblock.commands.core.components;

import lombok.NonNull;
import net.stuxcrystal.airblock.commands.backend.Backend;
import net.stuxcrystal.airblock.commands.backend.BackendHandle;
import net.stuxcrystal.airblock.commands.backend.ExecutorHandle;
import net.stuxcrystal.airblock.commands.core.CommandImplementation;
import net.stuxcrystal.airblock.commands.core.settings.Environment;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ComponentBagTest {

    public static interface TestInterface {

        public void something();

    }

    @Components(TestInterface.class)
    public static class ComponentResolverTest {
        int called = 0;
        BackendHandle<?, ?> handle = null;
        public void something(BackendHandle<?, ?> handle) {
            this.called++;
            this.handle = handle;
        }
    }

    public static class FakeBackendHandle extends BackendHandle<Object, Object> implements TestInterface {

        public FakeBackendHandle(Object handle) {
            super(handle);
        }

        @Override
        public ExecutorHandle<Object> getConsole() {
            return null;
        }

        @Override
        public Logger getLogger() {
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public ExecutorHandle<Object>[] getExecutors() {
            return new ExecutorHandle[0];
        }

        @Override
        public void runLater(@NonNull Runnable runnable) {

        }

        @Override
        public <R> R callInMainThread(@NonNull Callable<R> callable) throws Throwable {
            return null;
        }

        @Override
        public ExecutorHandle<Object> wrap(Object handle) {
            return null;
        }

        @Override
        public void registerCommand(String name, CommandImplementation implementation) {

        }

        @Override
        public void something() {

        }
    }

    @Test
    public void testCall() throws Exception {
        FakeBackendHandle handle = new FakeBackendHandle(new Object());

        FakeBackendHandle spyInternal = spy(handle);
        FakeBackendHandle spyExternal = spy(handle);

        Backend backendInternal = new Backend(spyInternal, this.createEnvironment(handle));
        Backend backendExternal = new Backend(spyExternal, this.createEnvironment(handle));

        ComponentBag holder = new ComponentBag();
        try {
            holder.call(
                    TestInterface.class, TestInterface.class.getDeclaredMethod("something"),
                    backendInternal
            );
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }

        verify(spyInternal, times(1)).something();

        ComponentResolverTest crt = new ComponentResolverTest();
        holder.registerComponent(crt);
        try {
            holder.call(
                    TestInterface.class, TestInterface.class.getDeclaredMethod("something"),
                    backendExternal
            );
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }

        verify(spyExternal, never()).something();
        assertEquals(1, crt.called);
        assertEquals(spyExternal, crt.handle);
    }

    @Test
    public void testCallInternal() throws Exception {
        FakeBackendHandle handle = spy(new FakeBackendHandle(new Object()));

        ComponentBag holder = new ComponentBag();
        try {
            holder.callInternal(
                    TestInterface.class.getDeclaredMethod("something"),
                    this.createEnvironment(handle),
                    handle
            );
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }

        verify(handle, times(1)).something();
    }

    @Test
    public void testCallExternal() throws Exception {
        BackendHandle handle = mock(BackendHandle.class);
        ComponentBag holder = new ComponentBag();
        ComponentResolverTest crt = new ComponentResolverTest();
        holder.registerComponent(crt);
        try {
            holder.callExternal(
                    TestInterface.class, TestInterface.class.getDeclaredMethod("something"),
                    this.createEnvironment(handle),
                    handle
            );
        } catch (Throwable throwable) {
            throw new Exception(throwable);
        }

        assertEquals(1, crt.called);
        assertEquals(handle, crt.handle);
    }

    @Test
    public void testGetMethod() throws Exception {
        BackendHandle handle = mock(BackendHandle.class);
        ComponentBag holder = new ComponentBag();
        ComponentResolverTest crt = new ComponentResolverTest();
        holder.registerComponent(crt);
        assertEquals(
                ComponentResolverTest.class.getMethod("something", BackendHandle.class),
                holder.getMethod(ComponentResolverTest.class, TestInterface.class.getDeclaredMethod("something"), handle)
        );
    }

    private Environment createEnvironment(BackendHandle handle) {
        Environment env = mock(Environment.class);
        when(env.getEnvironment()).thenReturn(env);
        when(env.getBackend()).thenReturn(new Backend(handle, env));
        return env;
    }
}