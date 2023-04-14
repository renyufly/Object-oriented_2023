import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Controller implements Runnable {     //管理当前运行的所有电梯，分配人员进入对应电梯等待池[调度器]
    private HashMap<Integer, Elevator> elevators;    //管理当前 <ID - 电梯对象>
    private WaitTable waitTable;                    //主请求池

    public Controller(WaitTable waitTable) {
        this.elevators = new HashMap<>();
        this.waitTable = waitTable;
    }

    public void addElevator(int id, Elevator elevator) {
        this.elevators.put(id, elevator);
    }

    public void maintainEleva(int id) {
        this.elevators.get(id).setIsmaintain(true);
        this.elevators.remove(id);
    }

    public void deleteEleva(int id) {
        this.elevators.remove(id);
    }

    @Override
    public void run() {
        while (true) {
            synchronized (waitTable) {
                if (waitTable.isEmpty()) {
                    if (waitTable.getIsClose() && (waitTable.getHasMaintain() == 0)) {
                        int flag = 0;
                        for (Integer key : elevators.keySet()) {
                            if (!(elevators.get(key).getWaitingNumber() == 0
                                    && elevators.get(key).getLoadPerson() == 0)) {
                                flag = 1;
                            }
                        }
                        if (flag == 0) {
                            waitTable.setAllClear();
                            break;       //结束线程
                        }
                    }
                    try {
                        waitTable.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!waitTable.isEmpty()) {
                Person personRequest = waitTable.getRequest();  //从主请求池获得一个请求
                /*    对该请求规划分配     */
                ArrayList<ArrayList<Integer>> routes = new ArrayList<>();
                ArrayList<ArrayList<Integer>> paths = new ArrayList<>();
                searchRoute(personRequest, routes, paths);
                for (int i = 0; i < routes.size(); i++) {
                    if (personRequest.isUsedEle(routes.get(i))) {
                        routes.remove(i);
                        paths.remove(i);
                        i = i - 1;
                    }
                }
                ArrayList<Integer> minRoute = routes.get(0);
                ArrayList<Integer> minPath = paths.get(0);
                for (int i = 1; i < routes.size(); i++) {
                    if (((routes.get(i).size() <= minRoute.size())
                            && elevators.get(routes.get(i).get(0)).getWaitingNumber()
                            < elevators.get(minRoute.get(0)).getWaitingNumber())) {
                        minRoute = routes.get(i);
                        minPath = paths.get(i);
                    }
                }
                personRequest.setShortTermDesti(minPath.get(1));
                personRequest.addUsedEleva(elevators.get(minRoute.get(0)).getId());
                minPath.remove(0);
                elevators.get(minRoute.get(0)).setWaitingPerson(personRequest);
                synchronized (waitTable) {
                    waitTable.notifyAll();
                }
                minRoute.remove(0);
            }
        }
    }

    private void findPath(ArrayList<Integer> floorState, Elevator curEleva,
                          ArrayList<Integer> currentPath, ArrayList<Integer> currentRoute,
                          ArrayList<ArrayList<Integer>> allPaths,
                          ArrayList<ArrayList<Integer>> allRoutes,
                          HashMap<Integer, Elevator> usedEleva, ArrayList<Integer> usedFloor) {
        currentRoute.add(curEleva.getId());
        currentPath.add(floorState.get(0));
        if (!curEleva.isAccess(floorState.get(0))) {
            return;
        }
        if (curEleva.isAccess(floorState.get(1))) {
            allRoutes.add(new ArrayList<>(currentRoute));
            currentPath.add(floorState.get(1));
            allPaths.add(new ArrayList<>(currentPath));
            currentPath.remove(currentPath.size() - 1);
            return;
        }
        synchronized (waitTable) {
            usedFloor.add(floorState.get(0));
            usedEleva.put(curEleva.getId(), curEleva);
            for (int i = 1; i <= 11; i++) {
                if (!usedFloor.contains(i) && curEleva.isAccess(i)) {
                    Iterator iter = elevators.keySet().iterator();
                    while (iter.hasNext()) {
                        Integer key = (Integer) iter.next();
                        Iterator<Integer> integerIterator = usedEleva.keySet().iterator();
                        int flag1 = 0;
                        while (integerIterator.hasNext()) {
                            if (usedEleva.get(integerIterator.next()) == usedEleva.get(key)) {
                                flag1 = 1;
                                break;
                            }
                        }
                        if (flag1 == 0) {
                            floorState.set(0, i);
                            findPath(floorState, elevators.get(key), currentPath,
                                    currentRoute, allPaths, allRoutes, usedEleva, usedFloor);
                            currentRoute.remove(currentRoute.size() - 1);
                            currentPath.remove(currentPath.size() - 1);
                        }
                    }
                }
            }
        }
    }

    private void searchRoute(Person personRequest, ArrayList<ArrayList<Integer>> routes,
                             ArrayList<ArrayList<Integer>> paths) {
        for (Integer key : elevators.keySet()) {
            ArrayList<Integer> floorState = new ArrayList<>(); //0位为curFloor, 1位为destination
            int curFloor = personRequest.getFrom();
            floorState.add(curFloor);
            floorState.add(personRequest.getDesti());
            findPath(floorState, elevators.get(key),
                    new ArrayList<>(), new ArrayList<>(),
                    paths, routes, new HashMap<>(), new ArrayList<>());
        }
    }
}
