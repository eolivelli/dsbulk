/*
 * Copyright DataStax Inc.
 *
 * This software can be used solely with DataStax Enterprise. Please consult the license at
 * http://www.datastax.com/terms/datastax-dse-driver-license-terms
 */
package com.datastax.dsbulk.engine.ccm;

import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_CERT_CHAIN_PATH;
import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_KEYSTORE_PASSWORD;
import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_KEYSTORE_PATH;
import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_PRIVATE_KEY_PATH;
import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_TRUSTSTORE_PASSWORD;
import static com.datastax.dsbulk.tests.ccm.DefaultCCMCluster.DEFAULT_CLIENT_TRUSTSTORE_PATH;
import static com.datastax.dsbulk.tests.utils.CsvUtils.CSV_RECORDS_UNIQUE;
import static com.datastax.dsbulk.tests.utils.CsvUtils.IP_BY_COUNTRY_MAPPING;
import static com.datastax.dsbulk.tests.utils.CsvUtils.SELECT_FROM_IP_BY_COUNTRY;
import static com.datastax.dsbulk.tests.utils.CsvUtils.createIpByCountryTable;
import static com.datastax.dsbulk.tests.utils.EndToEndUtils.deleteIfExists;
import static com.datastax.dsbulk.tests.utils.EndToEndUtils.validateOutputFiles;
import static java.nio.file.Files.createTempDirectory;
import static org.assertj.core.api.Assertions.assertThat;

import com.datastax.driver.core.Session;
import com.datastax.dsbulk.engine.Main;
import com.datastax.dsbulk.tests.ccm.annotations.CCMConfig;
import com.datastax.dsbulk.tests.ccm.annotations.CCMTest;
import com.datastax.dsbulk.tests.ccm.annotations.ClusterConfig;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Test;

@CCMTest
@CCMConfig(ssl = true)
public class SSLEncryptionCCMEndToEndIT extends AbstractCCMEndToEndIT {

  @Inject
  @ClusterConfig(ssl = true)
  private static Session session;

  @Test
  public void full_load_unload_jdk() throws Exception {
    createIpByCountryTable(session);

    List<String> args = new ArrayList<>();
    args.add("load");
    args.add("--connector.csv.url");
    args.add(CSV_RECORDS_UNIQUE.toExternalForm());
    args.add("--connector.csv.header");
    args.add("false");
    args.add("--schema.keyspace");
    args.add(session.getLoggedKeyspace());
    args.add("--schema.table");
    args.add("ip_by_country");
    args.add("--schema.mapping");
    args.add(IP_BY_COUNTRY_MAPPING);
    args.add("--driver.ssl.provider");
    args.add("JDK");
    args.add("--driver.ssl.keystore.path");
    args.add(getAbsoluteKeystorePath());
    args.add("--driver.ssl.keystore.password");
    args.add(DEFAULT_CLIENT_KEYSTORE_PASSWORD);
    args.add("--driver.ssl.truststore.path");
    args.add(getAbsoluteTrustStorePath());
    args.add("--driver.ssl.truststore.password");
    args.add(DEFAULT_CLIENT_TRUSTSTORE_PASSWORD);

    int status = new Main(addContactPointAndPort(args)).run();
    assertThat(status).isZero();
    validateResultSetSize(24, SELECT_FROM_IP_BY_COUNTRY, session);

    Path unloadDir = createTempDirectory("test");
    Path outputFile = unloadDir.resolve("output-000001.csv");
    deleteIfExists(unloadDir);

    args = new ArrayList<>();
    args.add("unload");
    args.add("--connector.csv.url");
    args.add(unloadDir.toString());
    args.add("--connector.csv.header");
    args.add("false");
    args.add("--connector.csv.maxConcurrentFiles");
    args.add("1");
    args.add("--schema.keyspace");
    args.add(session.getLoggedKeyspace());
    args.add("--schema.table");
    args.add("ip_by_country");
    args.add("--schema.mapping");
    args.add(IP_BY_COUNTRY_MAPPING);
    args.add("--driver.ssl.provider");
    args.add("JDK");
    args.add("--driver.ssl.keystore.path");
    args.add(getAbsoluteKeystorePath());
    args.add("--driver.ssl.keystore.password");
    args.add(DEFAULT_CLIENT_KEYSTORE_PASSWORD);
    args.add("--driver.ssl.truststore.path");
    args.add(getAbsoluteTrustStorePath());
    args.add("--driver.ssl.truststore.password");
    args.add(DEFAULT_CLIENT_TRUSTSTORE_PASSWORD);

    status = new Main(addContactPointAndPort(args)).run();
    assertThat(status).isZero();
    validateOutputFiles(24, outputFile);
  }

  @Test
  public void full_load_unload_openssl() throws Exception {
    createIpByCountryTable(session);

    List<String> args = new ArrayList<>();
    args.add("load");
    args.add("--connector.csv.url");
    args.add(CSV_RECORDS_UNIQUE.toExternalForm());
    args.add("--connector.csv.header");
    args.add("false");
    args.add("--schema.keyspace");
    args.add(session.getLoggedKeyspace());
    args.add("--schema.table");
    args.add("ip_by_country");
    args.add("--schema.mapping");
    args.add(IP_BY_COUNTRY_MAPPING);
    args.add("--driver.ssl.provider");
    args.add("OpenSSL");
    args.add("--driver.ssl.openssl.keyCertChain");
    args.add(getAbsoluteClientCertPath());
    args.add("--driver.ssl.openssl.privateKey");
    args.add(getAbsoluteClientKeyPath());
    args.add("--driver.ssl.truststore.path");
    args.add(getAbsoluteTrustStorePath());
    args.add("--driver.ssl.truststore.password");
    args.add(DEFAULT_CLIENT_TRUSTSTORE_PASSWORD);

    int status = new Main(addContactPointAndPort(args)).run();
    assertThat(status).isZero();
    validateResultSetSize(24, SELECT_FROM_IP_BY_COUNTRY, session);

    Path unloadDir = createTempDirectory("test");
    Path outputFile = unloadDir.resolve("output-000001.csv");
    deleteIfExists(unloadDir);

    args = new ArrayList<>();
    args.add("unload");
    args.add("--connector.csv.url");
    args.add(unloadDir.toString());
    args.add("--connector.csv.header");
    args.add("false");
    args.add("--connector.csv.maxConcurrentFiles");
    args.add("1");
    args.add("--schema.keyspace");
    args.add(session.getLoggedKeyspace());
    args.add("--schema.table");
    args.add("ip_by_country");
    args.add("--schema.mapping");
    args.add(IP_BY_COUNTRY_MAPPING);
    args.add("--driver.ssl.provider");
    args.add("OpenSSL");
    args.add("--driver.ssl.openssl.keyCertChain");
    args.add(getAbsoluteClientCertPath());
    args.add("--driver.ssl.openssl.privateKey");
    args.add(getAbsoluteClientKeyPath());
    args.add("--driver.ssl.truststore.path");
    args.add(getAbsoluteTrustStorePath());
    args.add("--driver.ssl.truststore.password");
    args.add(DEFAULT_CLIENT_TRUSTSTORE_PASSWORD);

    status = new Main(addContactPointAndPort(args)).run();
    assertThat(status).isZero();
    validateOutputFiles(24, outputFile);
  }

  private static String getAbsoluteKeystorePath() {
    return getAbsoluteKeystorePath(DEFAULT_CLIENT_KEYSTORE_PATH);
  }

  private static String getAbsoluteClientCertPath() {
    return getAbsoluteKeystorePath(DEFAULT_CLIENT_CERT_CHAIN_PATH);
  }

  private static String getAbsoluteClientKeyPath() {
    return getAbsoluteKeystorePath(DEFAULT_CLIENT_PRIVATE_KEY_PATH);
  }

  private static String getAbsoluteTrustStorePath() {
    return getAbsoluteKeystorePath(DEFAULT_CLIENT_TRUSTSTORE_PATH);
  }

  private static String getAbsoluteKeystorePath(String path) {
    URL resource = SSLEncryptionCCMEndToEndIT.class.getResource(path);
    return resource.getPath();
  }
}
