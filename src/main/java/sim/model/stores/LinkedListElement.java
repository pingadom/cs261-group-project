package sim.model.stores;

import java.time.LocalTime; 

/** aircaft storeLinked list element */

public class LinkedListElement<E>{

    private E value;
    private LinkedListElement<E> next;
    private int priority;

    public LinkedListElement(){
        value = null;
        next = null;
    }

    public E getValue(){
        return value;
    }

    public LinkedListElement<E> getNext(){
        return next;
    }

    public int getPriority(){
        return priority;
    }

    public int setValue(E _value){
        value = value;
        return 1;
    }

    public int setNext(LinkedListElement<E> _next){
        next = next;
        return 1;
    }

    public int setPriority(int _priority){
        if (_priority == 0 || _priority == 1){
            priority = _priority;
            return 1;
        }
        return 0;
    }

}