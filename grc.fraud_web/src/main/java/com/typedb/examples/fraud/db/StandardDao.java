package com.typedb.examples.fraud.db;

import java.util.Hashtable;
import java.util.Set;

public interface StandardDao<T> {

  Set<T> getAll();

  void insertAll(Set<T> data);

}
