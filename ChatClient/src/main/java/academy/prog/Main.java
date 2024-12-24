package academy.prog;

import java.io.IOException;
import java.util.*;


public class Main {
    public static void main(String[] args) { // головний метод
        String to = "All"; // змінна-буфер для розділення введеної з клавіатури строки від клієнта, яка буде зберігати
        // адресу отримувача повідомлення, "адресу" призначаємо літерал "All" - для всіх за замовчуванням
        String text; // змінна-буфер для розділення введеної з клавіатури строки від клієнта, яка буде зберігати
        // текст для отримувача повідомлення

        try (Scanner scanner = new Scanner(System.in)) { // в блоці try-with-resource читаємо з консолі дані
            System.out.println("Enter your login: "); // літерал-коментар для подальших дій клієнту
            String login = scanner.nextLine(); // читаємо в рядок введені клієнтом дані - його логін/ім'я

            Thread th = new Thread(new GetThread(login)); // запускаємо фоновий потік, який періодично звертається
            // до сервера і запитує - чи є користувачі онлайн. При наявності інших учасників - виводить всіх їх.
            th.setDaemon(true); // встановлюємо прапор на цей потік, який автоматично знищиться після завершення main
            th.start(); // запускаємо потік

            // літерали-коментарі для подальших дій клієнту
            System.out.println("Enter your message: ");
            System.out.println("	or");
            System.out.println("Enter your message for user-addressee: (example format:  @user'sLogin-messageText)");
            System.out.println(" 	or");
            System.out.println("Enter /allUsers: ");

            while (true) { // цикл для введення повідомлення, працює поки не введено порожнє повідомлення або не виникла помилка
                String to_text = scanner.nextLine(); // в змінну (яка в процесі буде ділитися на дві частини) читаємо
                // дані введені з клавіатури клієнтом
                if (to_text.isEmpty()) break; // якщо клієнт натисне Enter (порожній рядок), то завершуємо програму
                // users
                if (to_text.equals("/allUsers")) { // якщо клієнт введе літерал для відображення всіх клієнтів, то
                    new GetThread(login).getAllClientsList(); // для статичного методу створюємо екземпляр нестатичного класу і
                    // викликаємо його метод - отриматиСписокВсіхКлієнтів()
                    continue; // блокуємо дублювання останнього повідомлення від клієнта при виклику ним списку всіх Клієнтів
                // @test Hello
                } if (to_text.startsWith("@")) { // якщо введена стрічка клієнтом починається з символа "@"
                    if (to_text.contains("-")) { // і якщо буде містити тире, то ділимо стрічку на дві частини - "адреса" і "повідомлення"
                        to = to_text.substring(to_text.indexOf("@") + 1, to_text.indexOf("-")); // обрізаємо стрічку - залишаємо
                        // "адресу" - від другого символу і до пробілу
                        text = to_text.substring(to_text.indexOf("-") + 1); // обрізаємо стрічку - залишаємо "повідомлення" -
                        // від першого символу після тире і до кінця
                    } else { // інакше
                        text = to_text; // вся введена стрічка є "повідомленням"
                    }
                } else { // інакше
                    text = to_text; // вся введена стрічка є "повідомленням"
                }

                Message m = new Message(login, to, text); // створюємо повідомлення згідно конструктора класу Message - об'єкт
                // new Message(вказуємо login, кому пишемо to і текст повідомлення text)
                int res = m.send(Utils.getURL() + "/add"); // за протоколом http відправляємо повідомлення на сервер - беремо
                // адресу URL і додаємо endpoint /add

                if (res != 200) { // якщо повернулась якась інша помилка, окрім 200 OK, то
                    System.out.println("HTTP error occurred: " + res); // виводимо повідомлення - у нас проблема + код помилки
                    return; // і припиняємо виконання
                }
            }
        } catch (IOException ex) { // ловимо виключення класу IOException (вхідні/вихідні потоки)
            ex.printStackTrace(); // виводимо стек виключення - причина і місце виникнення
        }
    }
}

