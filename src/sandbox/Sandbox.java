package sandbox;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Just for testing out things to behave as expected
 * Created by Brendan Galea on 11/24/2015.
 */
public class Sandbox {

  private static SecureRandom random = new SecureRandom();

  public static void main(String[] args) {

    HashMap<String, Double> map = new HashMap<>();
    for (int i = 0; i < 10000; i++) {
      map.put(randomString(), Math.random());
    }

    Iterator<String> keys = map.keySet().iterator();
    Iterator<Double> values = map.values().iterator();

    while (keys.hasNext()) {
      if (!map.get(keys.next()).equals(values.next()))
        throw new RuntimeException("Failed to match all pairs");
    }

    keys = map.keySet().iterator();
    values = map.values().iterator();
    while (keys.hasNext()) {
      if (!map.get(keys.next()).equals(values.next()))
        throw new RuntimeException("Failed to match all pairs");
    }
    System.out.println("Success");
  }

  public static String randomString() {
    return new BigInteger(130, random).toString(32);
  }
}
