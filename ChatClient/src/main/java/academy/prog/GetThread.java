package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GetThread implements Runnable {
    private final Gson gson; // створюємо приватне незмінне посилання на об'єкт бібліотеки Gson
    private int n; // /get?from=n - приватне поле-лічильник (ідентифікатор номера повідомлення)
    private String userNameOrLogin; // створюємо приватне поле, в якому зберігається ім'я/логін Клієнта
    private static Set<String> allUsersList = new HashSet<>(); // створюємо приватний список для всіх користувачів

    public GetThread(String userNameOrLogin) { // конструктор класу з параметром: змінна ім'я/логін клієнта
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); // ініціалізація посилання на бібліотеку Gson,
        // з використанням патерну для встановлення формату дати
        this.userNameOrLogin = userNameOrLogin; // ініціалізуємо поле класу за аргументом нового об'єкта цього класу
    }

    @Override
    public void run() { // WebSockets - протокол зв'язку для обміну даними (повідомленнями) між веб-сервером та браузером
        // в режимі реального часу з підтримкою безперервного з'єднання (з'єднання має бути стабільним)
        try { // блок try-catch
            while (!Thread.interrupted()) { // циклом перевіряємо, щоб потік не був перерваний
                URL url = new URL(Utils.getURL() + "/get?from=" + n); // формуємо адресу URL з урахуванням номера повідомлення
                HttpURLConnection http = (HttpURLConnection) url.openConnection(); // налаштовуємо з'єднання (інакше буде
                // прирівнюватися до порожнього (без тіла) GET-запиту

                try (InputStream is = http.getInputStream()) { // в блоці try-with-resource через потік вводу даних отримуємо
                    // тіло відповіді від сервера (в цьому стрімі очікуємо Json з отриманими повідомленнями)
                    byte[] buf = responseBodyToArray(is); // в буфер (байтовий масив) з стріму зчитуємо дані за допомогою
                    // спеціального методу-конвертера
                    String strBuf = new String(buf, StandardCharsets.UTF_8); // зчитане з стріму в буфер перетворюємо
                    // в рядок за стандартом StandardCharsets.UTF_8 (восьмибітний формат перетворення UCS)

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class); // з перетвореного рядка десеріалізуємо
                    // в JsonMessages list
                    if (list != null) { // якщо вдалося десеріалізувати, тобто JsonMessages list не порожній, то
                        for (Message m : list.getList()) { // проходимося по кожному повідомленню з незмінного списку
                            // JsonMessages list, поміченого(Collections.unmodifiableList(list))
                            if (m.getFrom().equals(userNameOrLogin)) { // якщо відправник повідомлення відповідає логіну, то
                                System.out.println(m); // виводимо повідомлення
                                n++; // і збільшуємо лічильник повідомлень, щоб повідомлення виводилося лише один раз
                            } else if (m.getTo().equals(userNameOrLogin)) { // якщо отримувач повідомлення відповідає логіну, то
                                System.out.println(m); // виводимо повідомлення
                                n++; // і збільшуємо лічильник повідомлень, щоб повідомлення виводилося лише один раз
                            } else if (m.getTo().equals("All")) { // якщо отримувач повідомлення відповідає літералу, то
                                System.out.println(m); // виводимо повідомлення
                                n++; // і збільшуємо лічильник повідомлень, щоб повідомлення виводилося лише один раз
                            } else { // інакше
                                n++; // збільшуємо лічильник
                            }
                        }
                    }
                }

                Thread.sleep(500); // засинаємо потік на 0,5 сек
            }
        } catch (Exception ex) { // ловимо виняток класу Exception
            ex.printStackTrace(); // якщо зловили - виводимо стектрейс - причина і де сталося
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException { // спеціальний метод для перетворення даних
        // з потоку (InputStream is) в байтовий масив
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // створюємо потік для виведення даних з великим допустимим об'ємом
        byte[] buf = new byte[10240]; // створюємо буфер - байтовий масив з певним розміром (10 кілобайт)
        int r; // ціле число для зберігання прочитаних байтів за допомогою методу read()

        do { // запускаємо цикл, який виконається хоча б один раз
            r = is.read(buf); // з InputStream is читаємо в буфер стільки, скільки поміститься (read передає в r скільки
            // прочитав байтів)
            if (r > 0)
                bos.write(buf, 0, r); // якщо було що-небудь прочитано, то записуємо це в потік виведення ByteArrayOutputStream bos
        } while (r != -1); // поки потік не закінчиться (тобто є дані для читання) - це умова виходу з циклу

        return bos.toByteArray(); // після чого повертаємо все, що накопичилося в ByteArrayOutputStream bos, перетворивши все
        // в масив байтів
    }

    public void getAllClientsList() throws IOException {
        int firstMessage = 0; // змінна для першого повідомлення
        URL url = new URL(Utils.getURL() + "/get?from=" + firstMessage); // формуємо адресу URL з урахуванням лише першого повідомлення
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(); // налаштовуємо з'єднання (інакше буде
        // прирівнюватися до порожнього (без тіла) GET-запиту

        try (InputStream inputStream = httpConn.getInputStream()) { // в блоці try-with-resource через потік вводу даних отримуємо
            // тіло відповіді від сервера
            byte[] buffer = responseBodyToArray(inputStream); // в буфер (байтовий масив) з потоку зчитуємо дані, за допомогою
            // спеціального методу-конвертера
            String strBuffer = new String(buffer, StandardCharsets.UTF_8); // зчитане з потоку в буфер перетворюємо
            // в рядок за стандартом StandardCharsets.UTF_8 (восьмибітний формат перетворення UCS)

            JsonMessages list = gson.fromJson(strBuffer, JsonMessages.class); // з перетвореного рядка десеріалізуємо
            // в JsonMessages list
            if (list != null) { // якщо вдалося десеріалізувати, тобто JsonMessages list не порожній, то
                for (Message m : list.getList()) { // проходимося по кожному повідомленню з незмінного списку
                    // JsonMessages list, поміченого(Collections.unmodifiableList(list))
                    allUsersList.add(m.getFrom()); // в загальний список всіх клієнтів додаємо логін відправника повідомлення,
                    // витягнутого за допомогою геттера
                }
            }
        } finally {
            allUsersList.add(userNameOrLogin);
            System.out.println("Всі користувачі: ");
            for (String user : allUsersList) {
                System.out.println("@" + user);
            }
        }
    }
}

