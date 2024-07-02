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

package com.gs.collections.impl.set.mutable.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class UnmodifiableShortSetSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zZXQubXV0YWJsZS5wcmltaXRpdmUuVW5t
                b2RpZmlhYmxlU2hvcnRTZXQAAAAAAAAAAQIAAHhyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5j
                b2xsZWN0aW9uLm11dGFibGUucHJpbWl0aXZlLkFic3RyYWN0VW5tb2RpZmlhYmxlU2hvcnRDb2xs
                ZWN0aW9uAAAAAAAAAAECAAFMAApjb2xsZWN0aW9udABETGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkv
                Y29sbGVjdGlvbi9wcmltaXRpdmUvTXV0YWJsZVNob3J0Q29sbGVjdGlvbjt4cHNyADpjb20uZ3Mu
                Y29sbGVjdGlvbnMuaW1wbC5zZXQubXV0YWJsZS5wcmltaXRpdmUuU2hvcnRIYXNoU2V0AAAAAAAA
                AAEMAAB4cHcEAAAAAHg=\
                """,
                new UnmodifiableShortSet(new ShortHashSet()));
    }
}
