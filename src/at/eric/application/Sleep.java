package at.eric.application;

public class Sleep {
    public static void delaySeconds(int time){
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void delayMinutes(int time){
        try {
            Thread.sleep(time * 60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
