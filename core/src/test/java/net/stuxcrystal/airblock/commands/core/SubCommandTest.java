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

package net.stuxcrystal.airblock.commands.core;

import org.junit.Test;

import static org.junit.Assert.*;

public class SubCommandTest {

    static final String[] toParse = new String[] {
            null,
            "",
            "ab",
            "ab abc",
            "ab abc def"
    };

    static final String[][] result = new String[][] {
            new String[] {"", ""},
            new String[] {"", ""},
            new String[] {"ab", ""},
            new String[] {"ab", "abc"},
            new String[] {"ab", "abc def"}
    };

    @Test
    public void testSplitArguments() throws Exception {
        for (int i = 0; i<SubCommandTest.toParse.length; i++) {
            String[] result = SubCommand.splitArguments(SubCommandTest.toParse[i]);
            assertArrayEquals(SubCommandTest.result[i], result);
        }
    }
}