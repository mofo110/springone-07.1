package io.pivotal.training.greeting;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Component
public class FortuneServiceClient {
	private RestTemplate restTemplate;
	private LoadBalancerClient loadBalancerClient;

	private final Logger logger = LoggerFactory
			.getLogger(FortuneServiceClient.class);

	public FortuneServiceClient(RestTemplate restTemplate,
			LoadBalancerClient loadBalancerClient) {
		this.restTemplate = restTemplate;
		this.loadBalancerClient = loadBalancerClient;
	}

	@HystrixCommand(fallbackMethod = "defaultFortune")
	public String getFortune() {
		String baseUrl = lookupUrlFor("FORTUNE");
		@SuppressWarnings("unchecked")
		Map<String, String> result = restTemplate.getForObject(baseUrl,
				Map.class);
		String fortune = result.get("fortune");
		logger.info("received fortune '{}'", fortune);
		return fortune;
	}

	private String lookupUrlFor(String appName) {
		ServiceInstance instance = loadBalancerClient.choose(appName);
		return String.format("http://%s:%s", instance.getHost(),
				instance.getPort());
	}

	public String defaultFortune() {
		logger.info("Default fortune used.");
		return "Your future is uncertain";
	}
}