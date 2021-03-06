package com.ansill.utility;

import com.ansill.validation.Validation;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/** Utility Class */
public final class Utility{

  /** RNG */
  private static final AtomicReference<Random> RANDOM_GENERATOR = new AtomicReference<>(null);

  /** Hex array */
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  /** Class for unmodifiable map class created by Collections.unmodifiableMap(Map<K,V>) */
  @Nonnull
  private static final Class<?> UNMODIFIABLE_MAP_CLASS;

  /** Class for unmodifiable set class created by Collections.unmodifiableSet(Set<V>) */
  @Nonnull
  private static final Class<?> UNMODIFIABLE_SET_CLASS;

  /** Class for unmodifiable list class created by Collections.unmodifiableList(List<V>) */
  @Nonnull
  private static final Class<?> UNMODIFIABLE_LIST_CLASS;

  /** Class for unmodifiable list class created by Collections.unmodifiableSet(List<V>) that's for special list classes like ArrayList */
  @Nonnull
  private static final Class<?> UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS;

  static{
    UNMODIFIABLE_MAP_CLASS = Collections.unmodifiableMap(new HashMap<>()).getClass();
    UNMODIFIABLE_SET_CLASS = Collections.unmodifiableSet(new HashSet<>()).getClass();
    UNMODIFIABLE_LIST_CLASS = Collections.unmodifiableList(new LinkedList<>()).getClass();
    UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS = Collections.unmodifiableList(new ArrayList<>()).getClass();
  }

  /**
   * Private constructor
   * <p>
   * No instantiations allowed because this is an utility class
   *
   * @throws AssertionError thrown if any instantiations were attempted
   */
  private Utility(){
    throw new AssertionError(f("No {} instances for you!", this.getClass().getName()));
  }

  /**
   * Converts bytes to hex string
   *
   * @param bytes byte array
   * @return hex string
   */
  @Nonnull
  public static String bytesToHex(byte[] bytes){
    char[] hexChars = new char[bytes.length * 2];
    for(int j = 0; j < bytes.length; j++){
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Converts hex string to byte array
   *
   * @param string hex string
   * @return byte array
   */
  @Nonnull
  public static byte[] hexToBytes(String string){
    int len = string.length();
    byte[] data = new byte[len / 2];
    for(int i = 0; i < len; i += 2){
      data[i / 2] = (byte) (
        (Character.digit(string.charAt(i), 16) << 4)
        + Character.digit(string.charAt(i + 1), 16)
      );
    }
    return data;
  }

  /**
   * Formats string, replaces any '{}' with objects
   *
   * @param message message with '{}'
   * @param object  object to replace
   * @param objects objects to replace
   * @return formatted string
   */
  @Nonnull
  public static String f(@Nonnull String message, @Nullable Object object, @Nonnull Object... objects){
    return format(message, object, objects);
  }

  /**
   * Formats string, replaces any '{}' with objects
   *
   * @param message message with '{}'
   * @param object  object to replace
   * @param objects objects to replace
   * @return formatted string
   */
  @Nonnull
  public static String format(@Nullable String message, @Nullable Object object, @Nullable Object... objects){
    if(object == null && objects == null) return format(message, (Object[]) null);
    if(object == null) return format(message, objects);
    if(objects == null) return format(message, new Object[]{object});
    Object[] newobj = new Object[objects.length + 1];
    newobj[0] = object;
    System.arraycopy(objects, 0, newobj, 1, objects.length);
    return format(message, newobj);
  }

  /**
   * Formats string, replaces any '{}' with objects
   *
   * @param message message with '{}'
   * @param objects objects to replace
   * @return formatted string
   */
  /* NOTE: This is private so it prevents Java from accepting 'format("message {}");' without objects arguments. This forces users to put down at least one object argument */
  @Nonnull
  private static String format(@Nullable String message, @Nullable Object... objects){

    // If any of parameters are null, then return message
    if(message == null) return "null";
    if(objects == null) return message;

    // Set up builder
    StringBuilder builder = new StringBuilder();

    // Set up index in objects
    int objectsIndex = 0;

    // Set up indices for message
    int previousIndex = 0;
    int braceIndex = 0;

    // Loop until all is replaced or all elements in object array is used
    while(objectsIndex < objects.length && (braceIndex = message.indexOf("{}", braceIndex)) != -1){

      // Copy in characters since previous index
      builder.append(message, previousIndex, braceIndex);

      // Update brace index to skip "{}"
      braceIndex = Math.min(braceIndex + 2, message.length());

      // Update the previous index
      previousIndex = braceIndex;

      // Format 'null' if null
      if(objects[objectsIndex] == null) builder.append("null");

        // Use normal string if String
      else if(objects[objectsIndex] instanceof String) builder.append((String) objects[objectsIndex]);

        // Else, use .toString() method
      else builder.append(objects[objectsIndex].toString());

      // Increment the array
      objectsIndex++;

    }

    // Finish the string if there's any remaining
    if(previousIndex < message.length()) builder.append(message, previousIndex, message.length());

    // Return result
    return builder.toString();
  }

  /**
   * Simple toString method that will inspect the object and print out all of its fields
   *
   * @param object object to be out-stringed
   * @return String representation of object
   */
  public static String simpleToString(@Nonnull Object object){

    // Ensure no null
    Validation.assertNonnull(object, "object");

    // If string, then return string
    if(object instanceof String) return (String) object;

    // Print class name and its fields
    return object.getClass().getSimpleName() + "(" + Arrays.stream(object.getClass().getDeclaredFields())
                                                           .filter(field -> !Modifier.isStatic(field.getModifiers()))
                                                           .map(field -> {
                                                             try{
                                                               return field.getName() +
                                                                      "=" +
                                                                      sensibleToString(field.get(object));
                                                             }catch(IllegalAccessException e){

                                                               // Check if it's access issue
                                                               //if (!e.getMessage().contains("modifiers \"private")) return "error";

                                                               // Temporarily change access
                                                               field.setAccessible(true);
                                                               try{

                                                                 // Access it
                                                                 return field.getName() +
                                                                        "=" +
                                                                        sensibleToString(field.get(object));

                                                               }catch(IllegalAccessException ex){
                                                                 ex.printStackTrace();
                                                                 return "inaccessible";
                                                               }finally{
                                                                 field.setAccessible(false);
                                                               }
                                                             }
                                                           })
                                                           .collect(Collectors.joining(", ")) + ")";
  }

  /**
   * Simple function that if it's a String, it adds double quotation marks to each of its ends, otherwise if it is not a string, it simply returns .toString() representation
   *
   * @param object object
   * @return string representation
   */
  @Nonnull
  public static String sensibleToString(@Nullable Object object){
    if(object == null) return "null";
    if(object instanceof String) return "\"" + object.toString() + "\"";
    else return object.toString();
  }

  /**
   * Retrieves an shared random generator
   * <p>
   * The generator is created with SecureRandom()
   *
   * @return random generator
   */
  @Nonnull
  private static Random getRandom(){
    return RANDOM_GENERATOR.updateAndGet(item -> item == null ? new SecureRandom() : item);
  }

  /**
   * Generates string with random characters of specified length
   *
   * @param length          length of string
   * @param randomGenerator random generator used to generate the string
   * @return randomized string up to specified length
   */
  @Nonnull
  public static String generateString(@Nonnull Random randomGenerator, @Nonnegative long length){

    // Check length
    Validation.assertNonnull(randomGenerator, "randomGenerator");
    Validation.assertNaturalNumber(length, "length");

    // Set up characterset
    String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String fullAlphabetSet = alphabet + alphabet.toLowerCase() + "0123456789";

    // Set up string builder
    StringBuilder sb = new StringBuilder();

    // Build random string
    LongStream.range(0, length)
              .forEach(i -> sb.append(fullAlphabetSet.charAt(randomGenerator.nextInt(fullAlphabetSet.length()))));

    // Return the string
    return sb.toString();

  }

  /**
   * Generates string with random characters of specified length
   *
   * @param length length of string
   * @return randomized string up to specified length
   */
  @Nonnull
  public static String generateString(@Nonnegative long length){
    return generateString(getRandom(), length);
  }

  /**
   * Behaves same as Collections.unmodifiableMap(Map) but this function will simply return if input map is already an unmodifiable map instead of wrapping it again
   *
   * @param originalMap original map
   * @param <K>         key type of map
   * @param <V>         value type of map
   * @return unmodifiable map
   */
  @Nonnull
  public static <K, V> Map<K,V> unmodifiableMap(@Nonnull Map<K,V> originalMap){
    if(originalMap.getClass().equals(UNMODIFIABLE_MAP_CLASS)) return originalMap;
    return Collections.unmodifiableMap(originalMap);
  }

  /**
   * Behaves same as Collections.unmodifiableSet(Set) but this function will simply return if input set is already an unmodifiable set instead of wrapping it again
   *
   * @param originalSet original set
   * @param <V>         value type of set
   * @return unmodifiable set
   */
  @Nonnull
  public static <V> Set<V> unmodifiableSet(@Nonnull Set<V> originalSet){
    if(originalSet.getClass().equals(UNMODIFIABLE_SET_CLASS)) return originalSet;
    return Collections.unmodifiableSet(originalSet);
  }

  /**
   * Behaves same as Collections.unmodifiableList(Set) but this function will simply return if input list is already an unmodifiable list instead of wrapping it again
   *
   * @param originalList original list
   * @param <V>          value type of list
   * @return unmodifiable list
   */
  @Nonnull
  public static <V> List<V> unmodifiableList(@Nonnull List<V> originalList){
    if(originalList.getClass().equals(UNMODIFIABLE_LIST_CLASS) || originalList.getClass().equals(
      UNMODIFIABLE_RANDOM_ACCESS_LIST_CLASS)) return originalList;
    return Collections.unmodifiableList(originalList);
  }

  /**
   * Unites multiple sets together, will re-use any one of set if all other sets are empty
   *
   * @param originalSet original set
   * @param otherSet    other set
   * @param moreSet     list of additional set
   * @param <V>         value type of set
   * @return united set
   */
  @SafeVarargs
  @Nonnull
  public static <V> Set<V> union(@Nonnull Set<V> originalSet, @Nonnull Set<V> otherSet, @Nonnull Set<V>... moreSet){
    long nonEmptys = Arrays.stream(moreSet).filter(item -> !item.isEmpty()).count();
    if(nonEmptys == 0 && otherSet.isEmpty()) return originalSet;
    if(nonEmptys == 0 && originalSet.isEmpty()) return otherSet;
    if(nonEmptys == 1 && originalSet.isEmpty() && otherSet.isEmpty()){
      return Arrays.stream(moreSet)
                   .filter(item -> !item.isEmpty())
                   .findAny()
                   .orElseThrow(() -> new RuntimeException("count failed"));
    }
    Set<V> newSet = new HashSet<>(originalSet);
    newSet.addAll(otherSet);
    Arrays.stream(moreSet).forEach(newSet::addAll);
    return newSet;
  }

  /**
   * Unites multiple sets together, will re-use any one of set if all other sets are empty, then returns as unmodifiable set
   *
   * @param originalSet original set
   * @param otherSet    other set
   * @param moreSet     list of additional set
   * @param <V>         value type of set
   * @return unmodifiable united set
   */
  @SafeVarargs
  @Nonnull
  public static <V> Set<V> unmodifiableSet(
    @Nonnull Set<V> originalSet,
    @Nonnull Set<V> otherSet,
    @Nonnull Set<V>... moreSet
  ){
    return unmodifiableSet(union(originalSet, otherSet, moreSet));
  }

  /**
   * Behaves same as Arrays.asList(V...)  but for set
   *
   * @param items items
   * @param <V>   value type of set
   * @return set
   */
  @SafeVarargs
  @Nonnull
  public static <V> Set<V> asSet(@Nonnull V... items){
    return new HashSet<>(Arrays.asList(items));
  }
}
