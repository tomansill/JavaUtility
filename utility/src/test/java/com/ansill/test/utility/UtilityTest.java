package com.ansill.test.utility;

import com.ansill.utility.Utility;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.ansill.utility.Utility.generateString;
import static org.junit.jupiter.api.Assertions.*;

class UtilityTest{

  @Test
  void testNegativeLength(){
    assertThrows(IllegalArgumentException.class, () -> generateString(-1));
    assertThrows(IllegalArgumentException.class, () -> generateString(new SecureRandom(), -1));
  }

  @SuppressWarnings("ConstantConditions") // Ignored because of test
  @Test
  void testNullGenerator(){
    assertThrows(IllegalArgumentException.class, () -> generateString(null, 1));
  }

  @Test
  void testRandomNumberGenerator(){

    // Get random generator
    Random randomGenerator = new SecureRandom();

    // Set length
    int length = randomGenerator.nextInt(49) + 1;

    // Generate it
    String string = generateString(length);

    // Check length
    assertEquals(length, string.length());
  }

  @Test
  void testRandomness(){

    // Get random generator
    Random randomGenerator = new SecureRandom("some seed".getBytes());

    // Set length
    int length = 10;

    // Set
    Set<String> generated = new HashSet<>();

    // Repeat a million times
    for(int i = 0; i < 1_000_000; i++){

      // Generate it
      String string = generateString(randomGenerator, length);

      // Check length
      assertEquals(length, string.length());

      // Ensure no duplicate
      assertTrue(
        generated.add(string),
        "Found a duplicate string in the set. Either the code is incorrect or maybe you should buy a lottery ticket ;)"
      );
    }
  }

  @Test
  void testUnmodifiableMap(){

    // Create map
    Map<String,String> map = new HashMap<>();
    map.put(generateString(8), generateString(32));
    map.put(generateString(3), generateString(22));

    // Create map
    Map<String,String> unmodMap = Utility.unmodifiableMap(map);
    assertNotEquals(System.identityHashCode(unmodMap), System.identityHashCode(map));
    assertEquals(map, unmodMap);

    // Manipulate it
    map.put(generateString(4), generateString(32));
    assertEquals(map, unmodMap);

    // Try again
    Map<String,String> otherUnmodMap = Utility.unmodifiableMap(unmodMap);
    assertEquals(System.identityHashCode(unmodMap), System.identityHashCode(otherUnmodMap));
    assertEquals(unmodMap, otherUnmodMap);

    // Clone map
    Map<String,String> cloned = new HashMap<>(unmodMap);
    assertNotEquals(System.identityHashCode(unmodMap), System.identityHashCode(cloned));
    assertEquals(unmodMap, otherUnmodMap);

    // Manipulate it
    cloned.put(generateString(4), generateString(32));
    assertNotEquals(map, cloned);
    assertNotEquals(unmodMap, cloned);

    // Ensure it's unmodifiable
    assertThrows(UnsupportedOperationException.class, () -> unmodMap.put(generateString(2), generateString(22)));
  }

  @Test
  void testUnmodifiableSet(){

    // Create set
    Set<String> set = new HashSet<>();
    set.add(generateString(32));
    set.add(generateString(22));

    // Create set
    Set<String> unmodSet = Utility.unmodifiableSet(set);
    assertNotEquals(System.identityHashCode(unmodSet), System.identityHashCode(set));
    assertEquals(set, unmodSet);

    // Manipulate it
    set.add(generateString(32));
    assertEquals(set, unmodSet);

    // Try again
    Set<String> otherUnmodSet = Utility.unmodifiableSet(unmodSet);
    assertEquals(System.identityHashCode(unmodSet), System.identityHashCode(otherUnmodSet));
    assertEquals(unmodSet, otherUnmodSet);

    // Clone set
    Set<String> cloned = new HashSet<>(unmodSet);
    assertNotEquals(System.identityHashCode(unmodSet), System.identityHashCode(cloned));
    assertEquals(unmodSet, otherUnmodSet);

    // Manipulate it
    cloned.add(generateString(32));
    assertNotEquals(set, cloned);
    assertNotEquals(unmodSet, cloned);

    // Ensure it's unmodifiable
    assertThrows(UnsupportedOperationException.class, () -> unmodSet.add(generateString(22)));
  }

  @Test
  void testUnmodifiableList(){

    // Create list
    List<String> list = new LinkedList<>();
    list.add(generateString(32));
    list.add(generateString(22));

    // Create list
    List<String> unmodList = Utility.unmodifiableList(list);
    assertNotEquals(System.identityHashCode(unmodList), System.identityHashCode(list));
    assertEquals(list, unmodList);

    // Manipulate it
    list.add(generateString(32));
    assertEquals(list, unmodList);

    // Try again
    List<String> otherUnmodList = Utility.unmodifiableList(unmodList);
    assertEquals(System.identityHashCode(unmodList), System.identityHashCode(otherUnmodList));
    assertEquals(unmodList, otherUnmodList);

    // Clone list
    List<String> cloned = new LinkedList<>(unmodList);
    assertNotEquals(System.identityHashCode(unmodList), System.identityHashCode(cloned));
    assertEquals(unmodList, otherUnmodList);

    // Manipulate it
    cloned.add(generateString(32));
    assertNotEquals(list, cloned);
    assertNotEquals(unmodList, cloned);

    // Ensure it's unmodifiable
    assertThrows(UnsupportedOperationException.class, () -> unmodList.add(generateString(22)));
  }

  @Test
  void testUnmodifiableRandomAccessList(){

    // Create list
    List<String> list = new ArrayList<>();
    list.add(generateString(32));
    list.add(generateString(22));

    // Create list
    List<String> unmodList = Utility.unmodifiableList(list);
    assertNotEquals(System.identityHashCode(unmodList), System.identityHashCode(list));
    assertEquals(list, unmodList);

    // Manipulate it
    list.add(generateString(32));
    assertEquals(list, unmodList);

    // Try again
    List<String> otherUnmodList = Utility.unmodifiableList(unmodList);
    assertEquals(System.identityHashCode(unmodList), System.identityHashCode(otherUnmodList));
    assertEquals(unmodList, otherUnmodList);

    // Clone list
    List<String> cloned = new ArrayList<>(unmodList);
    assertNotEquals(System.identityHashCode(unmodList), System.identityHashCode(cloned));
    assertEquals(unmodList, otherUnmodList);

    // Manipulate it
    cloned.add(generateString(32));
    assertNotEquals(list, cloned);
    assertNotEquals(unmodList, cloned);

    // Ensure it's unmodifiable
    assertThrows(UnsupportedOperationException.class, () -> unmodList.add(generateString(22)));
  }

  @Test
  void testUnion(){

    // Create Set
    Set<String> set = new HashSet<>();
    set.add(generateString(32));
    set.add(generateString(22));

    // Create set
    Set<String> combinedSet = Utility.union(set, Collections.emptySet());
    assertEquals(System.identityHashCode(combinedSet), System.identityHashCode(set));
    assertEquals(set, combinedSet);

    // Again
    combinedSet = Utility.union(set, Collections.emptySet(), Collections.emptySet());
    assertEquals(System.identityHashCode(combinedSet), System.identityHashCode(set));
    assertEquals(set, combinedSet);

    // Reverse
    combinedSet = Utility.union(Collections.emptySet(), set);
    assertEquals(System.identityHashCode(combinedSet), System.identityHashCode(set));
    assertEquals(set, combinedSet);

    // Again
    combinedSet = Utility.union(Collections.emptySet(), set, Collections.emptySet());
    assertEquals(System.identityHashCode(combinedSet), System.identityHashCode(set));
    assertEquals(set, combinedSet);

    // In variadic
    combinedSet = Utility.union(Collections.emptySet(), Collections.emptySet(), set);
    assertEquals(System.identityHashCode(combinedSet), System.identityHashCode(set));
    assertEquals(set, combinedSet);

    // Create Set
    Set<String> set1 = new HashSet<>();
    set1.add(generateString(32));
    set1.add(set.iterator().next());

    // Unite
    combinedSet = Utility.union(set, set1);
    assertNotEquals(System.identityHashCode(set), System.identityHashCode(combinedSet));
    assertNotEquals(System.identityHashCode(set1), System.identityHashCode(combinedSet));
    Set<String> testSet = new HashSet<>(set);
    testSet.addAll(set1);
    assertEquals(testSet, combinedSet);

    // Create other Set
    Set<String> set2 = new HashSet<>();
    set2.add(generateString(32));
    set2.add(generateString(33));
    set2.add(set1.iterator().next());

    // Unite
    combinedSet = Utility.union(set, set1, set2);
    assertNotEquals(System.identityHashCode(set), System.identityHashCode(combinedSet));
    assertNotEquals(System.identityHashCode(set1), System.identityHashCode(combinedSet));
    assertNotEquals(System.identityHashCode(set2), System.identityHashCode(combinedSet));
    testSet = new HashSet<>(set);
    testSet.addAll(set1);
    testSet.addAll(set2);
    assertEquals(testSet, combinedSet);

    // Modify it
    combinedSet.add(generateString(3));

    // Create unmodifiable
    Set<String> unmodSet = Utility.unmodifiableSet(set, set1, set2);

    // Ensure it's unmodifiable
    assertThrows(UnsupportedOperationException.class, () -> unmodSet.add(generateString(22)));
  }

  @Test
  void testAsSet(){

    // Create values
    String item1 = generateString(32);
    String item2 = generateString(32);
    String item3 = generateString(32);
    String item4 = generateString(32);

    // Create set
    Set<String> items = Utility.asSet(item1, item2, item3, item4, item2);

    // Compare
    assertEquals(new HashSet<>(Arrays.asList(item1, item2, item3, item4)), items);
  }
}