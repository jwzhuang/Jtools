package tw.jwzhuang.jtools;

/**
 * Created by jwzhuang on 2017/3/10.
 */

class HandlerEvent {
    public static class Resume{
        public static Resume build(){
            return new Resume();
        }
    }

    public static class Pause{
        public static Pause build(){
            return new Pause();
        }
    }
}
