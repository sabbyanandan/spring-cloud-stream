/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder.rabbit.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.cloud.CloudAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Bind to services, either locally or in a Lattice environment.
 *
 * @author Mark Fisher
 * @author Dave Syer
 * @author Glenn Renfro
 * @author David Turanski
 * @author Eric Bottard
 */
@Configuration
@ConditionalOnMissingBean(Binder.class)
@Import(RabbitMessageChannelBinderConfiguration.class)
@AutoConfigureBefore({CloudAutoConfiguration.class, RabbitAutoConfiguration.class})
@PropertySource("classpath:/META-INF/spring-cloud-stream/rabbit-binder.properties")
public class RabbitServiceAutoConfiguration {
	@Configuration
	@Profile("cloud")
	@ConditionalOnClass(Cloud.class)
	protected static class CloudConfig {
		@Bean
		public Cloud cloud() {
			return new CloudFactory().getCloud();
		}

		@Bean
		@ConditionalOnMissingBean(ConnectionFactory.class)
		ConnectionFactory rabbitConnectionFactory(Cloud cloud) {
			return cloud.getSingletonServiceConnector(ConnectionFactory.class, null);
		}
	}
}
