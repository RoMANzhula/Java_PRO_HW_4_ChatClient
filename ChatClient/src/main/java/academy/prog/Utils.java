package academy.prog;
/*
    /add - POST(json) -> list
    /get?from=x - GET(json[])
 */

public class Utils {
    private static final String URL = "http://127.0.0.1"; //пишем localHost(адрес) - IP для запуска сервера
    private static final int PORT = 8080; //и порт, на котором сидит сервер

    public static String getURL() { //метод для конкатенации URL and PORT - т.е. получаем полный URL
        return URL + ":" + PORT;
    } //с апомощью конкатенации возвращаем URL-адрес
}
