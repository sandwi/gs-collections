/*
 * Copyright 2013 Goldman Sachs.
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

package com.gs.collections.impl.map.mutable.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class UnmodifiableShortCharMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAubXV0YWJsZS5wcmltaXRpdmUuVW5t
                b2RpZmlhYmxlU2hvcnRDaGFyTWFwAAAAAAAAAAECAAFMAANtYXB0ADpMY29tL2dzL2NvbGxlY3Rp
                b25zL2FwaS9tYXAvcHJpbWl0aXZlL011dGFibGVTaG9ydENoYXJNYXA7eHBzcgA+Y29tLmdzLmNv
                bGxlY3Rpb25zLmltcGwubWFwLm11dGFibGUucHJpbWl0aXZlLlNob3J0Q2hhckhhc2hNYXAAAAAA
                AAAAAQwAAHhwdwQAAAAAeA==\
                """,
                new UnmodifiableShortCharMap(new ShortCharHashMap()));
    }
}
