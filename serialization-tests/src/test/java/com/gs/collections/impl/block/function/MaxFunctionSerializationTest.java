/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.block.function;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class MaxFunctionSerializationTest
{
    @Test
    public void maxDouble()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAERjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mdW5jdGlvbi5NYXhGdW5jdGlv
                biRNYXhEb3VibGVGdW5jdGlvbgAAAAAAAAABAgAAeHA=\
                """,
                MaxFunction.DOUBLE);
    }

    @Test
    public void maxInteger()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mdW5jdGlvbi5NYXhGdW5jdGlv
                biRNYXhJbnRlZ2VyRnVuY3Rpb24AAAAAAAAAAQIAAHhw\
                """,
                MaxFunction.INTEGER);
    }

    @Test
    public void maxLong()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mdW5jdGlvbi5NYXhGdW5jdGlv
                biRNYXhMb25nRnVuY3Rpb24AAAAAAAAAAQIAAHhw\
                """,
                MaxFunction.LONG);
    }
}
