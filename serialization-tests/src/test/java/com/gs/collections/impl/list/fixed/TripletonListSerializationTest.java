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

package com.gs.collections.impl.list.fixed;

import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.list.mutable.FastListSerializationTest;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class TripletonListSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0LmZpeGVkLlRyaXBsZXRvbkxpc3QA
                AAAAAAAAAQwAAHhwcHBweA==\
                """,
                Lists.fixedSize.of(null, null, null));
    }

    @Test
    public void subList()
    {
        Verify.assertSerializedForm(
                1L,
                FastListSerializationTest.FAST_LIST_WITH_ONE_NULL,
                Lists.fixedSize.of(null, null, null).subList(0, 1));
    }
}
