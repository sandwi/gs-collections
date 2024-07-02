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

import java.util.Collections;

import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.factory.Sets;
import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class PredicatesSerializationTest
{
    @Test
    public void throwing()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                VGhyb3dpbmdQcmVkaWNhdGVBZGFwdGVyAAAAAAAAAAECAAFMABF0aHJvd2luZ1ByZWRpY2F0ZXQA
                Q0xjb20vZ3MvY29sbGVjdGlvbnMvaW1wbC9ibG9jay9wcmVkaWNhdGUvY2hlY2tlZC9UaHJvd2lu
                Z1ByZWRpY2F0ZTt4cgBAY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2sucHJlZGljYXRlLmNo
                ZWNrZWQuQ2hlY2tlZFByZWRpY2F0ZQAAAAAAAAABAgAAeHBw\
                """,
                Predicates.throwing(null));
    }

    @Test
    public void alwaysTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QWx3YXlzVHJ1ZQAAAAAAAAABAgAAeHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZh
                Y3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHA=\
                """,
                Predicates.alwaysTrue());
    }

    @Test
    public void alwaysFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QWx3YXlzRmFsc2UAAAAAAAAAAQIAAHhyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5m
                YWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhw\
                """,
                Predicates.alwaysFalse());
    }

    @Test
    public void adapt()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                UHJlZGljYXRlQWRhcHRlcgAAAAAAAAABAgABTAAJcHJlZGljYXRldAAyTGNvbS9ncy9jb2xsZWN0
                aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL1ByZWRpY2F0ZTt4cgAwY29tLmdzLmNvbGxlY3Rpb25z
                LmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHA=\
                """,
                Predicates.adapt(null));
    }

    @Test
    public void attributePredicate()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QXR0cmlidXRlUHJlZGljYXRlAAAAAAAAAAECAAJMAAhmdW5jdGlvbnQAMExjb20vZ3MvY29sbGVj
                dGlvbnMvYXBpL2Jsb2NrL2Z1bmN0aW9uL0Z1bmN0aW9uO0wACXByZWRpY2F0ZXQAMkxjb20vZ3Mv
                Y29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9QcmVkaWNhdGU7eHIAMGNvbS5ncy5jb2xs
                ZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBwcA==\
                """,
                Predicates.attributePredicate(null, null));
    }

    @Test
    public void ifTrue()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QXR0cmlidXRlVHJ1ZQAAAAAAAAABAgAAeHIAQ2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2Nr
                LmZhY3RvcnkuUHJlZGljYXRlcyRBdHRyaWJ1dGVQcmVkaWNhdGUAAAAAAAAAAQIAAkwACGZ1bmN0
                aW9udAAwTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svZnVuY3Rpb24vRnVuY3Rpb247TAAJ
                cHJlZGljYXRldAAyTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkvYmxvY2svcHJlZGljYXRlL1ByZWRp
                Y2F0ZTt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVz
                AAAAAAAAAAECAAB4cHBzcgA7Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5Q
                cmVkaWNhdGVzJFRydWVFcXVhbHMAAAAAAAAAAQIAAHhw\
                """,
                Predicates.ifTrue(null));
    }

    @Test
    public void ifFalse()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QXR0cmlidXRlRmFsc2UAAAAAAAAAAQIAAHhyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9j
                ay5mYWN0b3J5LlByZWRpY2F0ZXMkQXR0cmlidXRlUHJlZGljYXRlAAAAAAAAAAECAAJMAAhmdW5j
                dGlvbnQAMExjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL2Z1bmN0aW9uL0Z1bmN0aW9uO0wA
                CXByZWRpY2F0ZXQAMkxjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9QcmVk
                aWNhdGU7eHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRl
                cwAAAAAAAAABAgAAeHBwc3IAPGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rvcnku
                UHJlZGljYXRlcyRGYWxzZUVxdWFscwAAAAAAAAABAgAAeHA=\
                """,
                Predicates.ifFalse(null));
    }

    @Test
    public void anySatisfy()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QW55U2F0aXNmeQAAAAAAAAABAgABTAAJcHJlZGljYXRldAAyTGNvbS9ncy9jb2xsZWN0aW9ucy9h
                cGkvYmxvY2svcHJlZGljYXRlL1ByZWRpY2F0ZTt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwu
                YmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHA=\
                """,
                Predicates.anySatisfy(null));
    }

    @Test
    public void allSatisfy()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QWxsU2F0aXNmeQAAAAAAAAABAgABTAAJcHJlZGljYXRldAAyTGNvbS9ncy9jb2xsZWN0aW9ucy9h
                cGkvYmxvY2svcHJlZGljYXRlL1ByZWRpY2F0ZTt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwu
                YmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHA=\
                """,
                Predicates.allSatisfy(null));
    }

    @Test
    public void assignableFrom()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QXNzaWduYWJsZUZyb21QcmVkaWNhdGUAAAAAAAAAAQIAAUwABWNsYXp6dAARTGphdmEvbGFuZy9D
                bGFzczt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVz
                AAAAAAAAAAECAAB4cHZyABBqYXZhLmxhbmcuT2JqZWN0AAAAAAAAAAAAAAB4cA==\
                """,
                Predicates.assignableFrom(Object.class));
    }

    @Test
    public void instanceOf()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAERjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SW5zdGFuY2VPZlByZWRpY2F0ZQAAAAAAAAABAgABTAAFY2xhenp0ABFMamF2YS9sYW5nL0NsYXNz
                O3hyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAA
                AAAAAQIAAHhwdnIAEGphdmEubGFuZy5PYmplY3QAAAAAAAAAAAAAAHhw\
                """,
                Predicates.instanceOf(Object.class));
    }

    @Test
    public void notInstanceOf()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90SW5zdGFuY2VPZlByZWRpY2F0ZQAAAAAAAAABAgABTAAFY2xhenp0ABFMamF2YS9sYW5nL0Ns
                YXNzO3hyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMA
                AAAAAAAAAQIAAHhwdnIAEGphdmEubGFuZy5PYmplY3QAAAAAAAAAAAAAAHhw\
                """,
                Predicates.notInstanceOf(Object.class));
    }

    @Test
    public void lessThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                TGVzc1RoYW5QcmVkaWNhdGUAAAAAAAAAAQIAAHhyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5i
                bG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkQ29tcGFyZVRvUHJlZGljYXRlAAAAAAAAAAECAAFMAAlj
                b21wYXJlVG90ABZMamF2YS9sYW5nL0NvbXBhcmFibGU7eHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5p
                bXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBw\
                """,
                Predicates.lessThan((String) null));
    }

    @Test
    public void lessThanOrEqualTo()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                TGVzc1RoYW5PckVxdWFsUHJlZGljYXRlAAAAAAAAAAECAAB4cgBDY29tLmdzLmNvbGxlY3Rpb25z
                LmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzJENvbXBhcmVUb1ByZWRpY2F0ZQAAAAAAAAAB
                AgABTAAJY29tcGFyZVRvdAAWTGphdmEvbGFuZy9Db21wYXJhYmxlO3hyADBjb20uZ3MuY29sbGVj
                dGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwcA==\
                """,
                Predicates.lessThanOrEqualTo((String) null));
    }

    @Test
    public void greaterThan()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                R3JlYXRlclRoYW5QcmVkaWNhdGUAAAAAAAAAAQIAAHhyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1w
                bC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkQ29tcGFyZVRvUHJlZGljYXRlAAAAAAAAAAECAAFM
                AAljb21wYXJlVG90ABZMamF2YS9sYW5nL0NvbXBhcmFibGU7eHIAMGNvbS5ncy5jb2xsZWN0aW9u
                cy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBw\
                """,
                Predicates.greaterThan((String) null));
    }

    @Test
    public void greaterThanOrEqualTo()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAExjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                R3JlYXRlclRoYW5PckVxdWFsUHJlZGljYXRlAAAAAAAAAAECAAB4cgBDY29tLmdzLmNvbGxlY3Rp
                b25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzJENvbXBhcmVUb1ByZWRpY2F0ZQAAAAAA
                AAABAgABTAAJY29tcGFyZVRvdAAWTGphdmEvbGFuZy9Db21wYXJhYmxlO3hyADBjb20uZ3MuY29s
                bGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwcA==\
                """,
                Predicates.greaterThanOrEqualTo((String) null));
    }

    @Test
    public void equal()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                RXF1YWxQcmVkaWNhdGUAAAAAAAAAAQIAAUwADWNvbXBhcmVPYmplY3R0ABJMamF2YS9sYW5nL09i
                amVjdDt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVz
                AAAAAAAAAAECAAB4cHNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4cgAQ
                amF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAA\
                """,
                Predicates.equal(0));
    }

    @Test
    public void notEqual()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90RXF1YWxQcmVkaWNhdGUAAAAAAAAAAQIAAUwADWNvbXBhcmVPYmplY3R0ABJMamF2YS9sYW5n
                L09iamVjdDt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNh
                dGVzAAAAAAAAAAECAAB4cHNyABFqYXZhLmxhbmcuSW50ZWdlchLioKT3gYc4AgABSQAFdmFsdWV4
                cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHAAAAAA\
                """,
                Predicates.notEqual(0));
    }

    @Test
    public void betweenExclusive()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QmV0d2VlbkV4Y2x1c2l2ZQAAAAAAAAABAgAAeHIAP2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJs
                b2NrLmZhY3RvcnkuUHJlZGljYXRlcyRSYW5nZVByZWRpY2F0ZQAAAAAAAAABAgABTAALY29tcGFy
                ZUZyb210ABZMamF2YS9sYW5nL0NvbXBhcmFibGU7eHIAQ2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBs
                LmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcyRDb21wYXJlVG9QcmVkaWNhdGUAAAAAAAAAAQIAAUwA
                CWNvbXBhcmVUb3EAfgACeHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rvcnku
                UHJlZGljYXRlcwAAAAAAAAABAgAAeHB0AABxAH4ABg==\
                """,
                Predicates.betweenExclusive("", ""));
    }

    @Test
    public void betweenInclusive()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QmV0d2VlbkluY2x1c2l2ZQAAAAAAAAABAgAAeHIAP2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJs
                b2NrLmZhY3RvcnkuUHJlZGljYXRlcyRSYW5nZVByZWRpY2F0ZQAAAAAAAAABAgABTAALY29tcGFy
                ZUZyb210ABZMamF2YS9sYW5nL0NvbXBhcmFibGU7eHIAQ2NvbS5ncy5jb2xsZWN0aW9ucy5pbXBs
                LmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcyRDb21wYXJlVG9QcmVkaWNhdGUAAAAAAAAAAQIAAUwA
                CWNvbXBhcmVUb3EAfgACeHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rvcnku
                UHJlZGljYXRlcwAAAAAAAAABAgAAeHB0AABxAH4ABg==\
                """,
                Predicates.betweenInclusive("", ""));
    }

    @Test
    public void betweenInclusiveFrom()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QmV0d2VlbkluY2x1c2l2ZUZyb20AAAAAAAAAAQIAAHhyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1w
                bC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkUmFuZ2VQcmVkaWNhdGUAAAAAAAAAAQIAAUwAC2Nv
                bXBhcmVGcm9tdAAWTGphdmEvbGFuZy9Db21wYXJhYmxlO3hyAENjb20uZ3MuY29sbGVjdGlvbnMu
                aW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkQ29tcGFyZVRvUHJlZGljYXRlAAAAAAAAAAEC
                AAFMAAljb21wYXJlVG9xAH4AAnhyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0
                b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwdAAAcQB+AAY=\
                """,
                Predicates.betweenInclusiveFrom("", ""));
    }

    @Test
    public void betweenInclusiveTo()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAENjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QmV0d2VlbkluY2x1c2l2ZVRvAAAAAAAAAAECAAB4cgA/Y29tLmdzLmNvbGxlY3Rpb25zLmltcGwu
                YmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzJFJhbmdlUHJlZGljYXRlAAAAAAAAAAECAAFMAAtjb21w
                YXJlRnJvbXQAFkxqYXZhL2xhbmcvQ29tcGFyYWJsZTt4cgBDY29tLmdzLmNvbGxlY3Rpb25zLmlt
                cGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzJENvbXBhcmVUb1ByZWRpY2F0ZQAAAAAAAAABAgAB
                TAAJY29tcGFyZVRvcQB+AAJ4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9y
                eS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHQAAHEAfgAG\
                """,
                Predicates.betweenInclusiveTo("", ""));
    }

    @Test
    public void and()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QW5kUHJlZGljYXRlAAAAAAAAAAECAAJMAARsZWZ0dAAyTGNvbS9ncy9jb2xsZWN0aW9ucy9hcGkv
                YmxvY2svcHJlZGljYXRlL1ByZWRpY2F0ZTtMAAVyaWdodHEAfgABeHIAMGNvbS5ncy5jb2xsZWN0
                aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBwcA==\
                """,
                Predicates.and(null, null));
    }

    @Test
    public void andCollection()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QW5kSXRlcmFibGVQcmVkaWNhdGUAAAAAAAAAAQIAAHhyAEpjb20uZ3MuY29sbGVjdGlvbnMuaW1w
                bC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkQWJzdHJhY3RJdGVyYWJsZVByZWRpY2F0ZQAAAAAA
                AAABAgABTAAKcHJlZGljYXRlc3QAFExqYXZhL2xhbmcvSXRlcmFibGU7eHIAMGNvbS5ncy5jb2xs
                ZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBzcgAaamF2
                YS51dGlsLkFycmF5cyRBcnJheUxpc3TZpDy+zYgG0gIAAVsAAWF0ABNbTGphdmEvbGFuZy9PYmpl
                Y3Q7eHB1cgAzW0xjb20uZ3MuY29sbGVjdGlvbnMuYXBpLmJsb2NrLnByZWRpY2F0ZS5QcmVkaWNh
                dGU7Q7YSGUSRPkwCAAB4cAAAAANwcHA=\
                """,
                Predicates.and(null, null, null));
    }

    @Test
    public void or()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                T3JQcmVkaWNhdGUAAAAAAAAAAQIAAkwABGxlZnR0ADJMY29tL2dzL2NvbGxlY3Rpb25zL2FwaS9i
                bG9jay9wcmVkaWNhdGUvUHJlZGljYXRlO0wABXJpZ2h0cQB+AAF4cgAwY29tLmdzLmNvbGxlY3Rp
                b25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHBw\
                """,
                Predicates.or(null, null));
    }

    @Test
    public void orCollection()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAERjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                T3JJdGVyYWJsZVByZWRpY2F0ZQAAAAAAAAABAgAAeHIASmNvbS5ncy5jb2xsZWN0aW9ucy5pbXBs
                LmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcyRBYnN0cmFjdEl0ZXJhYmxlUHJlZGljYXRlAAAAAAAA
                AAECAAFMAApwcmVkaWNhdGVzdAAUTGphdmEvbGFuZy9JdGVyYWJsZTt4cgAwY29tLmdzLmNvbGxl
                Y3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cHNyABpqYXZh
                LnV0aWwuQXJyYXlzJEFycmF5TGlzdNmkPL7NiAbSAgABWwABYXQAE1tMamF2YS9sYW5nL09iamVj
                dDt4cHVyADNbTGNvbS5ncy5jb2xsZWN0aW9ucy5hcGkuYmxvY2sucHJlZGljYXRlLlByZWRpY2F0
                ZTtDthIZRJE+TAIAAHhwAAAAA3BwcA==\
                """,
                Predicates.or(null, null, null));
    }

    @Test
    public void neither()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEFjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                TmVpdGhlclByZWRpY2F0ZQAAAAAAAAABAgACTAAEbGVmdHQAMkxjb20vZ3MvY29sbGVjdGlvbnMv
                YXBpL2Jsb2NrL3ByZWRpY2F0ZS9QcmVkaWNhdGU7TAAFcmlnaHRxAH4AAXhyADBjb20uZ3MuY29s
                bGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwcHA=\
                """,
                Predicates.neither(null, null));
    }

    @Test
    public void noneOf()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm9uZU9mSXRlcmFibGVQcmVkaWNhdGUAAAAAAAAAAQIAAHhyAEpjb20uZ3MuY29sbGVjdGlvbnMu
                aW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMkQWJzdHJhY3RJdGVyYWJsZVByZWRpY2F0ZQAA
                AAAAAAABAgABTAAKcHJlZGljYXRlc3QAFExqYXZhL2xhbmcvSXRlcmFibGU7eHIAMGNvbS5ncy5j
                b2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBzcgAa
                amF2YS51dGlsLkFycmF5cyRBcnJheUxpc3TZpDy+zYgG0gIAAVsAAWF0ABNbTGphdmEvbGFuZy9P
                YmplY3Q7eHB1cgAzW0xjb20uZ3MuY29sbGVjdGlvbnMuYXBpLmJsb2NrLnByZWRpY2F0ZS5QcmVk
                aWNhdGU7Q7YSGUSRPkwCAAB4cAAAAANwcHA=\
                """,
                Predicates.noneOf(null, null, null));
    }

    @Test
    public void not()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90UHJlZGljYXRlAAAAAAAAAAECAAFMAAlwcmVkaWNhdGV0ADJMY29tL2dzL2NvbGxlY3Rpb25z
                L2FwaS9ibG9jay9wcmVkaWNhdGUvUHJlZGljYXRlO3hyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1w
                bC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwcA==\
                """,
                Predicates.not(null));
    }

    @Test
    public void in_SetIterable()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SW5TZXRJdGVyYWJsZVByZWRpY2F0ZQAAAAAAAAABAgABTAALc2V0SXRlcmFibGV0AChMY29tL2dz
                L2NvbGxlY3Rpb25zL2FwaS9zZXQvU2V0SXRlcmFibGU7eHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5p
                bXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBzcgBEY29tLmdzLmNvbGxl
                Y3Rpb25zLmltcGwuc2V0LmltbXV0YWJsZS5JbW11dGFibGVTZXRTZXJpYWxpemF0aW9uUHJveHkA
                AAAAAAAAAQwAAHhwdwQAAAAAeA==\
                """,
                Predicates.in(Sets.immutable.with()));
    }

    @Test
    public void notIn_SetIterable()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90SW5TZXRJdGVyYWJsZVByZWRpY2F0ZQAAAAAAAAABAgABTAALc2V0SXRlcmFibGV0AChMY29t
                L2dzL2NvbGxlY3Rpb25zL2FwaS9zZXQvU2V0SXRlcmFibGU7eHIAMGNvbS5ncy5jb2xsZWN0aW9u
                cy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHBzcgBEY29tLmdzLmNv
                bGxlY3Rpb25zLmltcGwuc2V0LmltbXV0YWJsZS5JbW11dGFibGVTZXRTZXJpYWxpemF0aW9uUHJv
                eHkAAAAAAAAAAQwAAHhwdwQAAAAAeA==\
                """,
                Predicates.notIn(Sets.immutable.with()));
    }

    @Test
    public void in_Set()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SW5TZXRQcmVkaWNhdGUAAAAAAAAAAQIAAUwAA3NldHQAD0xqYXZhL3V0aWwvU2V0O3hyADBjb20u
                Z3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhw
                c3IAIGphdmEudXRpbC5Db2xsZWN0aW9ucyRDaGVja2VkU2V0QSSbonrZ/6sCAAB4cgAnamF2YS51
                dGlsLkNvbGxlY3Rpb25zJENoZWNrZWRDb2xsZWN0aW9uFelt/RjmzG8CAANMAAFjdAAWTGphdmEv
                dXRpbC9Db2xsZWN0aW9uO0wABHR5cGV0ABFMamF2YS9sYW5nL0NsYXNzO1sAFnplcm9MZW5ndGhF
                bGVtZW50QXJyYXl0ABNbTGphdmEvbGFuZy9PYmplY3Q7eHBzcgAuY29tLmdzLmNvbGxlY3Rpb25z
                LmltcGwuc2V0Lm11dGFibGUuVW5pZmllZFNldAAAAAAAAAABDAAAeHB3CAAAAAA/QAAAeHZyABBq
                YXZhLmxhbmcuT2JqZWN0AAAAAAAAAAAAAAB4cHA=\
                """,
                Predicates.in(Collections.checkedSet(Sets.mutable.with(), Object.class)));
    }

    @Test
    public void notIn_Set()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90SW5TZXRQcmVkaWNhdGUAAAAAAAAAAQIAAUwAA3NldHQAD0xqYXZhL3V0aWwvU2V0O3hyADBj
                b20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMAAAAAAAAAAQIA
                AHhwc3IAIGphdmEudXRpbC5Db2xsZWN0aW9ucyRDaGVja2VkU2V0QSSbonrZ/6sCAAB4cgAnamF2
                YS51dGlsLkNvbGxlY3Rpb25zJENoZWNrZWRDb2xsZWN0aW9uFelt/RjmzG8CAANMAAFjdAAWTGph
                dmEvdXRpbC9Db2xsZWN0aW9uO0wABHR5cGV0ABFMamF2YS9sYW5nL0NsYXNzO1sAFnplcm9MZW5n
                dGhFbGVtZW50QXJyYXl0ABNbTGphdmEvbGFuZy9PYmplY3Q7eHBzcgAuY29tLmdzLmNvbGxlY3Rp
                b25zLmltcGwuc2V0Lm11dGFibGUuVW5pZmllZFNldAAAAAAAAAABDAAAeHB3CAAAAAA/QAAAeHZy
                ABBqYXZhLmxhbmcuT2JqZWN0AAAAAAAAAAAAAAB4cHA=\
                """,
                Predicates.notIn(Collections.checkedSet(Sets.mutable.with(), Object.class)));
    }

    @Test
    public void in_small_Collection()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SW5Db2xsZWN0aW9uUHJlZGljYXRlAAAAAAAAAAECAAFMAApjb2xsZWN0aW9udAAWTGphdmEvdXRp
                bC9Db2xsZWN0aW9uO3hyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlBy
                ZWRpY2F0ZXMAAAAAAAAAAQIAAHhwc3IALWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmxpc3QubXV0
                YWJsZS5GYXN0TGlzdAAAAAAAAAABDAAAeHB3BAAAAAB4\
                """,
                Predicates.in(Lists.mutable.with()));
    }

    @Test
    public void notIn_small_Collection()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90SW5Db2xsZWN0aW9uUHJlZGljYXRlAAAAAAAAAAECAAFMAApjb2xsZWN0aW9udAAWTGphdmEv
                dXRpbC9Db2xsZWN0aW9uO3hyADBjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5
                LlByZWRpY2F0ZXMAAAAAAAAAAQIAAHhwc3IALWNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmxpc3Qu
                bXV0YWJsZS5GYXN0TGlzdAAAAAAAAAABDAAAeHB3BAAAAAB4\
                """,
                Predicates.notIn(Lists.mutable.with()));
    }

    @Test
    public void isNull()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SXNOdWxsAAAAAAAAAAECAAB4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9y
                eS5QcmVkaWNhdGVzAAAAAAAAAAECAAB4cA==\
                """,
                Predicates.isNull());
    }

    @Test
    public void notNull()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90TnVsbAAAAAAAAAABAgAAeHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3Rv
                cnkuUHJlZGljYXRlcwAAAAAAAAABAgAAeHA=\
                """,
                Predicates.notNull());
    }

    @Test
    public void sameAs()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                SWRlbnRpdHlQcmVkaWNhdGUAAAAAAAAAAQIAAUwABHR3aW50ABJMamF2YS9sYW5nL09iamVjdDt4
                cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAAAAAA
                AAECAAB4cHA=\
                """,
                Predicates.sameAs(null));
    }

    @Test
    public void notSameAs()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEVjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                Tm90SWRlbnRpdHlQcmVkaWNhdGUAAAAAAAAAAQIAAUwABHR3aW50ABJMamF2YS9sYW5nL09iamVj
                dDt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAA
                AAAAAAECAAB4cHA=\
                """,
                Predicates.notSameAs(null));
    }

    @Test
    public void synchronizedEach()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                U3luY2hyb25pemVkUHJlZGljYXRlAAAAAAAAAAECAAFMAAlwcmVkaWNhdGV0ADJMY29tL2dzL2Nv
                bGxlY3Rpb25zL2FwaS9ibG9jay9wcmVkaWNhdGUvUHJlZGljYXRlO3hwcA==\
                """,
                Predicates.synchronizedEach(null));
    }

    @Test
    public void subClass()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAEJjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                U3ViY2xhc3NQcmVkaWNhdGUAAAAAAAAAAQIAAUwABmFDbGFzc3QAEUxqYXZhL2xhbmcvQ2xhc3M7
                eHIAMGNvbS5ncy5jb2xsZWN0aW9ucy5pbXBsLmJsb2NrLmZhY3RvcnkuUHJlZGljYXRlcwAAAAAA
                AAABAgAAeHB2cgAQamF2YS5sYW5nLk9iamVjdAAAAAAAAAAAAAAAeHA=\
                """,
                Predicates.subClass(Object.class));
    }

    @Test
    public void superClass()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAERjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                U3VwZXJjbGFzc1ByZWRpY2F0ZQAAAAAAAAABAgABTAAGYUNsYXNzdAARTGphdmEvbGFuZy9DbGFz
                czt4cgAwY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxvY2suZmFjdG9yeS5QcmVkaWNhdGVzAAAA
                AAAAAAECAAB4cHZyABBqYXZhLmxhbmcuT2JqZWN0AAAAAAAAAAAAAAB4cA==\
                """,
                Predicates.superClass(Object.class));
    }

    @Test
    public void bind()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD9jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlByZWRpY2F0ZXMk
                QmluZFByZWRpY2F0ZTIAAAAAAAAAAQIAAkwACXBhcmFtZXRlcnQAEkxqYXZhL2xhbmcvT2JqZWN0
                O0wACXByZWRpY2F0ZXQAM0xjb20vZ3MvY29sbGVjdGlvbnMvYXBpL2Jsb2NrL3ByZWRpY2F0ZS9Q
                cmVkaWNhdGUyO3hwcHNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5ibG9jay5mYWN0b3J5LlBy
                ZWRpY2F0ZXMyJEVxdWFsAAAAAAAAAAECAAB4cgAxY29tLmdzLmNvbGxlY3Rpb25zLmltcGwuYmxv
                Y2suZmFjdG9yeS5QcmVkaWNhdGVzMgAAAAAAAAABAgAAeHA=\
                """,
                Predicates.bind(Predicates2.equal(), null));
    }
}

