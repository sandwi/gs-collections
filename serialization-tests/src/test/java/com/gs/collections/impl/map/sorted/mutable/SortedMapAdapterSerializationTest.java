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

package com.gs.collections.impl.map.sorted.mutable;

import java.util.TreeMap;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class SortedMapAdapterSerializationTest
{
    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5tYXAuc29ydGVkLm11dGFibGUuU29ydGVk
                TWFwQWRhcHRlcgAAAAAAAAABAgABTAAIZGVsZWdhdGV0ABVMamF2YS91dGlsL1NvcnRlZE1hcDt4
                cHNyABFqYXZhLnV0aWwuVHJlZU1hcAzB9j4tJWrmAwABTAAKY29tcGFyYXRvcnQAFkxqYXZhL3V0
                aWwvQ29tcGFyYXRvcjt4cHB3BAAAAAB4\
                """,
                new SortedMapAdapter<Object, Object>(new TreeMap<Object, Object>()));
    }
}
