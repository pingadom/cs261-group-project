package sim.model.stores;

import java.time.LocalTime; 

/** Linked list implmentation*/

public class List<E> {
    private LinkedListElement<E> head;
    private LinkedListElement<E> tail;
    private int size;

    public List(){
        head = null;
        tail = null;
        size = 0;
    }

    public LinkedListElement<E> getHead(){
        return head;
    }

    public LinkedListElement<E> getTail(){
        return tail;
    }

    public int getSize(){
        return size;
        }

    public LinkedListElement<E> get(int index){
        if (index < 0 || index >= size){
            return null;
        }
        LinkedListElement<E> ptr = head;
        for (int i = 0; i < index; i++){
            if (ptr == null) return null;
            ptr = ptr.getNext();
        }
        return ptr;
    }

    public int add(LinkedListElement<E> element){
        // added check for empty
        if (size == 0) {
            head = element;
            tail = element;
            size = 1;
            return 1;
        }
        tail.setNext(element);
        tail = element;
        size++;
        return 1;
    }

    public LinkedListElement<E> pop(int index){
        if (size == 1){
            LinkedListElement<E> element = head;
            clear();
            return element;
        }
        if (size == 0){
            return null;
        }
        if (index == 0){
            LinkedListElement<E> element = head;
            head = head.getNext();
            size--;
            return element;
        }
        LinkedListElement<E> element = get(index);
        LinkedListElement<E> prev = get(index - 1);
        prev.setNext(element.getNext());

        // Update the tail if removing last element
        if (index == size - 1) {
            tail = prev;
        }

        size--;
        return element;
        
    }

    public int clear(){
        head = null;
        tail = null;
        size = 0;
        return 1;
    }

    public int addValue(E value) {
      LinkedListElement<E> element = new LinkedListElement<>();
      element.setValue(value);
      return add(element);
    }
    // Calculates the average wait time of all aircraft. Should only be run on a list where all aircraft have arrived/departed.
    // This will produce innacurate results if some aircraft in the list are canceled/diverted/not yet arrived/not yet departed
    public double getAverageWait(List<Aircraft> list){
        double total = 0;
        int num = list.getSize();
        LinkedListElement<Aircraft> ptr = list.getHead();
        while (ptr != null){
            total += (ptr.getValue().getRealTime() - ptr.getValue().getTime());
            ptr = ptr.getNext();
        }
        if (total > 0){
            return (total / num);
        }
        return 0;
    }

}