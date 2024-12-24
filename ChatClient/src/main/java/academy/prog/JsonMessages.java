package academy.prog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMessages {
    private final List<Message> list = new ArrayList<>(); //список, в який копіюємо повідомлення

    public JsonMessages(List<Message> sourceList, int fromIndex, String to) { //метод для копіювання
        // повідомлень зі списку, починаючи з вказаного індексу і до кінця (параметри: список, з якого копіюємо, індекс - з якого
        // повідомлення починаємо копіювати, рядок логіну отримувача (адреса))
        for (int i = fromIndex; i < sourceList.size(); i++) { //проходимо по списку з fromIndex до кінця
            if (sourceList.get(i).getTo().equals(to)) { //якщо елемент списку за отримувачем (getTo())
                //буде = адресату, то
                list.add(sourceList.get(i)); //додаємо цей елемент до списку
            }
        }
    }

    public List<Message> getList() { //метод повертає незмінний (тільки для читання) список суперкласу Collections
        return Collections.unmodifiableList(list);
    }
}
