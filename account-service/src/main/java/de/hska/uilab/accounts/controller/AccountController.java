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

import de.hska.uilab.accounts.dto.CreateUserAccountBody;
import de.hska.uilab.accounts.dto.ModifyServiceBody;
import de.hska.uilab.accounts.dto.CreateAccountBody;
import de.hska.uilab.accounts.dto.UpdateAccountBody;
import de.hska.uilab.accounts.model.Account;
import de.hska.uilab.accounts.model.AccountType;
import de.hska.uilab.accounts.model.Service;
import de.hska.uilab.accounts.model.TenantStatus;
import de.hska.uilab.accounts.repository.AccountRepository;
import de.hska.uilab.accounts.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mavogel on 11/23/16.
 */
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    /////////////
    // GET
    /////////////
    @RequestMapping(value = "", method = RequestMethod.GET, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Account> allAccounts() {
        return accountRepository.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> getAccount(@PathVariable long id) {
        if (accountRepository.findOne(id) != null) {
            return ResponseEntity.ok(accountRepository.findOne(id));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    /////////////
    // POST
    /////////////
    @RequestMapping(value = "", produces = "text/plain", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccount(@RequestBody CreateAccountBody createAccountBody) {
        Account createdAccount = Account.asProspect(createAccountBody.getEmail());
        Service.getProspectStandardServices()
                .forEach(service -> createdAccount.addService(this.serviceRepository.findOne(service)));
        return accountRepository.save(createdAccount).getPassword();
    }

    @RequestMapping(value = "/{tenantId}", produces = "text/plain", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createAccount(@PathVariable long tenantId, @RequestBody CreateUserAccountBody createUserAccountBody) {
        Account tenantAccount = accountRepository.findOne(tenantId);
        Account createdAccount = Account.asUser(tenantId,
                createUserAccountBody.getFirstname(),
                createUserAccountBody.getLastname(),
                createUserAccountBody.getEmail(),
                tenantAccount.getCompany(),
                tenantAccount.getServices().toArray(new Service[]{}));
        accountRepository.save(createdAccount);
        return createdAccount.getPassword();
    }

    /////////////
    // PATCH
    /////////////
    @RequestMapping(value = "", method = RequestMethod.PATCH, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> updateAccount(@RequestBody UpdateAccountBody updateAccountBody) {
        Account accountToUpdate = accountRepository.findOne(updateAccountBody.getId());
        accountToUpdate.setUsername(updateAccountBody.getUsername());
        accountToUpdate.setFirstname(updateAccountBody.getFirstname());
        accountToUpdate.setLastname(updateAccountBody.getLastname());
        accountToUpdate.setCompany(updateAccountBody.getCompany());
        accountToUpdate.setEmail(updateAccountBody.getEmail());
        try {
            accountRepository.save(accountToUpdate);
            return ResponseEntity.ok(accountToUpdate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @RequestMapping(value = "/{id}/upgrade", method = RequestMethod.PATCH, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> upgradeToCustomer(@PathVariable long id) {
        Account accountToUpgrade = accountRepository.findOne(id);
        if (AccountType.USER == accountToUpgrade.getAccountType()) {
            accountToUpgrade.setTenantStatus(TenantStatus.CUSTOMER);
            accountRepository.save(accountToUpgrade);
            return ResponseEntity.ok(accountToUpgrade);
        } else if (AccountType.TENANT == accountToUpgrade.getAccountType()) {
            findAllUserAccounts(id)
                    .forEach(useracc -> {
                        useracc.setTenantStatus(TenantStatus.CUSTOMER);
                        this.accountRepository.save(useracc);
                    });

            accountToUpgrade.setTenantStatus(TenantStatus.CUSTOMER);
            this.accountRepository.save(accountToUpgrade);
            return ResponseEntity.ok(accountToUpgrade);
        } else {
            return ResponseEntity.badRequest().body(accountToUpgrade);
        }
    }

    @RequestMapping(value = "/{id}/rmservice", method = RequestMethod.PATCH, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> removeService(@PathVariable long id, @RequestBody List<ModifyServiceBody> modifyServiceBody) {
        Account account = accountRepository.findOne(id);
        if (AccountType.USER == account.getAccountType()) {
            modifyServiceBody.forEach(msb -> {
                Service serviceToRemove = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.removeService(serviceToRemove);
            });
            this.accountRepository.save(account);
            return ResponseEntity.ok(account);
        } else if (AccountType.TENANT == account.getAccountType()) {
            // TODO as well for each user if its a tenant acc
            modifyServiceBody.forEach(msb -> {
                Service serviceToRemove = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.removeService(serviceToRemove);
            });
            accountRepository.save(account);
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.badRequest().body(account);
        }
    }

    @RequestMapping(value = "/{id}/addservice", method = RequestMethod.PATCH, headers = {"Authorization: Bearer"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Account> addService(@PathVariable long id, @RequestBody List<ModifyServiceBody> modifyServiceBody) {
        Account account = accountRepository.findOne(id);
        if (AccountType.USER == account.getAccountType()) {
            modifyServiceBody.forEach(msb -> {
                Service servicetToAdd = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.addService(servicetToAdd);
            });
            accountRepository.save(account);
            return ResponseEntity.ok(account);
        } else if (AccountType.TENANT == account.getAccountType()) {
            modifyServiceBody.forEach(msb -> {
                Service serviceToAdd = serviceRepository.findOne(Service.ServiceName.valueOf(msb.getName()));
                account.addService(serviceToAdd);
            });
            // TODO as well for each user if its a tenant acc
            accountRepository.save(account);
            return ResponseEntity.ok(account);
        } else {
            return ResponseEntity.badRequest().body(account);
        }
    }

    /////////////
    // DELETE
    /////////////
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, headers = {"Authorization: Bearer"})
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
                // TODO as well for each user if its a tenant acc
                accountRepository.delete(account);
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
