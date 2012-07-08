/**
 * Copyright (C) 2012 skymobi LTD
 *
 * Licensed under GNU GENERAL PUBLIC LICENSE  Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.skymobi.monitor.security;

import junit.framework.TestCase;

/**
 * @author Hill.Hu
 */
public class SimpleAuthzTest extends TestCase {
    SimpleAuthz authz = new SimpleAuthz();

    @Override
    public void setUp() throws Exception {

    }

    public void test_grant() throws Exception {
        assertTrue(authz.noneGranted("ROLE_ADMIN"));
    }
}
