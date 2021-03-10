package com.web.crawler;

import com.web.crawler.exception.CrawlingService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.ClassRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = CrawlerApplication.class)
@Import(CrawlerApplicationIntegrationTest.KafkaTestContainersConfiguration.class)
@DirtiesContext
@ActiveProfiles("test")
class CrawlerApplicationIntegrationTest {

	@Autowired
	private CrawlingService crawlingService;

	@Autowired
	private ApplicationContext context;

	@ClassRule
	public static KafkaContainer kafka =
			new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

	@BeforeAll
	public static void init() {
		kafka.start();
	}

	@Test
	void whenContextLoads_thenRunnersAreNotLoaded() {
		assertNotNull(context.getBean(CrawlingService.class));

		assertThrows(NoSuchBeanDefinitionException.class,
				() -> context.getBean(CommandLineExecutor.class),
				"CommandLineRunner should not be loaded during this integration test");
	}

	@Test
	void contextTests() {
	}

	@Test
	void sendLink() {
		String baseUrl = "https://www.facebook.com";
		crawlingService.startCrawlingUsingUrl(baseUrl);
		KafkaConsumer<String, String> kafkaConsumer = KafkaTestContainersConfiguration.createKafkaConsumer(CrawlingService.TOPIC);
		ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));
		assertEquals(consumerRecords.iterator().next().value(), baseUrl);
	}

	@Test
	void sendLinkWithoutProtocol() {
		crawlingService.startCrawlingUsingUrl("www.facebook.com");
	}

	@Test
	void testWithBadUrl_WhichGetNormalized() {
		crawlingService.startCrawlingUsingUrl("https://www.facebook.com");
	}

	@TestConfiguration
	public static class KafkaTestContainersConfiguration {
		@Bean
		public Map<String, Object> consumerConfigs() {
			Map<String, Object> props = new HashMap<>();
			props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
			props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			props.put(ConsumerConfig.GROUP_ID_CONFIG, CrawlingService.GROUP_ID);
			return props;
		}

		public static KafkaConsumer<String, String> createKafkaConsumer(String topicName) {
			Properties properties = new Properties();
			properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
			properties.put(ConsumerConfig.GROUP_ID_CONFIG, CrawlingService.GROUP_ID);
			properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
			properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

			KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
			kafkaConsumer.subscribe(List.of(topicName));
			return kafkaConsumer;
		}

		@Bean
		public ProducerFactory<Object, Object> producerFactory() {
			Map<String, Object> configProps = new HashMap<>();
			configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
			configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
			configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class);
			return new DefaultKafkaProducerFactory<>(configProps);
		}
	}
}
