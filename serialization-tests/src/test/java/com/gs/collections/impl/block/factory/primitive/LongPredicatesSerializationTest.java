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

public class LongPredicatesSerializationTest
{
    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRBbHdheXNGYWxzZUxvbmdQcmVkaWNhdGUAAAAAAAAAAQIAAHhw\
                """,
                LongPredicates.alwaysFalse());
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRBbHdheXNUcnVlTG9uZ1ByZWRpY2F0ZQAAAAAAAAABAgAAeHA=\
                """,
                LongPredicates.alwaysTrue());
    }

    @Test
    public void equal()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRFcXVhbHNMb25nUHJlZGljYXRlAAAAAAAAAAECAAFKAAhleHBlY3RlZHhw
                AAAAAAAAAAA=\
                """,
                LongPredicates.equal(0L));
    }

    @Test
    public void lessThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFRjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRMZXNzVGhhbkxvbmdQcmVkaWNhdGUAAAAAAAAAAQIAAUoACGV4cGVjdGVk
                eHAAAAAAAAAAAA==\
                """,
                LongPredicates.lessThan(0L));
    }

    @Test
    public void greaterThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRHcmVhdGVyVGhhbkxvbmdQcmVkaWNhdGUAAAAAAAAAAQIAAUoACGV4cGVj
                dGVkeHAAAAAAAAAAAA==\
                """,
                LongPredicates.greaterThan(0L));
    }

    @Test
    public void isEven()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRMb25nSXNFdmVuUHJlZGljYXRlAAAAAAAAAAECAAB4cA==\
                """,
                LongPredicates.isEven());
    }

    @Test
    public void isOdd()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAFFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRMb25nSXNPZGRQcmVkaWNhdGUAAAAAAAAAAQIAAHhw\
                """,
                LongPredicates.isOdd());
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAE9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRBbmRMb25nUHJlZGljYXRlAAAAAAAAAAECAAJMAANvbmV0AEBMY29tL2dz
                L2NvbGxlY3Rpb25zL2FwaS9ibG9jay9wcmVkaWNhdGUvcHJpbWl0aXZlL0xvbmdQcmVkaWNhdGU7
                TAADdHdvcQB+AAF4cHNyAFJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnBy
                aW1pdGl2ZS5Mb25nUHJlZGljYXRlcyRMb25nSXNFdmVuUHJlZGljYXRlAAAAAAAAAAECAAB4cHNy
                AFFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5Mb25nUHJl
                ZGljYXRlcyRMb25nSXNPZGRQcmVkaWNhdGUAAAAAAAAAAQIAAHhw\
                """,
                LongPredicates.and(LongPredicates.isEven(), LongPredicates.isOdd()));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAE5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyRPckxvbmdQcmVkaWNhdGUAAAAAAAAAAQIAAkwAA29uZXQAQExjb20vZ3Mv
                Y29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9wcmltaXRpdmUvTG9uZ1ByZWRpY2F0ZTtM
                AAN0d29xAH4AAXhwc3IAUmNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJp
                bWl0aXZlLkxvbmdQcmVkaWNhdGVzJExvbmdJc0V2ZW5QcmVkaWNhdGUAAAAAAAAAAQIAAHhwc3IA
                UWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkucHJpbWl0aXZlLkxvbmdQcmVk
                aWNhdGVzJExvbmdJc09kZFByZWRpY2F0ZQAAAAAAAAABAgAAeHA=\
                """,
                LongPredicates.or(LongPredicates.isEven(), LongPredicates.isOdd()));
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAE9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LnByaW1pdGl2ZS5M
                b25nUHJlZGljYXRlcyROb3RMb25nUHJlZGljYXRlAAAAAAAAAAECAAFMAAZuZWdhdGV0AEBMY29t
                L2dzL2NvbGxlY3Rpb25zL2FwaS9ibG9jay9wcmVkaWNhdGUvcHJpbWl0aXZlL0xvbmdQcmVkaWNh
                dGU7eHBzcgBSY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5wcmltaXRpdmUu
                TG9uZ1ByZWRpY2F0ZXMkTG9uZ0lzRXZlblByZWRpY2F0ZQAAAAAAAAABAgAAeHA=\
                """,
                LongPredicates.not(LongPredicates.isEven()));
    }
}
