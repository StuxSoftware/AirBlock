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
import net.stuxcrystal.airblock.commands.core.list.Command;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * The basic implementation of commands.
 */
public abstract class AnnotationCommand implements Command {

    /**
     * Contains the command metadata.
     */
    @Getter(AccessLevel.PROTECTED)
    private final SimpleCommand command;

    /**
     * Contains the method that should be called.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Method method;

    /**
     * Contains the instance of the command.
     */
    @Getter(AccessLevel.PROTECTED)
    private final Object instance;

    /**
     * Creates a new annotation command.
     * @param command  The new command.
     * @param method   The method.
     * @param instance The instance that is used for the commands.
     */
    public AnnotationCommand(SimpleCommand command, Method method, Object instance) {
        this.command = command;
        this.method = method;
        this.instance = instance;
    }

    @Override
    public String getName() {
        if (this.getCommand().value().equals(" "))
            return this.method.getName();
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
        for (SimpleCommand.Executor type : this.getCommand().executors()) {
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

    public abstract void executeNow(@NonNull Executor executor, @NonNull String rawArguments);
}