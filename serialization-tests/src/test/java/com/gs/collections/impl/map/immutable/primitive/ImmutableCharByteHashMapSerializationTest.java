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

import com.gs.collections.impl.map.mutable.primitive.CharByteHashMap;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ImmutableCharByteHashMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAG9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuaW1tdXRhYmxlLnByaW1pdGl2ZS5J
                bW11dGFibGVDaGFyQnl0ZUhhc2hNYXAkSW1tdXRhYmxlQ2hhckJ5dGVNYXBTZXJpYWxpemF0aW9u
                UHJveHkAAAAAAAAAAQwAAHhwdwoAAAACAAEBAAICeA==\
                """,
                new ImmutableCharByteHashMap(CharByteHashMap.newWithKeysValues((char) 1, (byte) 1, (char) 2, (byte) 2)));
    }
}
