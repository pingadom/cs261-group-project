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
        if (size <= index && index >= 0){
            return null;
        }
        LinkedListElement<E> ptr = head;
        for (int i = 0; i < index;i++){
            ptr = ptr.getNext();
        }
        return ptr;
    }

    public int add(LinkedListElement<E> element){
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
        size--;
        return element;
        
    }

    public int clear(){
        head = null;
        tail = null;
        size = 0;
        return 1;
    }

    public double getAverageWait(List<Aircraft> list){
        double total = 0;
        int num = list.getSize();
        LinkedListElement<Aircraft> ptr = list.getHead();
        while (ptr != null){
            total += ptr.getValue().getRealTime();
            ptr = ptr.getNext();
        }
        if (total > 0){
            return (total / num);
        }
        return 0;
    }

}