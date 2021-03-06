package net.stuxcrystal.airblock.commands.contrib.annotations;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.stuxcrystal.airblock.commands.Executor;
import net.stuxcrystal.airblock.commands.arguments.list.ArgumentList;
import net.stuxcrystal.airblock.utils.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * A strategy that does not parse the method in any way.
 */
@RequiredArgsConstructor
public class RawMethodCallingStrategy  implements CommandCallingStrategy {

    @Getter
    @NonNull
    public final Method method;

    @Getter
    public final Object object;

    /**
     * Executes the command.
     * @param command   The command that has been executed.
     * @param executor  The executor that is executed.
     * @param raw       The raw command.
     */
    @Override
    public void call(AnnotationCommand command, Executor executor, String raw) {
        try {
            ReflectionUtils.invoke(this.method, this.object, executor, raw);
        } catch (Throwable throwable) {
            AnnotationCommand.throwError(
                    "Error occured while '" +  executor.getName() +"' executed '/" + command.getName() + " " + raw,
                    throwable, executor
            );
        }
    }

    /**
     * The raw name of the command.
     *
     * @return The raw name.
     */
    @Override
    public String getName() {
        return this.getMethod().getName();
    }

}
