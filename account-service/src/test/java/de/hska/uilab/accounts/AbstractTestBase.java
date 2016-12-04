package de.hska.uilab.accounts;/*
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2016 Manuel Vogel
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *  https://opensource.org/licenses/MIT
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import de.hska.uilab.accounts.repository.AccountRepository;
import de.hska.uilab.accounts.repository.ServiceRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;

/**
 * Created by mavogel on 12/4/16.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // clear the db before each test
public abstract class AbstractTestBase {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext context;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected ServiceRepository serviceRepository;

    @Autowired
    protected ObjectMapper objectMapper;

    protected void createServices() {
        Arrays.stream(Service.ServiceName.values()).forEach(s -> createService(s));
    }

    protected Account createSampleTenantAccount(final String email) throws Exception {
        Account createdAcc = this.accountRepository.save(Account.asProspect(email));
        Service.getProspectStandardServices().forEach(service -> {
            createdAcc.addService(this.serviceRepository.findOne(service));
        });

        return this.accountRepository.save(createdAcc);
    }

    protected Account createSampleUserAccount(final Long tenantId, final String firstName, final String lastName, final String email) {
        final Account tenantAccount = this.accountRepository.findOne(tenantId);
        final Account createdAcc = this.accountRepository.save(Account.asUser(tenantId, firstName, lastName, email,
                tenantAccount.getCompany(), tenantAccount.getTenantStatus()));

        tenantAccount.getServices()
                .forEach(service -> createdAcc.addServices(service));

        return this.accountRepository.save(createdAcc);
    }

    protected Service createService(final Service.ServiceName name) {
        return this.serviceRepository.save(new Service(name));
    }
}
