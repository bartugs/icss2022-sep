package nl.han.ica.datastructures;
import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
    private List<T> list;

    public HANStack() {
        this.list = new ArrayList<>();
    }

    @Override
    public void push(T value) {
        list.add(value); // Voeg value toe aan het einde van de list (top van de stack)
    }

    @Override
    public T pop() {
        if (list.isEmpty()) {
            throw new IllegalStateException("Stack is empty!");
        }
        return list.remove(list.size() - 1); // Verwijder en retourneer het laatste element
    }

    @Override
    public T peek() {
        if (list.isEmpty()) {
            throw new IllegalStateException("Stack is empty!");
        }
        return list.get(list.size() - 1); // Retourneer het laatste element zonder te verwijderen
    }

    // Optioneel: handig voor testen
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
