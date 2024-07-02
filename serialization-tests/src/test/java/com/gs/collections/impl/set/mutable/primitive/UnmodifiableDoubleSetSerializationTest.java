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

public class UnmodifiableDoubleSetSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zZXQubXV0YWJsZS5wcmltaXRpdmUuVW5t
                b2RpZmlhYmxlRG91YmxlU2V0AAAAAAAAAAECAAB4cgBZY29tLmdzLmNvbGxlY3Rpb25zLmltcGwu
                Y29sbGVjdGlvbi5tdXRhYmxlLnByaW1pdGl2ZS5BYnN0cmFjdFVubW9kaWZpYWJsZURvdWJsZUNv
                bGxlY3Rpb24AAAAAAAAAAQIAAUwACmNvbGxlY3Rpb250AEVMY29tL2dzL2NvbGxlY3Rpb25zL2Fw
                aS9jb2xsZWN0aW9uL3ByaW1pdGl2ZS9NdXRhYmxlRG91YmxlQ29sbGVjdGlvbjt4cHNyADtjb20u
                Z3MuY29sbGVjdGlvbnMuaW1wbC5zZXQubXV0YWJsZS5wcmltaXRpdmUuRG91YmxlSGFzaFNldAAA
                AAAAAAABDAAAeHB3BAAAAAB4\
                """,
                new UnmodifiableDoubleSet(new DoubleHashSet()));
    }
}
