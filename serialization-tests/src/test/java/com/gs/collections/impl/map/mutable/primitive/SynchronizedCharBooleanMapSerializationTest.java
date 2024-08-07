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

public class SynchronizedCharBooleanMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAubXV0YWJsZS5wcmltaXRpdmUuU3lu
                Y2hyb25pemVkQ2hhckJvb2xlYW5NYXAAAAAAAAAAAQIAAkwABGxvY2t0ABJMamF2YS9sYW5nL09i
                amVjdDtMAANtYXB0ADxMY29tL2dzL2NvbGxlY3Rpb25zL2FwaS9tYXAvcHJpbWl0aXZlL011dGFi
                bGVDaGFyQm9vbGVhbk1hcDt4cHEAfgADc3IAQGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLm1hcC5t
                dXRhYmxlLnByaW1pdGl2ZS5DaGFyQm9vbGVhbkhhc2hNYXAAAAAAAAAAAQwAAHhwdwgAAAAAPwAA
                AHg=\
                """,
                new SynchronizedCharBooleanMap(new CharBooleanHashMap()));
    }
}
