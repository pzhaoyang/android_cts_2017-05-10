/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.compatibility.common.tradefed.command;

import com.android.compatibility.common.tradefed.build.CompatibilityBuildProviderTest;
import com.android.compatibility.tradefed.command.MockConsole;

import junit.framework.TestCase;

public class CompatibilityConsoleTest extends TestCase {

    // Make sure the mock is in the ClassLoader
    @SuppressWarnings("unused")
    private MockConsole mMockConsole;

    @Override
    public void setUp() throws Exception {
        CompatibilityBuildProviderTest.setProperty("/tmp/foobar");
        mMockConsole = new MockConsole();
    }

    @Override
    public void tearDown() throws Exception {
        CompatibilityBuildProviderTest.setProperty(null);
        mMockConsole = null;
    }

    public void testHelpExists() throws Exception {
        CompatibilityConsole console = new CompatibilityConsole() {};
        assertFalse("No help", console.getGenericHelpString(null).isEmpty());
    }

    public void testPromptExists() throws Exception {
        CompatibilityConsole console = new CompatibilityConsole() {};
        assertFalse("No prompt", console.getConsolePrompt().isEmpty());
    }
}
