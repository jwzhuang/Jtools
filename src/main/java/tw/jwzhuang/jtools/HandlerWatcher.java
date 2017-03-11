package tw.jwzhuang.jtools;

import android.support.annotation.NonNull;

import com.badoo.mobile.util.WeakHandler;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Calendar;

/**
 * Created by jwzhuang on 2017/3/10.
 */

interface HandlerWatcherListener{
    void complete(HandlerWatcher watcher);
}

public class HandlerWatcher {

    private TimeWatcher timeWatcher = null;
    private HandlerTask task = null;
    private EventBus eventBus = null;
    private WeakReference<HandlerWatcherListener> listener = null;

    private HandlerWatcher (EventBus eventBus){
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    public static HandlerWatcher builder(@NonNull EventBus eventBus){
        return new HandlerWatcher(eventBus);
    }

    public HandlerWatcher setListener(HandlerWatcherListener listener){
        this.listener = new WeakReference<HandlerWatcherListener>(listener);
        return this;
    }

    public HandlerWatcher runTask(HandlerTask task){
        setup(task);
        this.timeWatcher = new TimeWatcher().setup();
        this.handler.post(makeRunnable());
        return this;
    }

    public HandlerWatcher runTask(HandlerTask task, long millis){
        setup(task);
        this.timeWatcher = new TimeWatcher().setup(millis);
        this.handler.postDelayed(makeRunnable(), millis);
        return this;
    }

    public void destroy(){
        reset();
    }

    private void setup(HandlerTask task) {
        this.task = task;
        this.handler = new WeakHandler();
    }

    private void reset(){
        handler.removeCallbacksAndMessages(null);
        eventBus.unregister(this);
        this.timeWatcher = null;
        task = null;
        if (listener != null){
            listener.get().complete(this);
            listener = null;
        }
    }

    private Runnable makeRunnable(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                task.complete();
                reset();
            }
        };
        return runnable;
    }

    private WeakHandler handler;

    private class TimeWatcher{
        private long startMillis = -1;
        private long endMillis = -1;

        private TimeWatcher(){
            update();
        }

        private TimeWatcher setup(){
            this.endMillis = startMillis;
            return this;
        }

        private TimeWatcher setup(long duration){
            this.endMillis = startMillis + duration;
            return this;
        }

        private void update(){
            this.startMillis = Calendar.getInstance().getTimeInMillis();
        }

        private boolean moreDuration(){
            return this.endMillis > this.startMillis;
        }

        private long duration(){
            return this.endMillis - this.startMillis;
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onHandlerEvent(@NonNull HandlerEvent.Resume event){
        if (timeWatcher.moreDuration()){
            this.handler.postDelayed(makeRunnable(), timeWatcher.duration());
            return;
        }
        this.handler.post(makeRunnable());
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onHandlerEvent(@NonNull HandlerEvent.Pause event){
        handler.removeCallbacksAndMessages(null);
        this.timeWatcher.update();
    }
}
