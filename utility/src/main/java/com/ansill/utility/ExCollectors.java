package com.ansill.utility;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public final class ExCollectors{

  private static final Set<Collector.Characteristics> CH_ID
    = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

  private static final Set<Collector.Characteristics> CH_CONCURRENT_ID
    = Collections.unmodifiableSet(EnumSet.of(
    Collector.Characteristics.CONCURRENT,
    Collector.Characteristics.UNORDERED,
    Collector.Characteristics.IDENTITY_FINISH
  ));

  private ExCollectors(){
    throw new AssertionError("Instantiation of this class is not allowed");
  }

  public static <K, V> Collector<Map.Entry<K,V>,Map<K,V>,Map<K,V>> toMap(){
    return new Collector<Map.Entry<K,V>,Map<K,V>,Map<K,V>>(){

      @Override
      public Supplier<Map<K,V>> supplier(){
        return HashMap::new;
      }

      @SuppressWarnings("RedundantTypeArguments") // IDE's suggestion does not work
      @Override
      public BiConsumer<Map<K,V>,Map.Entry<K,V>> accumulator(){
        return uniqKeysMapAccumulator(Map.Entry<K,V>::getKey, Map.Entry<K,V>::getValue);
      }

      @Override
      public BinaryOperator<Map<K,V>> combiner(){
        return uniqKeysMapMerger();
      }

      @Override
      public Function<Map<K,V>,Map<K,V>> finisher(){
        return i -> i;
      }

      @Override
      public Set<Characteristics> characteristics(){
        return CH_CONCURRENT_ID;
      }
    };
  }

  public static <K, V> Collector<Map.Entry<K,V>,Map<K,V>,Map<K,V>> toConcurrentMap(){
    return new Collector<Map.Entry<K,V>,Map<K,V>,Map<K,V>>(){

      @Override
      public Supplier<Map<K,V>> supplier(){
        return ConcurrentHashMap::new;
      }

      @SuppressWarnings("RedundantTypeArguments") // IDE's suggestion does not work
      @Override
      public BiConsumer<Map<K,V>,Map.Entry<K,V>> accumulator(){
        return uniqKeysMapAccumulator(Map.Entry<K,V>::getKey, Map.Entry<K,V>::getValue);
      }

      @Override
      public BinaryOperator<Map<K,V>> combiner(){
        return uniqKeysMapMerger();
      }

      @Override
      public Function<Map<K,V>,Map<K,V>> finisher(){
        return i -> i;
      }

      @Override
      public Set<Characteristics> characteristics(){
        return CH_ID;
      }
    };
  }

  private static <T, K, V>
  BiConsumer<Map<K,V>,T> uniqKeysMapAccumulator(
    Function<? super T,? extends K> keyMapper,
    Function<? super T,? extends V> valueMapper
  ){
    return (map, element) -> {
      K k = keyMapper.apply(element);
      V v = Objects.requireNonNull(valueMapper.apply(element));
      V u = map.putIfAbsent(k, v);
      if(u != null) throw duplicateKeyException(k, u, v);
    };
  }

  private static <K, V, M extends Map<K,V>>
  BinaryOperator<M> uniqKeysMapMerger(){
    return (m1, m2) -> {
      for(Map.Entry<K,V> e : m2.entrySet()){
        K k = e.getKey();
        V v = Objects.requireNonNull(e.getValue());
        V u = m1.putIfAbsent(k, v);
        if(u != null) throw duplicateKeyException(k, u, v);
      }
      return m1;
    };
  }

  private static IllegalStateException duplicateKeyException(
    Object k, Object u, Object v
  ){
    return new IllegalStateException(String.format(
      "Duplicate key %s (attempted merging values %s and %s)",
      k, u, v
    ));
  }
}
