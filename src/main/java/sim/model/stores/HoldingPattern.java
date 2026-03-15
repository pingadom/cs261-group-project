package sim.model.stores;

import java.time.LocalTime; 


public class HoldingPattern<E> {
    private List<E> emergency;
    private List<E> nonEmergency;

    /**
     * Holding pattern consists of two linked lists.
     * One stores emergency aircraft, one stores non-emergency aircraft.
     * The pop() function prioritises the emergency aircraft 
     */
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

    /**
     * Adds an element to the holding pattern. Depending on the priority of the element (whether it is an emergency),
     * the aircraft is added to the end of either the emergency or non emergency queue
     * 
     * @param element the list element to be added to the holding pattern
     * 
     * @return 1 on succesfully adding the element, 0 on failure
     */
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

    /**
     * Returns the first element of the emergency queue, unless the emergency queue is empty.
     * In which case it returns the first element of the non-emergency queue
     * 
     * @return the first element of the holding pattern
     */
    public LinkedListElement<E> pop(){
        if (emergency.getSize() > 0){
            return emergency.pop(0);
        }
        else{
            return nonEmergency.pop(0);
        }
    }
}
