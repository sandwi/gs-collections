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

package com.gs.collections.impl.stack.immutable;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ImmutableArrayStackSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zdGFjay5pbW11dGFibGUuSW1tdXRhYmxl
                QXJyYXlTdGFjayRJbW11dGFibGVTdGFja1NlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3
                BAAAAAB4\
                """,
                ImmutableArrayStack.newStack());
    }

    @Test
    public void serializedForm_with_element()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zdGFjay5pbW11dGFibGUuSW1tdXRhYmxl
                QXJyYXlTdGFjayRJbW11dGFibGVTdGFja1NlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3
                BAAAAAFweA==\
                """,
                ImmutableArrayStack.newStackWith((Object) null));
    }

    @Test
    public void serializedForm_with_elements()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zdGFjay5pbW11dGFibGUuSW1tdXRhYmxl
                QXJyYXlTdGFjayRJbW11dGFibGVTdGFja1NlcmlhbGl6YXRpb25Qcm94eQAAAAAAAAABDAAAeHB3
                BAAAAAVwcHBwcHg=\
                """,
                ImmutableArrayStack.newStackWith(null, null, null, null, null));
    }
}
