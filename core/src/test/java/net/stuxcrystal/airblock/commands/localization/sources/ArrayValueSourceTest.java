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

package net.stuxcrystal.airblock.commands.localization.sources;

import net.stuxcrystal.airblock.utils.Optional;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ArrayValueSourceTest {

    private Map<String, Optional<String>> result = new HashMap<String, Optional<String>>();

    private String[] values = new String[]{ "abc", "test" };

    public ArrayValueSourceTest() {
        this.result.put("t", Optional.<String>empty());
        this.result.put("-1", Optional.<String>empty());
        this.result.put("1a", Optional.<String>empty());
        this.result.put("1.5", Optional.<String>empty());
        this.result.put("1", Optional.from("test"));
        this.result.put("0", Optional.from("abc"));
        this.result.put("1000", Optional.<String>empty());
    }

    @Test
    public void testGet() throws Exception {
        ArrayValueSource avs = new ArrayValueSource(this.values);
        for (Map.Entry<String, Optional<String>> testvals : this.result.entrySet()) {
            assertEquals(testvals.getValue(), avs.get(testvals.getKey()));
        }
    }
}