package de.hska.uilab.accounts.controller;/*
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

import de.hska.uilab.accounts.dto.*;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.AccountType;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import de.hska.uilab.accounts.repository.AccountRepository;
import de.hska.uilab.accounts.repository.ServiceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 11/23/16.
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ModelMapper mapper;

    /////////////
    // GET
    /////////////
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public Iterable<AccountDto> allAccounts() {
        return ((List<Account>) accountRepository.findAll()).stream()
                .map(acc -> mapper.map(acc, AccountDto.class))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountDto> getAccount(@PathVariable long id) {
        if (accountRepository.findOne(id) != null) {
            return ResponseEntity.ok(mapper.map(accountRepository.findOne(id), AccountDto.class));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /////////////
    // POST
    /////////////
    @RequestMapping(value = "", produces = "text/plain", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createAccount(@RequestBody CreateAccountBody createAccountBody) {
        Account createdAccount = Account.asProspect(createAccountBody.getEmail());
        Service.getProspectStandardServices()
                .forEach(serviceName -> {
                    Service serviceToAdd = this.serviceRepository.findOne(serviceName);
                    if (serviceToAdd == null) {
                        serviceToAdd = this.serviceRepository.save(new Service(serviceName));
                    }
                    createdAccount.addService(serviceToAdd);
                });
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountRepository.save(createdAccount).getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account with email '" + createAccountBody.getEmail() + "' exists already");
        }
    }

    @RequestMapping(value = "/{tenantId}", produces = "text/plain", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createAccount(@PathVariable long tenantId, @RequestBody CreateUserAccountBody createUserAccountBody) {
        Account tenantAccount = accountRepository.findOne(tenantId);
        Account createdAccount = Account.asUser(tenantId,
                createUserAccountBody.getFirstname(),
                createUserAccountBody.getLastname(),
                createUserAccountBody.getEmail(),
                tenantAccount.getCompany(),
                tenantAccount.getTenantStatus(),
                tenantAccount.getServices().toArray(new Service[]{}));
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(accountRepository.save(createdAccount).getPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Account with email '" + createUserAccountBody.getEmail() + "' exists already");
        }
    }

    /////////////
    // PATCH
    /////////////
    @RequestMapping(value = "", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountDto> updateAccount(@RequestBody UpdateAccountBody updateAccountBody) {
        Account accountToUpdate = accountRepository.findOne(updateAccountBody.getId());
        accountToUpdate.setUsername(updateAccountBody.getUsername());
        accountToUpdate.setFirstname(updateAccountBody.getFirstname());
        accountToUpdate.setLastname(updateAccountBody.getLastname());
        accountToUpdate.setCompany(updateAccountBody.getCompany());
        accountToUpdate.setEmail(updateAccountBody.getEmail());
        try {
            accountRepository.save(accountToUpdate);
            return ResponseEntity.ok(mapper.map(accountToUpdate, AccountDto.class));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @RequestMapping(value = "/{id}/upgrade", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountDto> upgradeToCustomer(@PathVariable long id) {
        Account accountToUpgrade = accountRepository.findOne(id);
        if (AccountType.TENANT == accountToUpgrade.getAccountType()) {
            findAllUserAccounts(id)
                    .forEach(useracc -> {
                        useracc.setTenantStatus(TenantStatus.CUSTOMER);
                        this.accountRepository.save(useracc);
                    });

            accountToUpgrade.setTenantStatus(TenantStatus.CUSTOMER);
            this.accountRepository.save(accountToUpgrade);
            return ResponseEntity.ok(mapper.map(accountToUpgrade, AccountDto.class));
        } else {
            return ResponseEntity.badRequest().body(mapper.map(accountToUpgrade, AccountDto.class));
        }
    }

    @RequestMapping(value = "/{id}/rmservice", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountDto> removeService(@PathVariable long id, @RequestBody List<ModifyServiceBody> modifyServiceBody) {
        Account account = accountRepository.findOne(id);
        if (AccountType.TENANT == account.getAccountType()) {
            modifyServiceBody.forEach(msb -> {
                Service serviceToRemove = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.removeService(serviceToRemove);

                findAllUserAccounts(id)
                        .forEach(useracc -> {
                            useracc.removeService(serviceToRemove);
                            this.accountRepository.save(useracc);
                        });
            });
            accountRepository.save(account);
            return ResponseEntity.ok(mapper.map(account, AccountDto.class));
        } else {
            return ResponseEntity.badRequest().body(mapper.map(account, AccountDto.class));
        }
    }

    @RequestMapping(value = "/{id}/addservice", method = RequestMethod.PATCH)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<AccountDto> addService(@PathVariable long id, @RequestBody List<ModifyServiceBody> modifyServiceBody) {
        Account account = accountRepository.findOne(id);
        if (AccountType.TENANT == account.getAccountType()) {
            modifyServiceBody.forEach(msb -> {
                Service serviceToAdd = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.addService(serviceToAdd);

                findAllUserAccounts(id)
                        .forEach(useracc -> {
                            useracc.addService(serviceToAdd);
                            this.accountRepository.save(useracc);
                        });
            });

            return ResponseEntity.ok(mapper.map(accountRepository.save(account), AccountDto.class));
        } else {
            return ResponseEntity.badRequest().body(mapper.map(account, AccountDto.class));
        }
    }

    /////////////
    // DELETE
    /////////////
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeAccount(@PathVariable long id) {
        final Account account = accountRepository.findOne(id);
        if (account == null) {
            return ResponseEntity.notFound().build();
        } else {
            if (AccountType.USER == account.getAccountType()) {
                accountRepository.delete(account);
                return ResponseEntity.noContent().build();
            } else if (AccountType.TENANT == account.getAccountType()) {
                accountRepository.delete(account);
                findAllUserAccounts(id)
                        .forEach(useracc -> this.accountRepository.delete(useracc));
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    private List<Account> findAllUserAccounts(final long tenantId) {
        return ((List<Account>) accountRepository.findAll()).stream()
                .filter(acc -> acc.getTenantId() != null)
                .filter(acc -> acc.getTenantId() == tenantId)
                .filter(acc -> AccountType.USER.equals(acc.getAccountType()))
                .collect(Collectors.toList());
    }

}
