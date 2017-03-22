/****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************/

package org.apache.james.transport.matchers;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.mailet.MailAddress;
import org.apache.mailet.base.test.FakeMail;
import org.apache.mailet.base.test.FakeMatcherConfig;
import org.apache.mailet.base.test.MimeMessageBuilder;
import org.junit.Before;
import org.junit.Test;

public class TooMuchLinesTest {
	
    private TooMuchLines testee;

    @Before
    public void setUp() {
        testee = new TooMuchLines();
    }

    @Test(expected = MessagingException.class)
    public void initShouldThrowOnAbsentCondition() throws Exception {
    	
    	testee.init(FakeMatcherConfig.builder().matcherName("name").build());
       
    }

    @Test(expected = NumberFormatException.class)
    public void initShouldThrowOnInvalidCondition() throws Exception {
         	
    	testee.init(FakeMatcherConfig.builder().condition("a").matcherName("name").build());
    	   	
    }

    @Test(expected = NumberFormatException.class)
    public void initShouldThrowOnEmptyCondition() throws Exception {
        
    	testee.init(FakeMatcherConfig.builder().condition("").matcherName("name").build());
    	
    }

    @Test(expected = MessagingException.class)
    public void initShouldThrowOnZeroCondition() throws Exception {
       
    	testee.init(FakeMatcherConfig.builder().condition("0").matcherName("name").build());
    	
    }

    @Test(expected = MessagingException.class)
    public void initShouldThrowOnNegativeCondition() throws MessagingException {
        
    	testee.init(FakeMatcherConfig.builder().condition("-10").matcherName("name").build());
    	
    }

    @Test
    public void matchShouldReturnNoRecipientWhenMailHaveNoMimeMessageAndConditionIs100() throws Exception {
       
    	testee.init(FakeMatcherConfig.builder().condition("100").matcherName("name").build());
    	Collection<MailAddress> listMailAddress = testee.match(FakeMail.builder().build());
    	assertThat(listMailAddress).isEmpty();
    	
    }

    @Test
    public void matchShouldAcceptMailsUnderLimit() throws Exception {
        /*
        Start the matcher with condition = 100

        What happens with a mail with just a few lines?

        We should return empty list
         */
    	testee.init(FakeMatcherConfig.builder().condition("100").matcherName("name").build());
    	
    	FakeMail fakeMail = FakeMail.builder()
    		.mimeMessage(MimeMessageBuilder.mimeMessageBuilder()
    				.setMultipartWithBodyParts(MimeMessageBuilder.bodyPartBuilder()
    						.data("content")
    						.build())
    				.build())
    		.build();
    	
    	Collection<MailAddress> result = testee.match(fakeMail);
    	
    	assertThat(result).isEmpty();
    }

    @Test
    public void matchShouldRejectMailsOverLimit() throws Exception {
        /*
        Start the matcher with condition = 10

        What happens with a mail with 11 line body?

        We should return the list of Recipients
         */
    	testee.init(FakeMatcherConfig.builder().condition("10").matcherName("name").build());
    	
    	FakeMail fakeMail = FakeMail.builder()
    						.mimeMessage(MimeMessageBuilder.mimeMessageBuilder()
    						.setMultipartWithBodyParts(MimeMessageBuilder.bodyPartBuilder()
    						.data("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n11")
    						.build())
    						.build())
    						.build();
    	
    	Collection<MailAddress> result = testee.match(fakeMail);
    	
    	assertThat(result).isEqualTo(fakeMail.getRecipients());
    }

}
