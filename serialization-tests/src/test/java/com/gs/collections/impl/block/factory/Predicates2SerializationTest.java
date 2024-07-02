/*
 * Copyright 2014 Goldman Sachs.
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

package com.gs.collections.impl.block.factory;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class Predicates2SerializationTest
{
    @Test
    public void throwing()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JFRocm93aW5nUHJlZGljYXRlMkFkYXB0ZXIAAAAAAAAAAQIAAUwAEnRocm93aW5nUHJlZGljYXRl
                MnQARExjb20vZ3MvY29sbGVjdGlvbnMvaW1wbC9ibG9jay9wcmVkaWNhdGUvY2hlY2tlZC9UaHJv
                d2luZ1ByZWRpY2F0ZTI7eHIAQWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLnByZWRpY2F0
                ZS5jaGVja2VkLkNoZWNrZWRQcmVkaWNhdGUyAAAAAAAAAAECAAB4cHA=\
                """,
                Predicates2.throwing(null));
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEFsd2F5c1RydWUAAAAAAAAAAQIAAHhyADFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5m
                YWN0b3J5LlByZWRpY2F0ZXMyAAAAAAAAAAECAAB4cA==\
                """,
                Predicates2.alwaysTrue());
    }

    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEFsd2F5c0ZhbHNlAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2su
                ZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.alwaysFalse());
    }

    @Test
    public void attributeEqual()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEF0dHJpYnV0ZVByZWRpY2F0ZXMyAAAAAAAAAAECAAJMAAhmdW5jdGlvbnQAMExjb20vZ3MvY29s
                bGVjdGlvbnMvYXBpL2Jsb2NrL2Z1bmN0aW9uL0Z1bmN0aW9uO0wACXByZWRpY2F0ZXQAM0xjb20v
                Z3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9QcmVkaWNhdGUyO3hyADFjb20uZ3Mu
                Y29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMyAAAAAAAAAAECAAB4cHBz
                cgA3Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzMiRFcXVh
                bAAAAAAAAAABAgAAeHEAfgAD\
                """,
                Predicates2.attributeEqual(null));
    }

    @Test
    public void instanceOf()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JElzSW5zdGFuY2VPZgAAAAAAAAABAgAAeHIAMWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2Nr
                LmZhY3RvcnkuUHJlZGljYXRlczIAAAAAAAAAAQIAAHhw\
                """,
                Predicates2.instanceOf());
    }

    @Test
    public void notInstanceOf()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdEluc3RhbmNlT2YAAAAAAAAAAQIAAHhyADFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9j
                ay5mYWN0b3J5LlByZWRpY2F0ZXMyAAAAAAAAAAECAAB4cA==\
                """,
                Predicates2.notInstanceOf());
    }

    @Test
    public void lessThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JExlc3NUaGFuAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFj
                dG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.<Integer>lessThan());
    }

    @Test
    public void lessThanOrEqualTo()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JExlc3NUaGFuT3JFcXVhbAAAAAAAAAABAgAAeHIAMWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJs
                b2NrLmZhY3RvcnkuUHJlZGljYXRlczIAAAAAAAAAAQIAAHhw\
                """,
                Predicates2.<Integer>lessThanOrEqualTo());
    }

    @Test
    public void greaterThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEdyZWF0ZXJUaGFuAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2su
                ZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.<Integer>greaterThan());
    }

    @Test
    public void greaterThanOrEqualTo()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAERjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEdyZWF0ZXJUaGFuT3JFcXVhbAAAAAAAAAABAgAAeHIAMWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBs
                LmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlczIAAAAAAAAAAQIAAHhw\
                """,
                Predicates2.<Integer>greaterThanOrEqualTo());
    }

    @Test
    public void equal()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEVxdWFsAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9y
                eS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.equal());
    }

    @Test
    public void notEqual()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdEVxdWFsAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFj
                dG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.notEqual());
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEFuZAAAAAAAAAABAgACTAAEbGVmdHQAM0xjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3By
                ZWRpY2F0ZS9QcmVkaWNhdGUyO0wABXJpZ2h0cQB+AAF4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmlt
                cGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHBwcA==\
                """,
                Predicates2.and(null, null));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADRjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE9yAAAAAAAAAAECAAJMAARsZWZ0dAAzTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJl
                ZGljYXRlL1ByZWRpY2F0ZTI7TAAFcmlnaHRxAH4AAXhyADFjb20uZ3MuY29sbGVjdGlvbnMuaW1w
                bC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMyAAAAAAAAAAECAAB4cHBw\
                """,
                Predicates2.or(null, null));
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdAAAAAAAAAABAgABTAAJcHJlZGljYXRldAAzTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxv
                Y2svcHJlZGljYXRlL1ByZWRpY2F0ZTI7eHIAMWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2Nr
                LmZhY3RvcnkuUHJlZGljYXRlczIAAAAAAAAAAQIAAHhwcA==\
                """,
                Predicates2.not(null));
    }

    @Test
    public void in()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADRjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JEluAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5Q
                cmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.in());
    }

    @Test
    public void notIn()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdEluAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9y
                eS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.notIn());
    }

    @Test
    public void isNull()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JElzTnVsbAAAAAAAAAABAgAAeHIAMWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rv
                cnkuUHJlZGljYXRlczIAAAAAAAAAAQIAAHhw\
                """,
                Predicates2.isNull());
    }

    @Test
    public void notNull()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdE51bGwAAAAAAAAAAQIAAHhyADFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0
                b3J5LlByZWRpY2F0ZXMyAAAAAAAAAAECAAB4cA==\
                """,
                Predicates2.notNull());
    }

    @Test
    public void sameAs()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JElzSWRlbnRpY2FsAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2su
                ZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.sameAs());
    }

    @Test
    public void notSameAs()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMy
                JE5vdElkZW50aXRpY2FsAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxv
                Y2suZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates2.notSameAs());
    }
}
