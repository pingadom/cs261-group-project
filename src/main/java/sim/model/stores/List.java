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

    public int add(LinkedListElement<E> element) {
        if (element == null) {
            return 0;
        }

        element.setNext(null); // always detach before adding

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

    public LinkedListElement<E> pop(int index) {
        if (index < 0 || index >= size) {
            return null;
        }

        if (size == 0) {
            return null;
        }

        if (size == 1) {
            LinkedListElement<E> element = head;
            clear();
            if (element != null) {
                element.setNext(null);
            }
            return element;
        }

        if (index == 0) {
            LinkedListElement<E> element = head;
            head = head.getNext();
            element.setNext(null);   // detach popped node
            size--;
            return element;
        }

        LinkedListElement<E> prev = get(index - 1);
        LinkedListElement<E> element = prev.getNext();

        prev.setNext(element.getNext());

        if (index == size - 1) {
            tail = prev;   // update tail when removing last node
        }

        element.setNext(null);   // detach popped node
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

}