package jssp;

public class Machine {

    private int id;

    public Machine(int id){
        this.id = id;
    }



    public int getID(){
        return this.id;
    }

    @Override
    public String toString() {
        return String.format("Machine-%d", this.id);
    }

}
