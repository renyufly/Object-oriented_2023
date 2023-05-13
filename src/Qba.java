public class Qba {
    private MyPerson person;

    public Qba(MyPerson person) {
        this.person = person;
    }

    public int queryBestAc() {
        int flag = 0;
        int bestid = 0;
        for (Integer keyId : person.getValue().keySet()) {
            if (flag == 0) {
                bestid = keyId;
            } else {
                if ((person.getValue().get(keyId) > person.getValue().get(bestid)) ||
                        (person.getValue().get(keyId) ==
                                person.getValue().get(bestid) && keyId < bestid)) {
                    bestid = keyId;
                }
            }
            flag++;
        }
        return bestid;
    }

}
