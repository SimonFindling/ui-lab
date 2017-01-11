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

import de.hska.uilab.accounts.dto.AccountDto;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.AccountType;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import org.junit.Test;
import org.modelmapper.ModelMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by mavogel on 01/11/17.
 */
public class MappingTests {

    private ModelMapper mapper = new ModelMapper();

    @Test
    public void shouldMapProspectEntityToDTO() throws Exception {
        Service serviceEntity = new Service(Service.ServiceName.PRODUCT);
        Account accountEntity = Account.asProspect("test@mail.org");
        accountEntity.setId(1L);
        accountEntity.addService(serviceEntity);

        // == go
        AccountDto accountDto = mapper.map(accountEntity, AccountDto.class);

        // == verify
        assertEquals(1L, accountDto.getId().longValue());
        assertEquals(accountEntity.getEmail(), accountDto.getEmail());
        assertEquals("", accountDto.getUsername());
        assertEquals("", accountDto.getFirstname());
        assertEquals("", accountDto.getLastname());
        assertEquals("", accountDto.getCompany());
        assertEquals(AccountType.TENANT, accountDto.getAccountType());
        assertEquals(TenantStatus.PROSPECT, accountDto.getTenantStatus());
        assertNull(accountDto.getTenantId());
    }
}
