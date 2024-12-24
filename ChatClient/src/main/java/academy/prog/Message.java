package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class Message { //клас "Повідомлення"
	private Date date = new Date(); //поле класу - дата створення повідомлення
	private String from; //поле класу - відправник повідомлення
	private String to; //поле класу - отримувач повідомлення
	private String text; //поле класу - текст повідомлення

	public Message(String from, String to, String text) { //конструктор класу з параметрами (від кого, кому, вміст)
		this.from = from; //ініціалізація поля класу за аргументом створеного екземпляра (об'єкта) цього класу
		this.to = to;
		this.text = text;
	}

	public String toJSON() { //метод для перетворення об'єкта в формат JSON
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //створюємо Gson з використанням патерну
		//setDateFormat("yyyy-MM-dd HH:mm:ss"), за яким будуть серіалізуватися повідомлення
		return gson.toJson(this); //перетворюємо gson в JSON за поточним об'єктом (серіалізація)
	}

	public static Message fromJSON(String s) { //метод на вході отримує рядок, перетворює з формату JSON
		Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); //створюємо Gson з використанням патерну
		//setDateFormat("yyyy-MM-dd HH:mm:ss"), за яким будуть десеріалізуватися повідомлення
		return gson.fromJson(s, Message.class); //з рядка s десеріалізуємо об'єкт Message
	}

	@Override
	public String toString() { //формуємо формат виведення повідомлення в консоль (чат)
		return new StringBuilder().append("[").append(date)
				.append(", From: ").append(from).append(", To: ").append(to)
				.append("] ").append(text)
				.toString();
	}

	public int send(String url) throws IOException { //метод для відправлення повідомлення через HTTP
		//використовується стандартний бібліотечний клас HttpURLConnection - найпростіший HTTP-клієнт, який дозволяє
		// робити запити через HTTP
		URL obj = new URL(url); //рядок-адреса URL завертається в об'єкт URL
		HttpURLConnection conn = (HttpURLConnection) obj.openConnection(); //відкриваємо з'єднання по протоколу HTTP

		conn.setRequestMethod("POST"); //вказуємо, що це буде POST-запит
		conn.setDoOutput(true); //вказуємо, що запит буде містити дані

		try (OutputStream os = conn.getOutputStream()) { //отримуємо OutputStream від з'єднання
			String json = toJSON(); //поточне повідомлення перетворюємо у формат JSON
			os.write(json.getBytes(StandardCharsets.UTF_8)); //записуємо перетворену строку у OutputStream
			return conn.getResponseCode(); //повертаємо код відповіді (200 - OK, якщо помилка - викликається виключення)
		}
	     }
	}

	// Гетери/Сетери
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
