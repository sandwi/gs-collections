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

package com.gs.collections.impl.block.procedure.checked.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class CheckedObjectFloatProcedureSerializationTest
{
    private static final CheckedObjectFloatProcedure<?> CHECKED_OBJECT_FLOAT_PROCEDURE = new CheckedObjectFloatProcedure<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void safeValue(Object item1, float item2) throws Exception
        {
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAGhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuY2hlY2tlZC5w
                cmltaXRpdmUuQ2hlY2tlZE9iamVjdEZsb2F0UHJvY2VkdXJlU2VyaWFsaXphdGlvblRlc3QkMQAA
                AAAAAAABAgAAeHIAVWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByb2NlZHVyZS5jaGVj
                a2VkLnByaW1pdGl2ZS5DaGVja2VkT2JqZWN0RmxvYXRQcm9jZWR1cmUAAAAAAAAAAQIAAHhw\
                """,
                CHECKED_OBJECT_FLOAT_PROCEDURE);
    }
}
