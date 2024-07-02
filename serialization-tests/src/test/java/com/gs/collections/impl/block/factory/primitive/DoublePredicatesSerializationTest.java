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

public class DoublePredicatesSerializationTest
{
    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJEFsd2F5c0ZhbHNlRG91YmxlUHJlZGljYXRlAAAAAAAAAAECAAB4cA==
                """,
                DoublePredicates.alwaysFalse());
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJEFsd2F5c1RydWVEb3VibGVQcmVkaWNhdGUAAAAAAAAAAQIAAHhw\
                """,
                DoublePredicates.alwaysTrue());
    }

    @Test
    public void equal()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJEVxdWFsc0RvdWJsZVByZWRpY2F0ZQAAAAAAAAABAgABRAAIZXhwZWN0
                ZWR4cAAAAAAAAAAA\
                """,
                DoublePredicates.equal(0.0));
    }

    @Test
    public void lessThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJExlc3NUaGFuRG91YmxlUHJlZGljYXRlAAAAAAAAAAECAAFEAAhleHBl
                Y3RlZHhwAAAAAAAAAAA=\
                """,
                DoublePredicates.lessThan(0.0));
    }

    @Test
    public void greaterThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJEdyZWF0ZXJUaGFuRG91YmxlUHJlZGljYXRlAAAAAAAAAAECAAFEAAhl
                eHBlY3RlZHhwAAAAAAAAAAA=\
                """,
                DoublePredicates.greaterThan(0.0));
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFNjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJEFuZERvdWJsZVByZWRpY2F0ZQAAAAAAAAABAgACTAADb25ldABCTGNv
                bS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL3ByaW1pdGl2ZS9Eb3VibGVQcmVk
                aWNhdGU7TAADdHdvcQB+AAF4cHNyAFhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0
                b3J5LnByaW1pdGl2ZS5Eb3VibGVQcmVkaWNhdGVzJExlc3NUaGFuRG91YmxlUHJlZGljYXRlAAAA
                AAAAAAECAAFEAAhleHBlY3RlZHhwAAAAAAAAAABzcQB+AAMAAAAAAAAAAA==\
                """,
                DoublePredicates.and(DoublePredicates.lessThan(0.0), DoublePredicates.lessThan(0.0)));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJE9yRG91YmxlUHJlZGljYXRlAAAAAAAAAAECAAJMAANvbmV0AEJMY29t
                L2dzL2NvbGxlY3Rpb25zL2FwaS9ibG9jay9wcmVkaWNhdGUvcHJpbWl0aXZlL0RvdWJsZVByZWRp
                Y2F0ZTtMAAN0d29xAH4AAXhwc3IAWGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rv
                cnkucHJpbWl0aXZlLkRvdWJsZVByZWRpY2F0ZXMkTGVzc1RoYW5Eb3VibGVQcmVkaWNhdGUAAAAA
                AAAAAQIAAUQACGV4cGVjdGVkeHAAAAAAAAAAAHNxAH4AAwAAAAAAAAAA\
                """,
                DoublePredicates.or(DoublePredicates.lessThan(0.0), DoublePredicates.lessThan(0.0)));
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFNjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5E
                b3VibGVQcmVkaWNhdGVzJE5vdERvdWJsZVByZWRpY2F0ZQAAAAAAAAABAgABTAAGbmVnYXRldABC
                TGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL3ByaW1pdGl2ZS9Eb3VibGVQ
                cmVkaWNhdGU7eHBzcgBYY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5wcmlt
                aXRpdmUuRG91YmxlUHJlZGljYXRlcyRMZXNzVGhhbkRvdWJsZVByZWRpY2F0ZQAAAAAAAAABAgAB
                RAAIZXhwZWN0ZWR4cAAAAAAAAAAA\
                """,
                DoublePredicates.not(DoublePredicates.lessThan(0.0)));
    }
}
