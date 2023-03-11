package academy.prog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JsonMessages {
    private final List<Message> list = new ArrayList<>(); //список куда копируем сообщения

    public JsonMessages(List<Message> sourceList, int fromIndex, String to) { //метод для копирования
        // сообщений из списка от указанного индекса и до конца (параметры: список из которого копируем, индекс - с какого
        // сообщения начинаем копировать, строка логин получателя(адрес))
        for (int i = fromIndex; i < sourceList.size(); i++) { //проходим по списку от fromIndex и до конца
            if (sourceList.get(i).getTo().equals(to)) { //если элемент списка по полученному отправителю (getTo())
                //будет = адресату, то
                list.add(sourceList.get(i)); //добавляем этот элемент в список
            }
        }
    }

    public List<Message> getList() { //метод возвращает неизменяемый(только для чтения) список суперкласса Collections
        return Collections.unmodifiableList(list);
    }
}
