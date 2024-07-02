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

package com.gs.collections.impl.list.immutable;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class ImmutableOctupletonListSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                new ImmutableOctupletonList<Integer>(1, 2, 3, 4, 5, 6, 7, 8),
                """
                rO0ABXNyAD5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0LmltbXV0YWJsZS5JbW11dGFibGVP
                Y3R1cGxldG9uTGlzdAAAAAAAAAABAgAITAAIZWxlbWVudDF0ABJMamF2YS9sYW5nL09iamVjdDtM
                AAhlbGVtZW50MnEAfgABTAAIZWxlbWVudDNxAH4AAUwACGVsZW1lbnQ0cQB+AAFMAAhlbGVtZW50
                NXEAfgABTAAIZWxlbWVudDZxAH4AAUwACGVsZW1lbnQ3cQB+AAFMAAhlbGVtZW50OHEAfgABeHBz
                cgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGphdmEubGFuZy5OdW1i
                ZXKGrJUdC5TgiwIAAHhwAAAAAXNxAH4AAwAAAAJzcQB+AAMAAAADc3EAfgADAAAABHNxAH4AAwAA
                AAVzcQB+AAMAAAAGc3EAfgADAAAAB3NxAH4AAwAAAAg=\
                """);
    }
}
