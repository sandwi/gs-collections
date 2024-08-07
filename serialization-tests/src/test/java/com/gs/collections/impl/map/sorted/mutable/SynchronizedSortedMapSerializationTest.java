/*
 * Copyright 2015 Goldman Sachs.
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

package com.gs.collections.impl.map.sorted.mutable;

import com.gs.collections.impl.factory.SortedMaps;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class SynchronizedSortedMapSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                2L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAubXV0YWJsZS5TeW5jaHJvbml6ZWRN
                YXBTZXJpYWxpemF0aW9uUHJveHkAAAAAAAAAAQwAAHhwc3IAOGNvbS5ncy5jb2xsZWN0aW9ucy5p
                bXBsLm1hcC5zb3J0ZWQubXV0YWJsZS5UcmVlU29ydGVkTWFwAAAAAAAAAAEMAAB4cHB3BAAAAAB4
                eA==\
                """,
                SynchronizedSortedMap.of(SortedMaps.mutable.of()));
    }
}
