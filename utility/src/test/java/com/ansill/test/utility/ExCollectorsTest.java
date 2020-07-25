package com.ansill.test.utility;

import com.ansill.utility.ExCollectors;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static com.ansill.utility.Utility.generateString;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExCollectorsTest{


  @Test
  void testToMap(){

    // Set up random
    Random random = new SecureRandom();

    // Create random numbers
    int count = random.nextInt(300) + 100;

    // Set up original
    HashMap<String,Boolean> original = new HashMap<>();

    // Create random elements
    for(int i = 0; i < count; i++){
      original.put(generateString(32), random.nextBoolean());
    }

    // Mapped full
    @SuppressWarnings("ConstantConditions")
    Map<String,Boolean> full = original.entrySet()
                                       .stream()
                                       .filter(Map.Entry::getValue)
                                       .collect(Collectors.toMap(
                                         Map.Entry::getKey,
                                         Map.Entry::getValue
                                       ));

    // Mapped using shortcut
    Map<String,Boolean> shortcut = original.entrySet()
                                           .stream()
                                           .filter(Map.Entry::getValue)
                                           .collect(ExCollectors.toMap());

    // Compare
    assertEquals(full, shortcut);
  }

  @Test
  void testToConcurrentMap(){

    // Set up random
    Random random = new SecureRandom();

    // Create random numbers
    int count = random.nextInt(300) + 100;

    // Set up original
    HashMap<String,Boolean> original = new HashMap<>();

    // Create random elements
    for(int i = 0; i < count; i++){
      original.put(generateString(32), random.nextBoolean());
    }

    // Mapped full
    @SuppressWarnings("ConstantConditions")
    Map<String,Boolean> full = original.entrySet()
                                       .stream()
                                       .filter(Map.Entry::getValue)
                                       .collect(Collectors.toMap(
                                         Map.Entry::getKey,
                                         Map.Entry::getValue
                                       ));

    // Mapped using shortcut
    Map<String,Boolean> shortcut = original.entrySet()
                                           .stream()
                                           .filter(Map.Entry::getValue)
                                           .collect(ExCollectors.toConcurrentMap());

    // Compare
    assertEquals(full, shortcut);
  }
}