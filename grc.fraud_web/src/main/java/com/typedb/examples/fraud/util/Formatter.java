package com.typedb.examples.fraud.util;

public class Formatter {

  public static String formatDateTime(String dateTime) {

    String[] dateTimeParts = dateTime.split(" ");

    var formattedDateTime = dateTimeParts[0] + "T" + dateTimeParts[1];

    return formattedDateTime;
  }
}
