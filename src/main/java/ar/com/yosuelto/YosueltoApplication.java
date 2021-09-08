package ar.com.yosuelto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class YosueltoApplication {

	public static void main(String[] args) {
		SpringApplication.run(YosueltoApplication.class, args);
	}

}
