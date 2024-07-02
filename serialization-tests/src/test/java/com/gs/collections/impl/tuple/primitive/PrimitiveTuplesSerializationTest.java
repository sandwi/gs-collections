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

package com.gs.collections.impl.tuple.primitive;

import com.gs.collections.impl.test.Verify;
import org.junit.jupiter.api.Test;

public class PrimitiveTuplesSerializationTest
{
    @Test
    public void byteObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZU9iamVj
                dFBhaXJJbXBsAAAAAAAAAAECAAJCAANvbmVMAAN0d290ABJMamF2YS9sYW5nL09iamVjdDt4cABw
                """,
                PrimitiveTuples.pair((byte) 0, null));
    }

    @Test
    public void booleanObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbk9i
                amVjdFBhaXJJbXBsAAAAAAAAAAECAAJaAANvbmVMAAN0d290ABJMamF2YS9sYW5nL09iamVjdDt4
                cABw\
                """,
                PrimitiveTuples.pair(false, null));
    }

    @Test
    public void charObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhck9iamVj
                dFBhaXJJbXBsAAAAAAAAAAECAAJDAANvbmVMAAN0d290ABJMamF2YS9sYW5nL09iamVjdDt4cABh
                cA==\
                """,
                PrimitiveTuples.pair('a', null));
    }

    @Test
    public void doubleObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlT2Jq
                ZWN0UGFpckltcGwAAAAAAAAAAQIAAkQAA29uZUwAA3R3b3QAEkxqYXZhL2xhbmcvT2JqZWN0O3hw
                AAAAAAAAAABw\
                """,
                PrimitiveTuples.pair(0.0, null));
    }

    @Test
    public void floatObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRPYmpl
                Y3RQYWlySW1wbAAAAAAAAAABAgACRgADb25lTAADdHdvdAASTGphdmEvbGFuZy9PYmplY3Q7eHAA
                AAAAcA==\
                """,
                PrimitiveTuples.pair(0.0f, null));
    }

    @Test
    public void intObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50T2JqZWN0
                UGFpckltcGwAAAAAAAAAAQIAAkkAA29uZUwAA3R3b3QAEkxqYXZhL2xhbmcvT2JqZWN0O3hwAAAA
                AHA=\
                """,
                PrimitiveTuples.pair(0, null));
    }

    @Test
    public void longObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ09iamVj
                dFBhaXJJbXBsAAAAAAAAAAECAAJKAANvbmVMAAN0d290ABJMamF2YS9sYW5nL09iamVjdDt4cAAA
                AAAAAAAAcA==\
                """,
                PrimitiveTuples.pair(0L, null));
    }

    @Test
    public void shortObjectPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRPYmpl
                Y3RQYWlySW1wbAAAAAAAAAABAgACUwADb25lTAADdHdvdAASTGphdmEvbGFuZy9PYmplY3Q7eHAA
                AHA=\
                """,
                PrimitiveTuples.pair((short) 0, null));
    }

    @Test
    public void objectBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0Qnl0
                ZVBhaXJJbXBsAAAAAAAAAAECAAJCAAN0d29MAANvbmV0ABJMamF2YS9sYW5nL09iamVjdDt4cABw
                """,
                PrimitiveTuples.pair(null, (byte) 0));
    }

    @Test
    public void objectBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0Qm9v
                bGVhblBhaXJJbXBsAAAAAAAAAAECAAJaAAN0d29MAANvbmV0ABJMamF2YS9sYW5nL09iamVjdDt4
                cABw\
                """,
                PrimitiveTuples.pair(null, false));
    }

    @Test
    public void objectCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0Q2hh
                clBhaXJJbXBsAAAAAAAAAAECAAJDAAN0d29MAANvbmV0ABJMamF2YS9sYW5nL09iamVjdDt4cABh
                cA==\
                """,
                PrimitiveTuples.pair(null, 'a'));
    }

    @Test
    public void objectDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0RG91
                YmxlUGFpckltcGwAAAAAAAAAAQIAAkQAA3R3b0wAA29uZXQAEkxqYXZhL2xhbmcvT2JqZWN0O3hw
                AAAAAAAAAABw\
                """,
                PrimitiveTuples.pair(null, 0.0));
    }

    @Test
    public void objectFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0Rmxv
                YXRQYWlySW1wbAAAAAAAAAABAgACRgADdHdvTAADb25ldAASTGphdmEvbGFuZy9PYmplY3Q7eHAA
                AAAAcA==\
                """,
                PrimitiveTuples.pair(null, 0.0f));
    }

    @Test
    public void objectIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0SW50
                UGFpckltcGwAAAAAAAAAAQIAAkkAA3R3b0wAA29uZXQAEkxqYXZhL2xhbmcvT2JqZWN0O3hwAAAA
                AHA=\
                """,
                PrimitiveTuples.pair(null, 0));
    }

    @Test
    public void objectLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0TG9u
                Z1BhaXJJbXBsAAAAAAAAAAECAAJKAAN0d29MAANvbmV0ABJMamF2YS9sYW5nL09iamVjdDt4cAAA
                AAAAAAAAcA==\
                """,
                PrimitiveTuples.pair(null, 0L));
    }

    @Test
    public void objectShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuT2JqZWN0U2hv
                cnRQYWlySW1wbAAAAAAAAAABAgACUwADdHdvTAADb25ldAASTGphdmEvbGFuZy9PYmplY3Q7eHAA
                AHA=\
                """,
                PrimitiveTuples.pair(null, (short) 0));
    }

    @Test
    public void intIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADZjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50SW50UGFp
                ckltcGwAAAAAAAAAAQIAAkkAA29uZUkAA3R3b3hwAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0, 0));
    }

    @Test
    public void intFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50RmxvYXRQ
                YWlySW1wbAAAAAAAAAABAgACSQADb25lRgADdHdveHAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0, 0.0f));
    }

    @Test
    public void intDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50RG91Ymxl
                UGFpckltcGwAAAAAAAAAAQIAAkkAA29uZUQAA3R3b3hwAAAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0, 0.0));
    }

    @Test
    public void intLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50TG9uZ1Bh
                aXJJbXBsAAAAAAAAAAECAAJJAANvbmVKAAN0d294cAAAAAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0, 0L));
    }

    @Test
    public void intShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50U2hvcnRQ
                YWlySW1wbAAAAAAAAAABAgACSQADb25lUwADdHdveHAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0, (short) 0));
    }

    @Test
    public void intBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50Qnl0ZVBh
                aXJJbXBsAAAAAAAAAAECAAJJAANvbmVCAAN0d294cAAAAAAA\
                """,
                PrimitiveTuples.pair(0, (byte) 0));
    }

    @Test
    public void intCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50Q2hhclBh
                aXJJbXBsAAAAAAAAAAECAAJJAANvbmVDAAN0d294cAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0, '\0'));
    }

    @Test
    public void intBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuSW50Qm9vbGVh
                blBhaXJJbXBsAAAAAAAAAAECAAJJAANvbmVaAAN0d294cAAAAAAA\
                """,
                PrimitiveTuples.pair(0, false));
    }

    @Test
    public void floatIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRJbnRQ
                YWlySW1wbAAAAAAAAAABAgACRgADb25lSQADdHdveHAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0.0f, 0));
    }

    @Test
    public void floatFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRGbG9h
                dFBhaXJJbXBsAAAAAAAAAAECAAJGAANvbmVGAAN0d294cAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0.0f, 0.0f));
    }

    @Test
    public void floatDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXREb3Vi
                bGVQYWlySW1wbAAAAAAAAAABAgACRgADb25lRAADdHdveHAAAAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0f, 0.0));
    }

    @Test
    public void floatLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRMb25n
                UGFpckltcGwAAAAAAAAAAQIAAkYAA29uZUoAA3R3b3hwAAAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0.0f, 0L));
    }

    @Test
    public void floatShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRTaG9y
                dFBhaXJJbXBsAAAAAAAAAAECAAJGAANvbmVTAAN0d294cAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0.0f, (short) 0));
    }

    @Test
    public void floatBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRCeXRl
                UGFpckltcGwAAAAAAAAAAQIAAkYAA29uZUIAA3R3b3hwAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0f, (byte) 0));
    }

    @Test
    public void floatCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRDaGFy
                UGFpckltcGwAAAAAAAAAAQIAAkYAA29uZUMAA3R3b3hwAAAAAAAA\
                """,
                PrimitiveTuples.pair(0.0f, '\0'));
    }

    @Test
    public void floatBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRmxvYXRCb29s
                ZWFuUGFpckltcGwAAAAAAAAAAQIAAkYAA29uZVoAA3R3b3hwAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0f, false));
    }

    @Test
    public void doubleIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlSW50
                UGFpckltcGwAAAAAAAAAAQIAAkQAA29uZUkAA3R3b3hwAAAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0.0, 0));
    }

    @Test
    public void doubleFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlRmxv
                YXRQYWlySW1wbAAAAAAAAAABAgACRAADb25lRgADdHdveHAAAAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0, 0.0f));
    }

    @Test
    public void doubleDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlRG91
                YmxlUGFpckltcGwAAAAAAAAAAQIAAkQAA29uZUQAA3R3b3hwAAAAAAAAAAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0.0, 0.0));
    }

    @Test
    public void doubleLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlTG9u
                Z1BhaXJJbXBsAAAAAAAAAAECAAJEAANvbmVKAAN0d294cAAAAAAAAAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0, 0L));
    }

    @Test
    public void doubleShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlU2hv
                cnRQYWlySW1wbAAAAAAAAAABAgACRAADb25lUwADdHdveHAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0.0, (short) 0));
    }

    @Test
    public void doubleBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlQnl0
                ZVBhaXJJbXBsAAAAAAAAAAECAAJEAANvbmVCAAN0d294cAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0.0, (byte) 0));
    }

    @Test
    public void doubleCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlQ2hh
                clBhaXJJbXBsAAAAAAAAAAECAAJEAANvbmVDAAN0d294cAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0.0, '\0'));
    }

    @Test
    public void doubleBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuRG91YmxlQm9v
                bGVhblBhaXJJbXBsAAAAAAAAAAECAAJEAANvbmVaAAN0d294cAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0.0, false));
    }

    @Test
    public void longIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0ludFBh
                aXJJbXBsAAAAAAAAAAECAAJKAANvbmVJAAN0d294cAAAAAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0L, 0));
    }

    @Test
    public void longFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0Zsb2F0
                UGFpckltcGwAAAAAAAAAAQIAAkoAA29uZUYAA3R3b3hwAAAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0L, 0.0f));
    }

    @Test
    public void longDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0RvdWJs
                ZVBhaXJJbXBsAAAAAAAAAAECAAJKAANvbmVEAAN0d294cAAAAAAAAAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0L, 0.0));
    }

    @Test
    public void longLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0xvbmdQ
                YWlySW1wbAAAAAAAAAABAgACSgADb25lSgADdHdveHAAAAAAAAAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0L, 0L));
    }

    @Test
    public void longShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ1Nob3J0
                UGFpckltcGwAAAAAAAAAAQIAAkoAA29uZVMAA3R3b3hwAAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(0L, (short) 0));
    }

    @Test
    public void longBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0J5dGVQ
                YWlySW1wbAAAAAAAAAABAgACSgADb25lQgADdHdveHAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0L, (byte) 0));
    }

    @Test
    public void longCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0NoYXJQ
                YWlySW1wbAAAAAAAAAABAgACSgADb25lQwADdHdveHAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair(0L, '\0'));
    }

    @Test
    public void longBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuTG9uZ0Jvb2xl
                YW5QYWlySW1wbAAAAAAAAAABAgACSgADb25lWgADdHdveHAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(0L, false));
    }

    @Test
    public void shortIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRJbnRQ
                YWlySW1wbAAAAAAAAAABAgACUwADb25lSQADdHdveHAAAAAAAAA=\
                """,
                PrimitiveTuples.pair((short) 0, 0));
    }

    @Test
    public void shortFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRGbG9h
                dFBhaXJJbXBsAAAAAAAAAAECAAJTAANvbmVGAAN0d294cAAAAAAAAA==\
                """,
                PrimitiveTuples.pair((short) 0, 0.0f));
    }

    @Test
    public void shortDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnREb3Vi
                bGVQYWlySW1wbAAAAAAAAAABAgACUwADb25lRAADdHdveHAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair((short) 0, 0.0));
    }

    @Test
    public void shortLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRMb25n
                UGFpckltcGwAAAAAAAAAAQIAAlMAA29uZUoAA3R3b3hwAAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair((short) 0, 0L));
    }

    @Test
    public void shortShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRTaG9y
                dFBhaXJJbXBsAAAAAAAAAAECAAJTAANvbmVTAAN0d294cAAAAAA=\
                """,
                PrimitiveTuples.pair((short) 0, (short) 0));
    }

    @Test
    public void shortBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRCeXRl
                UGFpckltcGwAAAAAAAAAAQIAAlMAA29uZUIAA3R3b3hwAAAA\
                """,
                PrimitiveTuples.pair((short) 0, (byte) 0));
    }

    @Test
    public void shortCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRDaGFy
                UGFpckltcGwAAAAAAAAAAQIAAlMAA29uZUMAA3R3b3hwAAAAAA==\
                """,
                PrimitiveTuples.pair((short) 0, '\0'));
    }

    @Test
    public void shortBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuU2hvcnRCb29s
                ZWFuUGFpckltcGwAAAAAAAAAAQIAAlMAA29uZVoAA3R3b3hwAAAA\
                """,
                PrimitiveTuples.pair((short) 0, false));
    }

    @Test
    public void byteIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUludFBh
                aXJJbXBsAAAAAAAAAAECAAJCAANvbmVJAAN0d294cAAAAAAA\
                """,
                PrimitiveTuples.pair((byte) 0, 0));
    }

    @Test
    public void byteFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUZsb2F0
                UGFpckltcGwAAAAAAAAAAQIAAkIAA29uZUYAA3R3b3hwAAAAAAA=\
                """,
                PrimitiveTuples.pair((byte) 0, 0.0f));
    }

    @Test
    public void byteDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZURvdWJs
                ZVBhaXJJbXBsAAAAAAAAAAECAAJCAANvbmVEAAN0d294cAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair((byte) 0, 0.0));
    }

    @Test
    public void byteLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUxvbmdQ
                YWlySW1wbAAAAAAAAAABAgACQgADb25lSgADdHdveHAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair((byte) 0, 0L));
    }

    @Test
    public void byteShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZVNob3J0
                UGFpckltcGwAAAAAAAAAAQIAAkIAA29uZVMAA3R3b3hwAAAA\
                """,
                PrimitiveTuples.pair((byte) 0, (short) 0));
    }

    @Test
    public void byteBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUJ5dGVQ
                YWlySW1wbAAAAAAAAAABAgACQgADb25lQgADdHdveHAAAA==\
                """,
                PrimitiveTuples.pair((byte) 0, (byte) 0));
    }

    @Test
    public void byteCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUNoYXJQ
                YWlySW1wbAAAAAAAAAABAgACQgADb25lQwADdHdveHAAAAA=\
                """,
                PrimitiveTuples.pair((byte) 0, '\0'));
    }

    @Test
    public void byteBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQnl0ZUJvb2xl
                YW5QYWlySW1wbAAAAAAAAAABAgACQgADb25lWgADdHdveHAAAA==\
                """,
                PrimitiveTuples.pair((byte) 0, false));
    }

    @Test
    public void charIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADdjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckludFBh
                aXJJbXBsAAAAAAAAAAECAAJDAANvbmVJAAN0d294cAAAAAAAAA==\
                """,
                PrimitiveTuples.pair('\0', 0));
    }

    @Test
    public void charFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckZsb2F0
                UGFpckltcGwAAAAAAAAAAQIAAkMAA29uZUYAA3R3b3hwAAAAAAAA\
                """,
                PrimitiveTuples.pair('\0', 0.0f));
    }

    @Test
    public void charDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckRvdWJs
                ZVBhaXJJbXBsAAAAAAAAAAECAAJDAANvbmVEAAN0d294cAAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair('\0', 0.0));
    }

    @Test
    public void charLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckxvbmdQ
                YWlySW1wbAAAAAAAAAABAgACQwADb25lSgADdHdveHAAAAAAAAAAAAAA\
                """,
                PrimitiveTuples.pair('\0', 0L));
    }

    @Test
    public void charShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADljb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhclNob3J0
                UGFpckltcGwAAAAAAAAAAQIAAkMAA29uZVMAA3R3b3hwAAAAAA==\
                """,
                PrimitiveTuples.pair('\0', (short) 0));
    }

    @Test
    public void charBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckJ5dGVQ
                YWlySW1wbAAAAAAAAAABAgACQwADb25lQgADdHdveHAAAAA=\
                """,
                PrimitiveTuples.pair('\0', (byte) 0));
    }

    @Test
    public void charCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADhjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckNoYXJQ
                YWlySW1wbAAAAAAAAAABAgACQwADb25lQwADdHdveHAAAAAA\
                """,
                PrimitiveTuples.pair('\0', '\0'));
    }

    @Test
    public void charBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQ2hhckJvb2xl
                YW5QYWlySW1wbAAAAAAAAAABAgACQwADb25lWgADdHdveHAAAAA=\
                """,
                PrimitiveTuples.pair('\0', false));
    }

    @Test
    public void booleanIntPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADpjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbklu
                dFBhaXJJbXBsAAAAAAAAAAECAAJaAANvbmVJAAN0d294cAAAAAAA\
                """,
                PrimitiveTuples.pair(false, 0));
    }

    @Test
    public void booleanFloatPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkZs
                b2F0UGFpckltcGwAAAAAAAAAAQIAAloAA29uZUYAA3R3b3hwAAAAAAA=\
                """,
                PrimitiveTuples.pair(false, 0.0f));
    }

    @Test
    public void booleanDoublePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD1jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkRv
                dWJsZVBhaXJJbXBsAAAAAAAAAAECAAJaAANvbmVEAAN0d294cAAAAAAAAAAAAA==\
                """,
                PrimitiveTuples.pair(false, 0.0));
    }

    @Test
    public void booleanLongPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkxv
                bmdQYWlySW1wbAAAAAAAAAABAgACWgADb25lSgADdHdveHAAAAAAAAAAAAA=\
                """,
                PrimitiveTuples.pair(false, 0L));
    }

    @Test
    public void booleanShortPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADxjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhblNo
                b3J0UGFpckltcGwAAAAAAAAAAQIAAloAA29uZVMAA3R3b3hwAAAA\
                """,
                PrimitiveTuples.pair(false, (short) 0));
    }

    @Test
    public void booleanBytePair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkJ5
                dGVQYWlySW1wbAAAAAAAAAABAgACWgADb25lQgADdHdveHAAAA==\
                """,
                PrimitiveTuples.pair(false, (byte) 0));
    }

    @Test
    public void booleanCharPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkNo
                YXJQYWlySW1wbAAAAAAAAAABAgACWgADb25lQwADdHdveHAAAAA=\
                """,
                PrimitiveTuples.pair(false, '\0'));
    }

    @Test
    public void booleanBooleanPair()
    {
        Verify.assertSerializedForm(
                1L,
                """
                rO0ABXNyAD5jb20uZ3MuY29sbGVjdGlvbnMuaW1wbC50dXBsZS5wcmltaXRpdmUuQm9vbGVhbkJv
                b2xlYW5QYWlySW1wbAAAAAAAAAABAgACWgADb25lWgADdHdveHAAAA==\
                """,
                PrimitiveTuples.pair(false, false));
    }
}
