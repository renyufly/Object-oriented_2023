import com.oocourse.elevator3.TimableOutput;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) throws Exception {
        TimableOutput.initStartTimestamp();  // 初始化时间戳
        WaitTable waitTable = new WaitTable();
        Controller controller = new Controller(waitTable);
        HashMap<Integer, Thread> elevatorThread = new HashMap<>();
        // 创建所有电梯线程，共用同一个候乘表(请求池)对象
        for (int i = 1; i <= 6; i++) {
            Elevator eleva = new Elevator(i, waitTable);
            controller.addElevator(i, eleva);      //把电梯对象存起来
            elevatorThread.put(i, new Thread(eleva));
        }
        InputThread inputThread = new InputThread(waitTable, controller);
        Thread startThread = new Thread(inputThread);
        startThread.start();
        Thread controlThread = new Thread(controller);
        controlThread.start();
        // 所有电梯线程启动
        for (int i = 1; i <= 6; i++) {
            elevatorThread.get(i).start();
        }
    }
}
