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

package com.gs.collections.impl.bag.mutable.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class SynchronizedShortBagSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5iYWcubXV0YWJsZS5wcmltaXRpdmUuU3lu
                Y2hyb25pemVkU2hvcnRCYWcAAAAAAAAAAQIAAHhyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5j
                b2xsZWN0aW9uLm11dGFibGUucHJpbWl0aXZlLkFic3RyYWN0U3luY2hyb25pemVkU2hvcnRDb2xs
                ZWN0aW9uAAAAAAAAAAECAAJMAApjb2xsZWN0aW9udABETGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkv
                Y29sbGVjdGlvbi9wcmltaXRpdmUvTXV0YWJsZVNob3J0Q29sbGVjdGlvbjtMAARsb2NrdAASTGph
                dmEvbGFuZy9PYmplY3Q7eHBzcgA6Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmFnLm11dGFibGUu
                cHJpbWl0aXZlLlNob3J0SGFzaEJhZwAAAAAAAAABDAAAeHB3BAAAAAB4cQB+AAQ=\
                """,
                new SynchronizedShortBag(new ShortHashBag()));
    }
}
