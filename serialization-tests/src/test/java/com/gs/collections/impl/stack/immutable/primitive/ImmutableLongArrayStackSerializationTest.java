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

package com.gs.collections.impl.stack.immutable.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ImmutableLongArrayStackSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAG5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zdGFjay5pbW11dGFibGUucHJpbWl0aXZl
                LkltbXV0YWJsZUxvbmdBcnJheVN0YWNrJEltbXV0YWJsZUxvbmdTdGFja1NlcmlhbGl6YXRpb25Q
                cm94eQAAAAAAAAABDAAAeHB3HAAAAAMAAAAAAAAAAwAAAAAAAAACAAAAAAAAAAF4\
                """,
                ImmutableLongArrayStack.newStackWith(1L, 2L, 3L));
    }
}
