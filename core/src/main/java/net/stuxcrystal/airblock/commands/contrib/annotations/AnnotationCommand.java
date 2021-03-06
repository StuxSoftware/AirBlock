/*
 * AirBlock - Framework for Multi-Platform Minecraft-Plugins.
 * Copyright (C) 2014 stux!
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.stuxcrystal.airblock.commands.contrib.annotations;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.stuxcrystal.airblock.commands.Executor;
import net.stuxcrystal.airblock.commands.contrib.Permissions;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The basic implementation of commands.
 */
public class AnnotationCommand implements net.stuxcrystal.airblock.commands.core.list.Command {

    /**
     * Contains the command metadata.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Command command;

    @Getter(AccessLevel.PROTECTED)
    private final CommandCallingStrategy implementation;

    /**
     * Creates a new annotation command.
     * @param command  The new command.
     * @param strategy How should the command be called.
     */
    public AnnotationCommand(Command command, CommandCallingStrategy strategy) {
        this.command = command;
        this.implementation = strategy;
    }

    @Override
    public String getName() {
        if (this.getCommand().value().equals(" "))
            return this.implementation.getName();
        return this.getCommand().value();
    }

    @Override
    public String getDescription() {
        String result = this.getCommand().description();
        if (StringUtils.isEmpty(result))
            return null;
        return result;
    }

    @Override
    public boolean canExecute(@NonNull Executor executor, @Nullable String rawArguments) {
        // Check if the executor type is supported.
        boolean allowed = false;
        for (Command.Executor type : this.getCommand().executors()) {
            if (type.isSupported(executor)) {
                allowed = true;
                break;
            }
        }
        if (!allowed)
            return false;

        // Checks if we are allowed to execute the command.
        if (executor.hasComponent(Permissions.class)) {
            if (!this.getCommand().permission().isEmpty())
                if (!executor.hasPermission(this.getCommand().permission()))
                    return false;
        } else {
            if (this.getCommand().adminRequired())
                if (!executor.isAdmin())
                    return false;
        }

        return true;
    }

    /**
     * Executes the command.
     *
     * @param executor     The executor that executes the command.
     * @param rawArguments The arguments as a single string.
     */
    @Override
    public void execute(@NonNull Executor executor, @NonNull String rawArguments) {
        if (this.getCommand().async()) {
            executor.getEnvironment().getBackend().runAsynchronously(new CommandRunner(this, executor, rawArguments));
        } else {
            this.executeNow(executor, rawArguments);
        }
    }

    public void executeNow(@NonNull Executor executor, @NonNull String rawArguments) {
        this.getImplementation().call(this, executor, rawArguments);
    }

    public static void throwError(String message, Throwable throwable, Executor executor) {
        Logger logger = executor.getEnvironment().getBackend().getLogger();

        if (logger != null) {
            logger.log(Level.SEVERE, message, throwable);
        } else {
            System.err.println(message);
            throwable.printStackTrace();
        }
    }
}
