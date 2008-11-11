package ch.qos.logback.access.db;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.access.dummy.DummyRequest;
import ch.qos.logback.access.dummy.DummyResponse;
import ch.qos.logback.access.dummy.DummyServerAdapter;
import ch.qos.logback.access.joran.JoranConfigurator;
import ch.qos.logback.access.spi.AccessContext;
import ch.qos.logback.access.spi.AccessEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.testUtil.Env;
import ch.qos.logback.core.util.StatusPrinter;

public class DBAppenderIntegrationTest {

  static String LOCAL_HOST_NAME;
  static String[] CONFORMING_HOST_LIST = new String[] { "Orion" };

  int diff = new Random(System.nanoTime()).nextInt(10000);
  AccessContext context = new AccessContext();
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    InetAddress localhostIA = InetAddress.getLocalHost();
    LOCAL_HOST_NAME = localhostIA.getHostName();
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {

  }

  public void doTest(String configFile) throws JoranException {
    JoranConfigurator configurator = new JoranConfigurator();
    configurator.setContext(context);
    configurator.doConfigure(configFile);

    Appender<AccessEvent> appender = context.getAppender("DB");
    
    for (int i = 0; i < 10; i++) {
      AccessEvent event = createAccessEvent();
      appender.doAppend(event);
    }
    
    StatusPrinter.print(context);
    
    // check that there were no errors
    assertEquals(Status.INFO,  context.getStatusManager().getLevel());
    
  }
  
  static boolean isConformingHostAndJDK16OrHigher() {
    if(!Env.isJDK6OrHigher()) {
      return false;
    }
    for (String conformingHost : CONFORMING_HOST_LIST) {
      if (conformingHost.equalsIgnoreCase(LOCAL_HOST_NAME)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void sqlserver() throws Exception {
    // perform test only on conforming hosts
    if (!isConformingHostAndJDK16OrHigher()) {
      return;
    }
    doTest("src/test/input/integration/db/sqlserver-with-driver.xml");
  }

  @Test
  public void oracle10g() throws Exception {
    // perform test only on conforming hosts
    if (!isConformingHostAndJDK16OrHigher()) {
      return;
    }
    doTest("src/test/input/integration/db/oracle10g-with-driver.xml");
  }

  @Test
  @Ignore
  public void oracle11g() throws Exception {
    // perform test only on conforming hosts
    if (!isConformingHostAndJDK16OrHigher()) {
      return;
    }
    doTest("src/test/input/integration/db/oracle11g-with-driver.xml");
  }
  
  @Test
  public void mysql() throws Exception {
    // perform test only on conforming hosts
    if (!isConformingHostAndJDK16OrHigher()) {
      return;
    }
    doTest("src/test/input/integration/db/mysql-with-driver.xml");
  }
  
  @Test
  public void postgres() throws Exception {
    // perform test only on conforming hosts
    if (!isConformingHostAndJDK16OrHigher()) {
      return;
    }
    doTest("src/test/input/integration/db/postgresql-with-driver.xml");
  }
  
  private AccessEvent createAccessEvent() {
    DummyRequest request = new DummyRequest();
    DummyResponse response = new DummyResponse();
    DummyServerAdapter adapter = new DummyServerAdapter(request, response);

    AccessEvent ae = new AccessEvent(request, response, adapter);
    return ae;
  }
  
}