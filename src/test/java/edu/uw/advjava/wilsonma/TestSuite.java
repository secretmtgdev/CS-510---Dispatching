package edu.uw.advjava.wilsonma;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import edu.uw.pce.advjava.support.account.AccountDaoTest;

@SelectClasses({ AccountDaoTest.class })
@Suite
class TestSuite {
}
