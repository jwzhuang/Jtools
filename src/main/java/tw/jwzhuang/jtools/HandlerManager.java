package tw.jwzhuang.jtools;

import org.greenrobot.eventbus.EventBus;

import java.util.LinkedList;
import java.util.List;

public class HandlerManager implements HandlerWatcherListener {
    private static final HandlerManager ourInstance = new HandlerManager();
    private EventBus eventBus = EventBus.builder().build();
    public static HandlerManager shared() {
        return ourInstance;
    }

    private List<HandlerWatcher> watchers;

    private HandlerManager() {
        watchers = new LinkedList<>();
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
        return makeWatcher().runTask(task, secs * 1000);
    }

    private HandlerWatcher makeWatcher(){
        HandlerWatcher watcher = HandlerWatcher.builder(this.eventBus).setListener(this);
        this.watchers.add(watcher);
        return watcher;
    }

    //<editor-fold desc="HandlerWatcherListener">
    @Override
    public void complete(HandlerWatcher watcher) {
        synchronized (this.watchers){
            System.out.println("zzzz: " + this.watchers.size());
            this.watchers.remove(watcher);
            System.out.println("zzzz: " + this.watchers.size());
        }
    }
    //</editor-fold>
}

