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

package com.gs.collections.impl.block.factory.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class FloatPredicatesSerializationTest
{
    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkQWx3YXlzRmFsc2VGbG9hdFByZWRpY2F0ZQAAAAAAAAABAgAAeHA=\
                """,
                FloatPredicates.alwaysFalse());
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkQWx3YXlzVHJ1ZUZsb2F0UHJlZGljYXRlAAAAAAAAAAECAAB4cA==\
                """,
                FloatPredicates.alwaysTrue());
    }

    @Test
    public void equal()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFRjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkRXF1YWxzRmxvYXRQcmVkaWNhdGUAAAAAAAAAAQIAAUYACGV4cGVjdGVk
                eHAAAAAA\
                """,
                FloatPredicates.equal(0.0f));
    }

    @Test
    public void lessThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkTGVzc1RoYW5GbG9hdFByZWRpY2F0ZQAAAAAAAAABAgABRgAIZXhwZWN0
                ZWR4cAAAAAA=\
                """,
                FloatPredicates.lessThan(0.0f));
    }

    @Test
    public void greaterThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkR3JlYXRlclRoYW5GbG9hdFByZWRpY2F0ZQAAAAAAAAABAgABRgAIZXhw
                ZWN0ZWR4cAAAAAA=\
                """,
                FloatPredicates.greaterThan(0.0f));
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkQW5kRmxvYXRQcmVkaWNhdGUAAAAAAAAAAQIAAkwAA29uZXQAQUxjb20v
                Z3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9wcmltaXRpdmUvRmxvYXRQcmVkaWNh
                dGU7TAADdHdvcQB+AAF4cHNyAFZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5
                LnByaW1pdGl2ZS5GbG9hdFByZWRpY2F0ZXMkTGVzc1RoYW5GbG9hdFByZWRpY2F0ZQAAAAAAAAAB
                AgABRgAIZXhwZWN0ZWR4cAAAAABzcgBZY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFj
                dG9yeS5wcmltaXRpdmUuRmxvYXRQcmVkaWNhdGVzJEdyZWF0ZXJUaGFuRmxvYXRQcmVkaWNhdGUA
                AAAAAAAAAQIAAUYACGV4cGVjdGVkeHAAAAAA\
                """,
                FloatPredicates.and(FloatPredicates.lessThan(0.0f), FloatPredicates.greaterThan(0.0f)));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkT3JGbG9hdFByZWRpY2F0ZQAAAAAAAAABAgACTAADb25ldABBTGNvbS9n
                cy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL3ByaW1pdGl2ZS9GbG9hdFByZWRpY2F0
                ZTtMAAN0d29xAH4AAXhwc3IAVmNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rvcnku
                cHJpbWl0aXZlLkZsb2F0UHJlZGljYXRlcyRMZXNzVGhhbkZsb2F0UHJlZGljYXRlAAAAAAAAAAEC
                AAFGAAhleHBlY3RlZHhwAAAAAHNyAFljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0
                b3J5LnByaW1pdGl2ZS5GbG9hdFByZWRpY2F0ZXMkR3JlYXRlclRoYW5GbG9hdFByZWRpY2F0ZQAA
                AAAAAAABAgABRgAIZXhwZWN0ZWR4cAAAAAA=\
                """,
                FloatPredicates.or(FloatPredicates.lessThan(0.0f), FloatPredicates.greaterThan(0.0f)));
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5G
                bG9hdFByZWRpY2F0ZXMkTm90RmxvYXRQcmVkaWNhdGUAAAAAAAAAAQIAAUwABm5lZ2F0ZXQAQUxj
                b20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9wcmltaXRpdmUvRmxvYXRQcmVk
                aWNhdGU7eHBzcgBWY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5wcmltaXRp
                dmUuRmxvYXRQcmVkaWNhdGVzJExlc3NUaGFuRmxvYXRQcmVkaWNhdGUAAAAAAAAAAQIAAUYACGV4
                cGVjdGVkeHAAAAAA\
                """,
                FloatPredicates.not(FloatPredicates.lessThan(0.0f)));
    }
}
