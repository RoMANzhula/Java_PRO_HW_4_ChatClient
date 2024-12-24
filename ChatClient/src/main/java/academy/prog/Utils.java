package academy.prog;
/*
    /add - POST(json) -> list
    /get?from=x - GET(json[])
 */

public class Utils {
    private static final String URL = "http://127.0.0.1"; //вказуємо localHost (адреса) - IP для запуску сервера
    private static final int PORT = 8080; //та порт, на якому працює сервер

    public static String getURL() { //метод для конкатенації URL та PORT - тобто отримуємо повний URL
        return URL + ":" + PORT; //за допомогою конкатенації повертаємо URL-адресу
    }
}
