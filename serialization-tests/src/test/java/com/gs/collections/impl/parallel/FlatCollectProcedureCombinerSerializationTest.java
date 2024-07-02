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

package com.gs.collections.impl.parallel;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class FlatCollectProcedureCombinerSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5wYXJhbGxlbC5GbGF0Q29sbGVjdFByb2Nl
                ZHVyZUNvbWJpbmVyAAAAAAAAAAECAAB4cgBBY29tLmdzLmNvbGxlY3Rpb25zLmltcGwucGFyYWxs
                ZWwuQWJzdHJhY3RUcmFuc2Zvcm1lckJhc2VkQ29tYmluZXIAAAAAAAAAAQIAAUwABnJlc3VsdHQA
                FkxqYXZhL3V0aWwvQ29sbGVjdGlvbjt4cgA6Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwucGFyYWxs
                ZWwuQWJzdHJhY3RQcm9jZWR1cmVDb21iaW5lcgAAAAAAAAABAgABWgANdXNlQ29tYmluZU9uZXhw
                AHNyAC1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUuRmFzdExpc3QAAAAAAAAA
                AQwAAHhwdwQAAAAAeA==\
                """,
                new FlatCollectProcedureCombiner<Object, Object>(null, null, 1, false));
    }
}
