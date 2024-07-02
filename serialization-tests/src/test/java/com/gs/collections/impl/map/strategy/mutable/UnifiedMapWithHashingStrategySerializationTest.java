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

package com.gs.collections.impl.map.strategy.mutable;

import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class UnifiedMapWithHashingStrategySerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuc3RyYXRlZ3kubXV0YWJsZS5Vbmlm
                aWVkTWFwV2l0aEhhc2hpbmdTdHJhdGVneQAAAAAAAAABDAAAeHBzcgBHY29tLmdzLmNvbGxlY3Rp
                b25zLmltcGwuYmxvY2suZmFjdG9yeS5IYXNoaW5nU3RyYXRlZ2llcyREZWZhdWx0U3RyYXRlZ3kA
                AAAAAAAAAQIAAHhwdwgAAAAAP0AAAHg=\
                """,
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy()));
    }

    @Test
    public void keySet()
    {
        // SerialVersionUID not important for objects with writeReplace()
        Verify.assertSerializedForm(
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy()).keySet(),
                """
                rO0ABXNyAEpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zZXQuc3RyYXRlZ3kubXV0YWJsZS5Vbmlm
                aWVkU2V0V2l0aEhhc2hpbmdTdHJhdGVneQAAAAAAAAABDAAAeHBzcgBHY29tLmdzLmNvbGxlY3Rp
                b25zLmltcGwuYmxvY2suZmFjdG9yeS5IYXNoaW5nU3RyYXRlZ2llcyREZWZhdWx0U3RyYXRlZ3kA
                AAAAAAAAAQIAAHhwdwgAAAAAP0AAAHg=\
                """);
    }

    @Test
    public void entrySet()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFNjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuc3RyYXRlZ3kubXV0YWJsZS5Vbmlm
                aWVkTWFwV2l0aEhhc2hpbmdTdHJhdGVneSRFbnRyeVNldAAAAAAAAAABAgABTAAGdGhpcyQwdABM
                TGNvbS9ncy9jb2xsZWN0aW9ucy9pbXBsL21hcC9zdHJhdGVneS9tdXRhYmxlL1VuaWZpZWRNYXBX
                aXRoSGFzaGluZ1N0cmF0ZWd5O3hwc3IASmNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLm1hcC5zdHJh
                dGVneS5tdXRhYmxlLlVuaWZpZWRNYXBXaXRoSGFzaGluZ1N0cmF0ZWd5AAAAAAAAAAEMAAB4cHNy
                AEdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5Lkhhc2hpbmdTdHJhdGVnaWVz
                JERlZmF1bHRTdHJhdGVneQAAAAAAAAABAgAAeHB3CAAAAAA/QAAAeA==\
                """,
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy()).entrySet());
    }

    @Test
    public void values()
    {
        Verify.assertSerializedForm(
                UnifiedMapWithHashingStrategy.newMap(HashingStrategies.defaultStrategy()).values(),
                """
                rO0ABXNyAC1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5saXN0Lm11dGFibGUuRmFzdExpc3QAAAAA
                AAAAAQwAAHhwdwQAAAAAeA==\
                """);
    }
}
