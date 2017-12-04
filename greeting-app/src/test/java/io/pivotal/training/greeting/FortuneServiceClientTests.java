package io.pivotal.training.greeting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE)
@AutoConfigureStubRunner(workOffline = true, ids = "io.pivotal.training.springcloud:fortune-service:+:stubs:8081")
public class FortuneServiceClientTests {

	@Autowired
	private FortuneServiceClient fortuneServiceClient;

	@MockBean
	LoadBalancerClient loadBalancerClient;
	@Mock
	ServiceInstance serviceInstance;

	private static final String ExpectedFortune = "a random fortune";

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(FortuneServiceClientTests.class);
		when(serviceInstance.getHost()).thenReturn("localhost");
		when(serviceInstance.getPort()).thenReturn(8081);
		when(loadBalancerClient.choose(anyString()))
				.thenReturn(serviceInstance);
	}

	@Test
	public void shouldReturnAFortune() {
		assertThat(fortuneServiceClient.getFortune())
				.isEqualTo(ExpectedFortune);
	}

}