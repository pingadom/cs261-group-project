package sim.model.stores;

import java.time.LocalTime; 


public class HoldingPattern<E> {
    private List<E> emergency;
    private List<E> nonEmergency;

    public HoldingPattern() {
        emergency = new List<>();
        nonEmergency = new List<>();
    }

    public List<E> getEmergency(){
        return emergency;
    }

    public List<E> getNonEmergency(){
        return nonEmergency;
    }

    public int getSize(){
        return emergency.getSize() + nonEmergency.getSize();
    }

    public int add(LinkedListElement<E> element){
        if (element.getPriority() == 0){
            nonEmergency.add(element);
            return 1;
        }
        else if(element.getPriority() == 1){
            emergency.add(element);
            return 1;
        }
        else{
            return 0;
        }
    }

    public LinkedListElement<E> pop(){
        if (emergency.getSize() > 0){
            return emergency.pop(0);
        }
        else{
            return nonEmergency.pop(0);
        }
    }
}
