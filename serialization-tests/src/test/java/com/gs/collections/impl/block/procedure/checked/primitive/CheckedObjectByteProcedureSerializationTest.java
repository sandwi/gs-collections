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

public class CheckedObjectByteProcedureSerializationTest
{
    private static final CheckedObjectByteProcedure<?> CHECKED_OBJECT_BYTE_PROCEDURE = new CheckedObjectByteProcedure<Object>()
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void safeValue(Object item1, byte item2) throws Exception
        {
        }
    };

    @Test
    public void serializedForm()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAGdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5wcm9jZWR1cmUuY2hlY2tlZC5w
                cmltaXRpdmUuQ2hlY2tlZE9iamVjdEJ5dGVQcm9jZWR1cmVTZXJpYWxpemF0aW9uVGVzdCQxAAAA
                AAAAAAECAAB4cgBUY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2sucHJvY2VkdXJlLmNoZWNr
                ZWQucHJpbWl0aXZlLkNoZWNrZWRPYmplY3RCeXRlUHJvY2VkdXJlAAAAAAAAAAECAAB4cA==\
                """,
                CHECKED_OBJECT_BYTE_PROCEDURE);
    }
}
