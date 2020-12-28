/*
 * Copyright 2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.jdbcsession;

import io.jmix.core.annotation.JmixModule;
//import io.jmix.rest.RestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
//@JmixModule(dependsOn = RestConfiguration.class)
@PropertySource("classpath:/application.properties")
public class SampleRestTestConfiguration {

//    @Bean
//    protected DataSource dataSource() {
//        BasicDataSource dataSource = new BasicDataSource();
//        dataSource.setUrl("jdbc:hsqldb:mem:testdb");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");
//        return dataSource;
//    }
}
