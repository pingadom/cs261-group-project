package stores;

import java.time.LocalDate; 

/** Linked list implmentation*/

public class List<E> {
    private LinkedListElement<E> head;
    private LinkedListElement<E> tail;
    private int size;

    Public List(){
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
            return 0;
        }
        ptr = head;
        for (int i = 0; i < index;i++){
            ptr = ptr.next;
        }
        return ptr;
    }

    public int add(LinkedListElement<E> element){
        tail.next = elem;
        tail = elem;
        size++;
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
            head = head.next;
            size--
            return element;
        }
        LinkedListElement<E> element = get(index);
        LinkedListElement<E> prev = get(index - 1);
        prev.next = element.next;
        size--
        return element;
        
    }

    public int clear(){
        head = null;
        tail = null;
        size = 0;
    }

}