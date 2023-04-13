package com.typedb.examples.fraud.web;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.typedb.examples.fraud.db.StandardDao;
import com.typedb.examples.fraud.db.TransactionDao;
import com.typedb.examples.fraud.model.Bank;
import com.typedb.examples.fraud.model.BankCoordinates;
import com.typedb.examples.fraud.model.Cardholder;
import com.typedb.examples.fraud.model.Merchant;
import com.typedb.examples.fraud.model.Transaction;
import com.vaticle.typedb.client.api.TypeDBClient;
import com.vaticle.typedb.client.api.TypeDBSession;
import com.vaticle.typedb.client.api.TypeDBTransaction;
import com.vaticle.typeql.lang.TypeQL;
import io.quarkus.runtime.StartupEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

  private static final Logger LOGGER = Logger.getLogger("AppLifecycleBean");

  @Inject
  TypeDBClient client;

  @Inject
  StandardDao<Bank> banks;
  @Inject
  StandardDao<Cardholder> cardholders;
  @Inject
  StandardDao<Merchant> merchants;
  @Inject
  TransactionDao transactions;

  void onStart(@Observes StartupEvent ev) {

    LOGGER.info("Deleting database");

    client.databases().get("fraud").delete();

    LOGGER.info("Creating database");

    client.databases().create("fraud");

    LOGGER.info("Creating schema");

    try (TypeDBSession session = client.session("fraud", TypeDBSession.Type.SCHEMA)) {

      try (TypeDBTransaction tx = session.transaction(TypeDBTransaction.Type.WRITE)) {

        URL file = this.getClass().getClassLoader().getResource("schema.tql");

        assert file != null;

        String query = Files.readString(Paths.get(file.toURI()));

        tx.query().define(TypeQL.parseQuery(query).asDefine());

        tx.commit();
      }
      catch (Exception ex) {

        ex.printStackTrace(System.err);
      }
    }

    LOGGER.info("Preparing data");

    var sampleBanks = new HashSet<Bank>();
    var sampleCardholders = new HashSet<Cardholder>();
    var sampleMerchants = new HashSet<Merchant>();
    var sampleTransactions = new HashSet<Transaction>();

    sampleBanks.add(new Bank("ABC", new BankCoordinates("30.5", "-90.3")));
    sampleBanks.add(new Bank("MNO", new BankCoordinates("33.986391", "-81.200714")));
    sampleBanks.add(new Bank("QRS", new BankCoordinates("43.7", "-88.2")));
    sampleBanks.add(new Bank("XYZ", new BankCoordinates("40.98", "-90.4")));

    try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("data.csv")) {

      assert is != null;

      try (InputStreamReader isr = new InputStreamReader(is)) {

        CsvToBean<Transaction> csv = new CsvToBeanBuilder<Transaction>(isr).withType(Transaction.class).build();

        sampleTransactions.addAll(csv.parse());

        sampleTransactions.forEach(tx -> {

          Bank bank = sampleBanks.stream().skip((int) (sampleBanks.size() * Math.random())).findFirst().get();

          tx.getCardholder().getCc().setBank(bank);
        });

        sampleMerchants.addAll(sampleTransactions.stream().map(Transaction::getMerchant).collect(Collectors.toSet()));
        sampleCardholders.addAll(sampleTransactions.stream().map(Transaction::getCardholder).collect(Collectors.toSet()));
      }
    }
    catch (Exception ex) {
      LOGGER.error(ex);
    }

    LOGGER.info("Inserting data");

    banks.insertAll(sampleBanks);
    cardholders.insertAll(sampleCardholders);
    merchants.insertAll(sampleMerchants);
    transactions.insertAll(sampleTransactions);
  }
}
