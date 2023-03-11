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
    private final Gson gson; //создаем приватную неизменяемую ссылку на обьект библиотеки Gson
    private int n; // /get?from=n - приватное поле-счетчик (идентификатор номера сообщения)
    private String userNameOrLogin; //создаем приватное поле, в котором хранится имя/логин Клиента
    private static Set<String> allUsersList = new HashSet<>(); //создаем приватный список для всех пользователей

    public GetThread(String userNameOrLogin) { //конструктор класса с параметром: переменная имя/логин клмента
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //инициализация ссылки на библиотеку Gson,
        //с применением паттерна по установке даты
        this.userNameOrLogin = userNameOrLogin; //инициализируем поле класса по аргументу нового обьекта данного класса
    }

    @Override
    public void run() { // WebSockets - протокол связи для обмена данными(сообщениями) между веб-сервером и браузером
        //в режиме настоящего времени с поддержкой неразрывной связи(соединение должно быть стабильным)
        try { //блок try-catch
            while (!Thread.interrupted()) { //циклом проверяем, чтоб поток не был прерван
                URL url = new URL(Utils.getURL() + "/get?from=" + n); //формируем адрес URL с учетом номера сообщения
                HttpURLConnection http = (HttpURLConnection) url.openConnection(); //настраиваем соединение (иначе будет
                // приравниваться пустому(без тела) GET-запросу

                try (InputStream is = http.getInputStream()) { //в блоке try-with-resource через поток ввода данных получаем
                    //тело ответа от сервера (в этом стриме ожидаем Json с полученными сообщениями)
                    byte[] buf = responseBodyToArray(is); //в буфер(байтовый массив) из стрима считываем данные, с помощью
                    //специального метода-конвертера
                    String strBuf = new String(buf, StandardCharsets.UTF_8); //считанное из стрима в буфер преобразовываем
                    //в строку по стандарту StandardCharsets.UTF_8 (восьмиразрядный формат преобразования UCS)

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class); //из преобразованной строки десериализуем
                    //в JsonMessages list
                    if (list != null) { //если получилось десериализовать, т.е. JsonMessages list не пустой, то
                        for (Message m : list.getList()) { //проходимся по каждому сообщению из неизменяемого списка
                            // JsonMessages list помеченного(Collections.unmodifiableList(list))
                            if (m.getFrom().equals(userNameOrLogin)) { //если отправитель сообщения соответствует логину,то
                                System.out.println(m); //печатаем сообщение
                                n++;//и увеличиваем счетчик сообщений, чтоб сообщение выводилось один раз
                            } else if (m.getTo().equals(userNameOrLogin)) { //если получатель сообщения соответствует логину, то
                                System.out.println(m); //печатаем сообщение
                                n++;//и увеличиваем счетчик сообщений, чтоб сообщение выводилось один раз
                            } else if (m.getTo().equals("All")) { //если получатель сообщения соответствует литералу, то
                                System.out.println(m); //печатаем сообщение
                                n++;//и увеличиваем счетчик сообщений, чтоб сообщение выводилось один раз
                            } else { //иначе
                                n++; //увеличиваем счетчик
                            }
                        }
                    }
                }

                Thread.sleep(500); //усыпляем поток на 0,5 сек
            }
        } catch (Exception ex) { //ловим исключение класса Exception
            ex.printStackTrace(); //если поймали - то печатаем стэк - причина и в каком месте
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException { //специальный метод по преобразованию данных
        //из стрима (InputStream is) в байтовый массив
        ByteArrayOutputStream bos = new ByteArrayOutputStream(); //создаем стрим для вывода данных с большим допустимым обьемом
        byte[] buf = new byte[10240]; //создаем буфер - байтовый массив с определенным размером (10 килоБайт)
        int r; //целочисленная переменная для хранения прочитанных байтов с помощью метода read()

        do { //запускаем цикл, который выполнится хотябы один раз
            r = is.read(buf); //из InputStream is читаем в буфер столько, сколько поместиться (read передает в r сколько
            // прочел байтов)
            if (r > 0)
                bos.write(buf, 0, r); //если было что-то прочитано, то записываем это в поток вывода ByteArrayOutputStream bos
        } while (r != -1); //пока стрим не закончится(т.е. есть данные для чтения) - это условие выхода из цикла

        return bos.toByteArray(); //после чего возвращаем все, что накопилось в ByteArrayOutputStream bos, преобразовав все
        //в массив байтов
    }

    public void getAllClientsList() throws IOException {
        int firstMessage = 0; //переменная для первого сообщения
        URL url = new URL(Utils.getURL() + "/get?from=" + firstMessage); //формируем адрес URL с учетом только первого сообщения
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection(); //настраиваем соединение (иначе будет
        // приравниваться пустому(без тела) GET-запросу

        try (InputStream inputStream = httpConn.getInputStream()) { //в блоке try-with-resource через поток ввода данных получаем
            //тело ответа от сервера
            byte[] buffer = responseBodyToArray(inputStream); //в буфер(байтовый массив) из стрима считываем данные, с помощью
            //специального метода-конвертера
            String strBuffer = new String(buffer, StandardCharsets.UTF_8); //считанное из стрима в буфер преобразовываем
            //в строку по стандарту StandardCharsets.UTF_8 (восьмиразрядный формат преобразования UCS)

            JsonMessages list = gson.fromJson(strBuffer, JsonMessages.class); //из преобразованной строки десериализуем
            //в JsonMessages list
            if (list != null) { //если получилось десериализовать, т.е. JsonMessages list не пустой, то
                for (Message m : list.getList()) { //проходимся по каждому сообщению из неизменяемого списка
                    // JsonMessages list помеченного(Collections.unmodifiableList(list))
                    allUsersList.add(m.getFrom()); //в общий список всех клиентов добавляем логин отправителя сообщени,
                    //вытащенный с помощью Геттера
                }
            }
        } finally {
            allUsersList.add(userNameOrLogin);
            System.out.println("All users: ");
            for (String user : allUsersList) {
                System.out.println("@" + user);
            }
        }
    }
}
