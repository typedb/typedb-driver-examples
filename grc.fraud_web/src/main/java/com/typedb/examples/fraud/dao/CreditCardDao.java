package com.typedb.examples.fraud.dao;

import com.typedb.examples.fraud.model.CreditCard;
import java.util.Hashtable;

public class CreditCardDao {

  protected static CreditCard fromResult(Hashtable<String, String> result) {

    var bank = BankDao.fromResult(result);

    var ccNum = result.get("ccNum");

    var cc = new CreditCard(ccNum, bank);

    return cc;
  }
}
