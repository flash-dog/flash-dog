package org.flashdog.agent;

import junit.framework.TestCase;
import org.flashdog.mock.MockLogFile;

public class MockLogFileTest extends TestCase {
    MockLogFile mockLogFile=new MockLogFile();
    public void test_file() throws Exception {
        mockLogFile.init();


    }
}