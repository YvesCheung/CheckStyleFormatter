package com.unionyy.mobile.reformat.core

import org.junit.Assert
import org.junit.Test

class TabCharacterTest {

    @Test
    fun testJavaTabClass() {

        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.cache;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.yy.mobile.util.TimeUtils;
import com.yy.mobile.util.json.JsonParser;
import com.yy.mobile.util.log.MLog;
import com.yy.mobile.util.taskexecutor.IQueueTaskExecutor;
import com.yy.mobile.util.taskexecutor.YYTaskExecutor;
import com.yy.mobile.util.valid.BlankUtil;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 缓存客户端
 *
 * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
 */
@SuppressLint("HandlerLeak")
public class CacheClient implements Cache {
    private static final String TAG = "CacheClient";


    private long defaultExpire;

    private IQueueTaskExecutor asyncTask = YYTaskExecutor.createAQueueExcuter();

    private Map<String, BlockingQueue<CallbackWrapper>> manager =
            new ConcurrentHashMap<String, BlockingQueue<CallbackWrapper>>();

    private CacheManager cacheManager;

    /**
     * 保证资源唯一
     */
    private String uri;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CallbackWrapper wrapper = (CallbackWrapper) msg.obj;
            ReturnCallback returnCallback = wrapper.getReturnCallback();
            if (returnCallback != null) {
                try {
                    wrapper.getReturnCallback().onReturn(wrapper.getData());
                    ;
                } catch (Exception e) {
                    MLog.error(TAG, e);
                }
            }
            ErrorCallback errorCallback = wrapper.getErrorCallback();
            if (errorCallback != null) {
                try {
                    wrapper.getErrorCallback().onError(wrapper.getError());
                } catch (Exception e) {
                    MLog.error(TAG, e);
                }
            }
        }
    };

    protected CacheClient(String uri) {
        this(uri, TimeUtils.MINUTES_OF_HOUR * TimeUtils.SECONDS_OF_MINUTE * TimeUtils.MILLIS_OF_SECOND);
    }

    protected CacheClient(String uri, long defaultExpire) {
        this.defaultExpire = defaultExpire;
        this.uri = uri;
        this.cacheManager = new CacheManager(uri);
    }

    @Override
    public void get(String key, ReturnCallback returncallback) {
        get(key, returncallback, null);
    }

    @Override
    public void get(String key, ReturnCallback returncallback, ErrorCallback errorCallback) {
        if (BlankUtil.isBlank(key)) {
            return;
        }
        final String mKey = key;
        BlockingQueue<CallbackWrapper> handlers = manager.get(mKey);
        if (handlers == null) {
            handlers = new LinkedBlockingQueue<CallbackWrapper>();
        }
        CallbackWrapper wrapper = new CallbackWrapper();
        wrapper.setReturnCallback(returncallback);
        wrapper.setErrorCallback(errorCallback);
        handlers.add(wrapper);
        manager.put(mKey, handlers);


        YYTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String data = "";
                CacheException error = null;
                BlockingQueue<CallbackWrapper> handlers = manager.get(mKey);
                if (handlers.isEmpty()) {
                    return;
                }

                try {
                    //TODO read json
                    String json = cacheManager.getCache(mKey);

                    CachePacket packet = JsonParser.parseJsonObject(json, CachePacket.class);
                    data = packet.getContent().toString();
                } catch (NoSuchKeyException e) {
                    error = e;
                    MLog.error(TAG, e);
                } catch (Exception e) {
                    error = new CacheException(mKey, "Wrap otherwise exceptions", e);
                    MLog.error(TAG, error);
                }
                for (; ;) {
                    CallbackWrapper wrapper = handlers.poll();
                    if (wrapper == null) {
                        break;
                    }
                    wrapper.setData(data);
                    wrapper.setError(error);
                    Message msg = Message.obtain();
                    msg.obj = wrapper;
                    handler.sendMessage(msg);
                }
            }

        }, 0);
    }

    //	@Override
    //	public String get(String key) {
    //		if(BlankUtil.isBlank(key)){
    //			return null;
    //		}
    //		//TODO 读文件json
    //		String result = null;
    //		try{
    //			String json = cacheManager.getCache(key);
    //			CachePacket packet = JsonParser.parseJsonObject(json, CachePacket.class);
    //            result = packet.getContent().toString();
    //		}catch(Exception e){
    //			MLog.error(TAG, e);
    //		}
    //		return result;
    //	}

    @Override
    public void put(String key, String value) {
        put(key, value, defaultExpire);
    }

    @Override
    public void put(String key, String value, long expire) {
        if (BlankUtil.isBlank(key)) {
            return;
        }
        final String mKey = key;
        final long mexpire = expire;
        CacheHeader header = new CacheHeader(key, expire, System.currentTimeMillis());
        CachePacket packet = new CachePacket(header, value);
        final String json = JsonParser.toJson(packet);
        asyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //TODO Save json
                cacheManager.putCache(mKey, json, mexpire);
            }
        }, 0);
    }

    public void remove(String key) {
        // TODO Auto-generated method stub
        cacheManager.remove(key);
    }


    public void clear() {
        // TODO Auto-generated method stub
        cacheManager.clear();
    }

    public class CallbackWrapper {

        private String data;

        private CacheException error;

        private ReturnCallback returnCallback;

        private ErrorCallback errorCallback;

        public CallbackWrapper() {
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public ReturnCallback getReturnCallback() {
            return returnCallback;
        }

        public void setReturnCallback(ReturnCallback returnCallback) {
            this.returnCallback = returnCallback;
        }

        public ErrorCallback getErrorCallback() {
            return errorCallback;
        }

        public void setErrorCallback(ErrorCallback errorCallback) {
            this.errorCallback = errorCallback;
        }

        public CacheException getError() {
            return error;
        }

        public void setError(CacheException error) {
            this.error = error;
        }


    }

    /**
     * 缓存协议头
     *
     * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
     */
    public class CacheHeader {

        private String key;

        private long expired;

        private long createTime;

        public CacheHeader(String key, long expired, long createTime) {
            super();
            this.key = key;
            this.expired = expired;
            this.createTime = createTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }


    }

    /**
     * 缓存协议包
     *
     * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
     */
    public class CachePacket {

        private CacheHeader header;

        private Object content;

        public CachePacket(CacheHeader header, Object content) {
            this.header = header;
            this.content = content;
        }

        public CacheHeader getHeader() {
            return header;
        }

        public void setHeader(CacheHeader header) {
            this.header = header;
        }

        public Object getContent() {
            return content;
        }

        public void setContents(Object content) {
            this.content = content;
        }


    }

    public String getUri() {
        return uri;
    }

    public static void main(String[] args) {
        //		User u = new User();
        //		u.password = "123";
        //		u.username = "simon";
        //
        //		CacheClient client = new CacheClient();
        //		client.putObject("user", u);
        //
        //		client.get("user", new ReturnCallback() {
        //
        //			@Override
        //			public void onReturn(String data) {
        //				// TODO Auto-generated method stub
        //
        //			}
        //		});
        //		System.out.println(u1.password+","+u1.username);
        //
        //		List<User> list = new ArrayList<User>();
        //		list.add(u);
        //		client.putObject("list", list);
        //
        //		List<User> u2 = client.getJSONList("list", User.class);
        //		System.out.println(u2);
    }
}
""".trimIndent())

        Assert.assertEquals("""package com.yy.mobile.cache;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.yy.mobile.util.TimeUtils;
import com.yy.mobile.util.json.JsonParser;
import com.yy.mobile.util.log.MLog;
import com.yy.mobile.util.taskexecutor.IQueueTaskExecutor;
import com.yy.mobile.util.taskexecutor.YYTaskExecutor;
import com.yy.mobile.util.valid.BlankUtil;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 缓存客户端
 *
 * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
 */
@SuppressLint("HandlerLeak")
public class CacheClient implements Cache {
    private static final String TAG = "CacheClient";


    private long defaultExpire;

    private IQueueTaskExecutor asyncTask = YYTaskExecutor.createAQueueExcuter();

    private Map<String, BlockingQueue<CallbackWrapper>> manager =
            new ConcurrentHashMap<String, BlockingQueue<CallbackWrapper>>();

    private CacheManager cacheManager;

    /**
     * 保证资源唯一
     */
    private String uri;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CallbackWrapper wrapper = (CallbackWrapper) msg.obj;
            ReturnCallback returnCallback = wrapper.getReturnCallback();
            if (returnCallback != null) {
                try {
                    wrapper.getReturnCallback().onReturn(wrapper.getData());
""" + "                    \n" + """
                } catch (Exception e) {
                    MLog.error(TAG, e);
                }
            }
            ErrorCallback errorCallback = wrapper.getErrorCallback();
            if (errorCallback != null) {
                try {
                    wrapper.getErrorCallback().onError(wrapper.getError());
                } catch (Exception e) {
                    MLog.error(TAG, e);
                }
            }
        }
    };

    protected CacheClient(String uri) {
        this(uri, TimeUtils.MINUTES_OF_HOUR * TimeUtils.SECONDS_OF_MINUTE * TimeUtils.MILLIS_OF_SECOND);
    }

    protected CacheClient(String uri, long defaultExpire) {
        this.defaultExpire = defaultExpire;
        this.uri = uri;
        this.cacheManager = new CacheManager(uri);
    }

    @Override
    public void get(String key, ReturnCallback returncallback) {
        get(key, returncallback, null);
    }

    @Override
    public void get(String key, ReturnCallback returncallback, ErrorCallback errorCallback) {
        if (BlankUtil.isBlank(key)) {
            return;
        }
        final String mKey = key;
        BlockingQueue<CallbackWrapper> handlers = manager.get(mKey);
        if (handlers == null) {
            handlers = new LinkedBlockingQueue<CallbackWrapper>();
        }
        CallbackWrapper wrapper = new CallbackWrapper();
        wrapper.setReturnCallback(returncallback);
        wrapper.setErrorCallback(errorCallback);
        handlers.add(wrapper);
        manager.put(mKey, handlers);


        YYTaskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String data = "";
                CacheException error = null;
                BlockingQueue<CallbackWrapper> handlers = manager.get(mKey);
                if (handlers.isEmpty()) {
                    return;
                }

                try {
                    //TODO read json
                    String json = cacheManager.getCache(mKey);

                    CachePacket packet = JsonParser.parseJsonObject(json, CachePacket.class);
                    data = packet.getContent().toString();
                } catch (NoSuchKeyException e) {
                    error = e;
                    MLog.error(TAG, e);
                } catch (Exception e) {
                    error = new CacheException(mKey, "Wrap otherwise exceptions", e);
                    MLog.error(TAG, error);
                }
                for (; ; ) {
                    CallbackWrapper wrapper = handlers.poll();
                    if (wrapper == null) {
                        break;
                    }
                    wrapper.setData(data);
                    wrapper.setError(error);
                    Message msg = Message.obtain();
                    msg.obj = wrapper;
                    handler.sendMessage(msg);
                }
            }

        }, 0);
    }

    //	@Override
    //	public String get(String key) {
    //		if(BlankUtil.isBlank(key)){
    //			return null;
    //		}
    //		//TODO 读文件json
    //		String result = null;
    //		try{
    //			String json = cacheManager.getCache(key);
    //			CachePacket packet = JsonParser.parseJsonObject(json, CachePacket.class);
    //            result = packet.getContent().toString();
    //		}catch(Exception e){
    //			MLog.error(TAG, e);
    //		}
    //		return result;
    //	}

    @Override
    public void put(String key, String value) {
        put(key, value, defaultExpire);
    }

    @Override
    public void put(String key, String value, long expire) {
        if (BlankUtil.isBlank(key)) {
            return;
        }
        final String mKey = key;
        final long mexpire = expire;
        CacheHeader header = new CacheHeader(key, expire, System.currentTimeMillis());
        CachePacket packet = new CachePacket(header, value);
        final String json = JsonParser.toJson(packet);
        asyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                //TODO Save json
                cacheManager.putCache(mKey, json, mexpire);
            }
        }, 0);
    }

    public void remove(String key) {
        // TODO Auto-generated method stub
        cacheManager.remove(key);
    }


    public void clear() {
        // TODO Auto-generated method stub
        cacheManager.clear();
    }

    public class CallbackWrapper {

        private String data;

        private CacheException error;

        private ReturnCallback returnCallback;

        private ErrorCallback errorCallback;

        public CallbackWrapper() {
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public ReturnCallback getReturnCallback() {
            return returnCallback;
        }

        public void setReturnCallback(ReturnCallback returnCallback) {
            this.returnCallback = returnCallback;
        }

        public ErrorCallback getErrorCallback() {
            return errorCallback;
        }

        public void setErrorCallback(ErrorCallback errorCallback) {
            this.errorCallback = errorCallback;
        }

        public CacheException getError() {
            return error;
        }

        public void setError(CacheException error) {
            this.error = error;
        }


    }

    /**
     * 缓存协议头
     *
     * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
     */
    public class CacheHeader {

        private String key;

        private long expired;

        private long createTime;

        public CacheHeader(String key, long expired, long createTime) {
            super();
            this.key = key;
            this.expired = expired;
            this.createTime = createTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public long getExpired() {
            return expired;
        }

        public void setExpired(long expired) {
            this.expired = expired;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }


    }

    /**
     * 缓存协议包
     *
     * @author <a href="mailto:kuanglingxuan@yy.com">匡凌轩</a> V1.0
     */
    public class CachePacket {

        private CacheHeader header;

        private Object content;

        public CachePacket(CacheHeader header, Object content) {
            this.header = header;
            this.content = content;
        }

        public CacheHeader getHeader() {
            return header;
        }

        public void setHeader(CacheHeader header) {
            this.header = header;
        }

        public Object getContent() {
            return content;
        }

        public void setContents(Object content) {
            this.content = content;
        }


    }

    public String getUri() {
        return uri;
    }

    public static void main(String[] args) {
        //		User u = new User();
        //		u.password = "123";
        //		u.username = "simon";
        //
        //		CacheClient client = new CacheClient();
        //		client.putObject("user", u);
        //
        //		client.get("user", new ReturnCallback() {
        //
        //			@Override
        //			public void onReturn(String data) {
        //				// TODO Auto-generated method stub
        //
        //			}
        //		});
        //		System.out.println(u1.password+","+u1.username);
        //
        //		List<User> list = new ArrayList<User>();
        //		list.add(u);
        //		client.putObject("list", list);
        //
        //		List<User> u2 = client.getJSONList("list", User.class);
        //		System.out.println(u2);
    }
}
""".trimIndent(), text)
    }

    @Test
    fun testJava2() {
        val text = CodeFormatter.reformat("A.java", """
package com.yy.mobile.ui.widget.labelView;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class LabelUtils {

    public static int dipToPx(Context c, float dipValue) {
        DisplayMetrics metrics = c.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    /*public static int spToPx(Context context, float spValue) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, metrics);
    }*/

    public static int spToPx(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}
""".trimIndent())

        Assert.assertEquals("""""", text)
    }
}