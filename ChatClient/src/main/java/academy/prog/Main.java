package academy.prog;

import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) { //главный метод
        String to = "All"; //переменная-буфер для разделения считанной строки с клавиатуры от клиента, которая будет хранить
        //в себе адрес получателя сообщения, "адресу" назначаем литерал "All"-для всех - по умолчанию
        String text; //переменная-буфер для разделения считанной строки с клавиатуры от клиента, которая будет хранить
        //в себе текст для получателя сообщения

        try (Scanner scanner = new Scanner(System.in)) { //в блоке try-with-resource читаем с консоли данные
            System.out.println("Enter your login: "); //литерал-коментарий для дальнейших действий клиенту
            String login = scanner.nextLine(); //читаем в строку введенные клиентом данные - его логин/имя

            Thread th = new Thread(new GetThread(login)); //запускаем фоновый Thread() который периодически обращается
            //к серверу и спрашивает - а есть ли пользователи онлайн. При наличии других участников - выводит всех их.
            th.setDaemon(true); //устанавливаем флаг на данный поток, который автоматически уничтожится по завершению main
            th.start(); //запускаем поток

            //литералы-коментариы для дальнейших действий клиенту
            System.out.println("Enter your message: ");
            System.out.println("	or");
            System.out.println("Enter your message for user-addressee: (example format:  @user'sLogin-messageText)");
            System.out.println(" 	or");
            System.out.println("Enter /allUsers: ");

            while (true) { //цикл для ввода сообщения, работает пока не ввели пустое сообщение или не возникла ошибка
                String to_text = scanner.nextLine(); //в переменную(которая в процессе будет делиться на две части) читаем
                //данные введенные с клавитуры клиентом
                if (to_text.isEmpty()) break; //если клиент нажмет Enter (пустая строка), то завершить программу
                // users
                if (to_text.equals("/allUsers")) { //если клиент введет литерал для отображения всех клиентов, то
                    new GetThread(login).getAllClientsList(); //для статического метода создаем экземпляр нестатического класса и
                    //вызываем его метод - получитьСписокВсехКлиентов()
                    continue; //блокируем дублирование послднего сообщения от клиента при вызове им списка всех Клиентов
                // @test Hello
                } if (to_text.startsWith("@")) { //если введенная строка клиентом будет начинаться с символа "@"
                    if (to_text.contains("-")) { //и если будет содержать тире, то делим строку на две части - "адрес" и "сообщение"
                        to = to_text.substring(to_text.indexOf("@") + 1, to_text.indexOf("-")); //обрезаем строку - оставляем
                        // "адрес" - от второго символа и до пробела
                        text = to_text.substring(to_text.indexOf("-") + 1); //обрезаем строку - оставляем "сообщение" -
                        //от первого символа после тире и до конца
                    } else { //иначе
                        text = to_text; //вся введенная строка является "сообщением"
                    }
                } else { //иначе
                    text = to_text; //вся введенная строка является "сообщением"
                }

                Message m = new Message(login, to, text); //создаем сообщение согласно конструктора класса Message - обьект
                // new Message(указываем login, кому пишем to и текст сообщения text)
                int res = m.send(Utils.getURL() + "/add"); //по протоколу http отправляем сообщение на сервер - берем
                //адрес URL и добавляем andPoint /add

                if (res != 200) { //если к нам вернулась какая-то другая ошибка кроме 200 OK, то
                    System.out.println("HTTP error occurred: " + res); ////выводим сообщение - у нас проблема + код проблемы
                    return; //и прерываемся
                }
            }
        } catch (IOException ex) { //ловим исключение класса IOException (входящие/исходящие потоки)
            ex.printStackTrace(); //выводим стэк исключения - причина и место возникновения
        }
    }
}
