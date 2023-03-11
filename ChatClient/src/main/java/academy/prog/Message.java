package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message { //класс Сообщение
	private Date date = new Date(); //поле класса - дата создания сообщения
	private String from; //поле класса - отправитель сообщения
	private String to; //поле класса = получатель сообщения
	private String text; //поле класса - текст сообщения

	public Message(String from, String to, String text) { //конструктор класса с параметрами(от кого, кому, содержимое)
		this.from = from; //инициализация поля класса по аргументу созданного экземпляра(обьекта) данного класса
		this.to = to;
		this.text = text;
	}

	public String toJSON() { //метод преобразует к виду json-строке
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //строем Gson с применением паттерна
		//setDateFormat("yyyy-MM-dd HH:mm:ss"), по которому будут сериализоваться сообщения
		return gson.toJson(this); //преобразуем gson в json по текущему обьекту (сериализуем)
	}

	public static Message fromJSON(String s) {//метод на вход получает строку, преобразует из json'a
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //строем Gson с применением паттерна
		//setDateFormat("yyyy-MM-dd HH:mm:ss"), по которому будут десериализоваться сообщения
		return gson.fromJson(s, Message.class); //из строки s десериализуем обьект Message
	}

	@Override
	public String toString() { //формируем возвращаемый вид сообщения на консоль(чат)
		return new StringBuilder().append("[").append(date)
				.append(", From: ").append(from).append(", To: ").append(to)
				.append("] ").append(text)
				.toString();
	}

	public int send(String url) throws IOException {
		//используется стандартный библиотечный класс HttpURLConnection - самый простой HTTP-client, кот.позволяет
		// делать запросы у HTTP
		URL obj = new URL(url); //строку-адресURL заворачиваем в URL-обьект
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //открываем соединение по протоколу HTTP

		conn.setRequestMethod("POST"); //указываем что у нас будет POST-запрос
		conn.setDoOutput(true); //указываем, что запрос будет с данными

		try (OutputStream os = conn.getOutputStream()) { //у коннекшина получаем getOutputStream()
			String json = toJSON(); //текущее сообщение-toJSON() преобразуем к строке String json
			os.write(json.getBytes(StandardCharsets.UTF_8)); //преобразованную строку пишем в OutputStream os
			return conn.getResponseCode(); // 200? //возвращаем statusCode, если 200 ОК - то хорошо, если нет - то метод
			//main выдаст исключение
		}
	}

	//Геттеры/Сеттеры
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}