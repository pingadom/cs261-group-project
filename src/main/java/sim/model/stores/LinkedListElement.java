package sim.model.stores;

import java.time.LocalTime; 


public class LinkedListElement<E> {

    private E value;
    private LinkedListElement<E> next;
    private int priority;

    public LinkedListElement() {
        value = null;
        next = null;
        priority = 0;
    }

    public E getValue() {
        return value;
    }

    public LinkedListElement<E> getNext() {
        return next;
    }

    public int getPriority() {
        return priority;
    }

    public int setValue(E _value) {
        value = _value;     
        return 1;
    }

    public int setNext(LinkedListElement<E> _next) {
        next = _next;      
        return 1;
    }

    public int setPriority(int _priority) {
        // probably want more than 0/1 later, but keep it for now
        if (_priority == 0 || _priority == 1) {
            priority = _priority;
            return 1;
        }
        return 0;
    }
}