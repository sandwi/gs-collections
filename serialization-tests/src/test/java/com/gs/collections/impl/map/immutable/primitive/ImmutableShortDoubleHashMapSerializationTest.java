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

package com.gs.collections.impl.map.immutable.primitive;

import com.gs.collections.impl.map.mutable.primitive.ShortDoubleHashMap;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ImmutableShortDoubleHashMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAHVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuaW1tdXRhYmxlLnByaW1pdGl2ZS5J
                bW11dGFibGVTaG9ydERvdWJsZUhhc2hNYXAkSW1tdXRhYmxlU2hvcnREb3VibGVNYXBTZXJpYWxp
                emF0aW9uUHJveHkAAAAAAAAAAQwAAHhwdxgAAAACAAE/8AAAAAAAAAACQAAAAAAAAAB4\
                """,
                new ImmutableShortDoubleHashMap(ShortDoubleHashMap.newWithKeysValues((short) 1, 1.0, (short) 2, 2.0)));
    }
}
