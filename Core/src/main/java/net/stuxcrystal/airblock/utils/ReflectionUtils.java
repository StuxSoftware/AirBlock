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

package net.stuxcrystal.airblock.utils;

import lombok.NonNull;

import java.lang.reflect.*;

/**
 * Utilities for reflections.
 */
public class ReflectionUtils {

    /**
     * Extractes the {@link Class} out of the type.
     *
     * @param type The type where the class is to extract.
     * @return a Class object.
     */
    @NonNull
    public static Class<?> toClass(@NonNull Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            return Array.newInstance(toClass(((GenericArrayType) type).getGenericComponentType()), 0).getClass();
        } else {
            throw new IllegalArgumentException("This method does not support the type");
        }
    }

    /**
     * Returns the generic component type of the object.
     * @param type The type of the object.
     * @return The generic component type.
     */
    @NonNull
    public static Type getGenericComponentType(@NonNull Type type) {
        if (type instanceof Class<?>)
            return ((Class) type).getComponentType();
        else if (type instanceof GenericArrayType)
            return ((GenericArrayType) type).getGenericComponentType();
        return null;
    }

    /**
     * Invoke the method.
     * @param method      The method that should be invoked.
     * @param instance    The instance that should be passed.
     * @param parameters  The parameters that should be passed.
     * @param <T> The return type.
     * @return The return type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T invoke(Method method, Object instance, Object... parameters) throws Throwable {
        if (!method.isAccessible())
            method.setAccessible(true);

        // Since static methods do not
        if (Modifier.isStatic(method.getModifiers()) && instance != null)
            instance = null;

        try {
            return (T)method.invoke(instance, parameters);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke method", e);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
