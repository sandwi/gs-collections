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

package com.gs.collections.impl.list.mutable;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class FastListSerializationTest
{
    public static final String FAST_LIST_WITH_ONE_NULL =
            """
            rO0ABXNyAC1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUuRmFzdExpc3QAAAAA
            AAAAAQwAAHhwdwQAAAABcHg=\
            """;
    public static final String FAST_LIST_EMPTY =
            """
            rO0ABXNyAC1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUuRmFzdExpc3QAAAAA
            AAAAAQwAAHhwdwQAAAAAeA==\
            """;

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                FAST_LIST_EMPTY,
                FastList.newList());
    }

    @Test
    public void listWithOneNull()
    {
        Verify.assertSerializedForm(
                1L,
                FAST_LIST_WITH_ONE_NULL,
                FastList.newListWith((Object) null));
    }

    @Test
    public void subList()
    {
        // SerialVersionUID not important for objects with writeReplace()
        Verify.assertSerializedForm(
                FastList.newListWith(null, null).subList(0, 1),
                FAST_LIST_WITH_ONE_NULL);
    }
}
