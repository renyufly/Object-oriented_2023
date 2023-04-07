import com.oocourse.elevator3.ElevatorInput;
import com.oocourse.elevator3.ElevatorRequest;
import com.oocourse.elevator3.MaintainRequest;
import com.oocourse.elevator3.PersonRequest;
import com.oocourse.elevator3.Request;

import java.io.IOException;
import java.util.ArrayList;

public class InputThread implements Runnable {
    private WaitTable waitTable;
    private Controller controller;

    public InputThread(WaitTable waitTable, Controller controller1) {
        this.waitTable = waitTable;       //共享请求池对象
        this.controller = controller1;
    }

    @Override
    public void run() {
        ElevatorInput elevatorInput = new ElevatorInput(System.in);
        while (true) {
            Request request = elevatorInput.nextRequest();
            if (request == null) {
                break;
            } else {      // a new valid request
                /*     */
                if (request instanceof PersonRequest) {   // a PersonRequest
                    synchronized (waitTable) {
                        PersonRequest request1 = (PersonRequest) request;
                        Person pr = new Person(request1, request1.getToFloor(), new ArrayList<>());
                        waitTable.addRequest(pr);
                        waitTable.notifyAll();
                    }
                } else if (request instanceof ElevatorRequest) {   // an ElevatorRequest
                    ElevatorRequest req = (ElevatorRequest) request;
                    Elevator elevator = new Elevator(req.getElevatorId(), waitTable,
                            req.getFloor(), req.getCapacity(), req.getSpeed(), req.getAccess());
                    controller.addElevator(elevator.getId(), elevator);
                    Thread ethread = new Thread(elevator);
                    ethread.start();
                } else if (request instanceof MaintainRequest) {   // an MaintainRequest
                    controller.maintainEleva(((MaintainRequest) request).getElevatorId());
                    synchronized (waitTable) {
                        waitTable.notifyAll();
                        waitTable.setHasMaintain(0);
                    }

                }
            }
        }
        try {
            elevatorInput.close();    //结束输入
            synchronized (waitTable) {
                waitTable.setClose();
                waitTable.notifyAll();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
