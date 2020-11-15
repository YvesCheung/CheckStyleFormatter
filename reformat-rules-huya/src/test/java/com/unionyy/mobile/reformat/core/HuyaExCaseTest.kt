package com.unionyy.mobile.reformat.core

import com.unionyy.mobile.reformat.core.rule.DumpAST
import com.unionyy.mobile.reformat.core.rule.HuyaExReplacement
import org.junit.Test

/**
 * @author YvesCheung
 * 2020/11/14
 */
class HuyaExCaseTest {

    private val arkUtil = """
package com.duowan.ark;

import com.duowan.ark.def.Properties;
import com.huya.mtp.utils.Config;
import com.huya.mtp.utils.DebugUtils;
import com.duowan.ark.util.KLog;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.EventBusException;

/**
 * Created by legend on 15/3/20.
 */
public class ArkUtils {

    public static void crashIfDebug(String format, Object... args) {
    }

    public static void crashIfDebug(Throwable cause, String format, Object... args) {
    }
    
    public static void crashIfDebug(boolean condition, String format, Object... args) {
    }

    public static void crashIfInMainThreadDebug(String format, Object... args) {
    }

    public static void crashIfNotInMainThreadDebug(final String format, Object... args) {
    }
}
""".trimIndent()

    private val mapEx = """
package com.hyex.collections;

import androidx.annotation.NonNull;

import com.duowan.ark.ArkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * java集合Map类安全方法代理类，原则上kiwi项目不允许直接调用Map的方法
 *
 * @author lijunqing on 2019/8/30
 */
public class MapEx {

    private static final String TAG = "MapEx";

    public static <K, V, M extends Map<K, V>> boolean containsKey(@NotNull M m, K key, boolean defaultValue) {
        try {
            return m.containsKey(key);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return defaultValue;
    }

    public static <K, V, M extends Map<K, V>> boolean containsValue(@NotNull M m, V value) {
        try {
            return m.containsValue(value);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }


    /**
     * Returns the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key.
     *
     * @param m            the target map
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     * @implSpec The default implementation makes no guarantees about synchronization
     * or atomicity properties of this method. Any implementation providing
     * atomicity guarantees must override this method and document its
     * concurrency properties.
     */
    public static <K, V, M extends Map<K, V>> V get(@NotNull M m, K key, V defaultValue) {
        try {
            V v;
            return (((v = m.get(key)) != null) || m.containsKey(key))
                    ? v
                    : defaultValue;
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return defaultValue;
    }


    public static <K, V, M extends Map<K, V>> V put(@NotNull M m, K key, V value) {
        try {
            return m.put(key, value);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <K, V, M extends Map<K, V>> V remove(@NotNull M m, K key) {
        try {
            return m.remove(key);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <K, V, M extends Map<K, V>> void putAll(@NotNull M m, @NonNull Map<? extends K, ? extends V> mm) {
        try {
            m.putAll(mm);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
    }


    public static <K, V, M extends Map<K, V>> void clear(@NotNull M m) {
        try {
            m.clear();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
    }

    public static <K, V, M extends Map<K, V>> Set<K> keySet(@NotNull M m) {
        try {
            return m.keySet();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <K, V, M extends Map<K, V>> Collection<V> values(@NotNull M m) {
        try {
            return m.values();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <K, V, M extends Map<K, V>> Set<Map.Entry<K, V>> entrySet(@NotNull M m) {
        try {
            return m.entrySet();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <K, V, M extends Map<K, V>> int size(M m) {
        try {
            return m.size();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return 0;
    }
}
""".trimIndent()

    private val queueEx = """
package com.hyex.collections;

import androidx.annotation.NonNull;

import com.duowan.ark.ArkUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * java集合Quene类安全方法代理类，原则上kiwi项目不允许直接调用Quenet的方法
 *
 * @author lijunqing on 2019/8/30
 */
public class QueueEx {

    private static final String TAG = "QueueEx";

    public static <E, Q extends Queue<E>> boolean contains(@NotNull Q q, E o) {
        try {
            return q.contains(o);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> int size(@NotNull Q q) {
        try {
            return q.size();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return 0;
    }

    public static <E, Q extends Queue<E>> Iterator<E> iterator(@NotNull Q q) {
        try {
            return q.iterator();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <E, Q extends Queue<E>> Object[] toArray(@NotNull Q q) {
        try {
            return q.toArray();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return new Object[0];
    }


    public static <E, Q extends Queue<E>> E[] toArray(@NotNull Q q, @NonNull E[] a) {
        try {
            return q.toArray(a);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <E, Q extends Queue<E>> boolean add(@NotNull Q q, E e) {
        try {
            return q.add(e);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean remove(@NotNull Q q, Object o) {
        try {
            return q.remove(o);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean containsAll(@NotNull Q q, @NonNull Collection<?> c) {
        try {
            return q.containsAll(c);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean addAll(@NotNull Q q, @NonNull Collection<? extends E> c) {
        try {
            return q.addAll(c);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean removeAll(@NotNull Q q, @NonNull Collection<?> c) {
        try {
            return q.removeAll(c);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean retainAll(@NotNull Q q, @NonNull Collection<?> c) {
        try {
            return q.retainAll(c);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> void clear(@NotNull Q q) {
        try {
            q.clear();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
    }

    public static <E, Q extends Queue<E>> boolean offer(@NotNull Q q, E e) {
        try {
            return q.offer(e);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> boolean isEmpty(@NotNull Q q) {
        try {
            return q.isEmpty();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return false;
    }

    public static <E, Q extends Queue<E>> E remove(@NotNull Q q) {
        try {
            return q.remove();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <E, Q extends Queue<E>> E poll(@NotNull Q q) {
        try {
            return q.poll();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <E, Q extends BlockingQueue<E>> E poll(@NotNull Q q, long timeout, TimeUnit unit) {
        try {
            return q.poll(timeout,unit);
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }

    public static <E, Q extends Queue<E>> E element(@NotNull Q q) {
        try {
            return q.element();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }


    public static <E, Q extends Queue<E>> E peek(@NotNull Q q) {
        try {
            return q.peek();
        } catch (Exception ex) {
            ArkUtils.crashIfDebug(TAG, ex);
        }
        return null;
    }
}
""".trimIndent()

    @Test
    fun mapPutCase() {
        val text = CodeFormatter.reformat("A.java", """
package a;

import java.util.HashMap;
import java.util.Map;

public class A {

    public com.java.B a = new com.java.B();
    
    Map<String, String> field1 = new HashMap<String, String>() {
        {
            put("k0", "v0");
        }
    };

    {
        field1.put("k1", "v1");
    }

    private static void main() {
        Map<String, String> map = new HashMap<>();
        map.put("k2", "v2");
        
        a.b();
    }
}
""".trimIndent(), setOf(DumpAST(), HuyaExReplacement()))

        println(text)
    }
}