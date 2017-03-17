package tw.jwzhuang.jtools;

import org.greenrobot.eventbus.EventBus;

import java.util.Hashtable;
import java.util.Map;

public class HandlerManager implements HandlerWatcherListener {
    private static final HandlerManager ourInstance = new HandlerManager();
    private EventBus eventBus = EventBus.builder().build();
    public static HandlerManager shared() {
        return ourInstance;
    }

    private Map<String, HandlerWatcher> watchers;

    private HandlerManager() {
        watchers = new Hashtable<>();
    }

    public void resume() {
        if (watchers.size() > 0) {
            this.eventBus.post(HandlerEvent.Resume.build());
        }
    }

    public void pause() {
        if (watchers.size() > 0) {
            this.eventBus.post(HandlerEvent.Pause.build());
        }
    }

//    public void makeHandler(HandlerTask task){
//        HandlerWatcher watcher = makeWatcher()
//                .runTask(task);
//    }

    public HandlerWatcher makeHandler(HandlerTask task, int secs){
        return makeHandler(task, secs, "Default");
    }

    public HandlerWatcher makeHandler(HandlerTask task, int secs, String token){
        return makeWatcher(token).runTask(task, secs * 1000);
    }

    private HandlerWatcher makeWatcher(String token){

        if (watchers.containsKey(token)){
            HandlerWatcher watcher = watchers.get(token);
            watcher.destroy();
        }

        HandlerWatcher watcher = HandlerWatcher.builder(this.eventBus).setListener(this);
        this.watchers.put(token, watcher);
        return watcher;
    }

    //<editor-fold desc="HandlerWatcherListener">
    @Override
    public void complete(HandlerWatcher watcher) {
        synchronized (this.watchers){
            this.watchers.remove(watcher);
        }
    }
    //</editor-fold>
}

