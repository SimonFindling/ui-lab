package de.hska.uilab.accounts.model;
/*
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

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created by mavogel on 11/23/16.
 */
@Entity(name = "account")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Email
    @NotBlank
    private String email;

    @Length(min = 6, max = 64)
    private String password;

    private String username;
    private String firstname;
    private String lastname;
    private String company;
    private Integer tenantId;
    private TenantStatus tenantStatus;
    private AccountType accountType;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "service_account", joinColumns = @JoinColumn(name = "service_name"), inverseJoinColumns = @JoinColumn(name = "username"))
    private List<Service> services = new ArrayList<>();

    /**
     * Creates a new account for a Tenant as PROSPECT.
     *
     * @param email the email-address
     * @return the new account with a generated password
     */
    public static Account asProspect(final String email) {
        return new Account(null, null, email, AccountType.TENANT);
    }

    /**
     * Creates a new account of a user/employee of a tenant.
     *
     * @param firstname the first name
     * @param lastname  the last name
     * @param email     the email-address
     * @return the new account with a generated password
     */
    public static Account asUser(final String firstname, final String lastname, final String email) {
        return new Account(firstname, lastname, email, AccountType.USER);
    }


    /**
     * JPA needs it
     */
    private Account() {
    }

    /**
     * C'tor
     */
    private Account(final String firstname, final String lastname, final String email, final AccountType accountType) {
        this.username = "";
        this.firstname = firstname != null ? firstname : "";
        this.lastname = lastname != null ? lastname : "";
        this.email = email;
        this.accountType = accountType;
        this.tenantStatus = AccountType.TENANT == accountType ? TenantStatus.PROSPECT : null;
        this.password = Base64.getEncoder().encodeToString(Instant.now().toString().getBytes());
    }

    ////// GETTER
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public TenantStatus getTenantStatus() {
        return tenantStatus;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getCompany() {
        return company;
    }

    public List<Service> getServices() {
        return services;
    }


    ////// SETTER
    public void setUsername(final String username) {
        this.username = username;
    }


    public void setTenantId(final Integer tenantId) {
        this.tenantId = tenantId;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setTenantStatus(final TenantStatus tenantStatus) {
        this.tenantStatus = tenantStatus;
    }

    public void setAccountType(final AccountType accountType) {
        this.accountType = accountType;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public void setCompany(final String company) {
        this.company = company;
    }

    public void setServices(final List<Service> services) {
        this.services = services;
    }

    public void addService(final Service service) {
        this.services.add(service);
    }

    public void addServices(final Service... service) {
        this.services.addAll(Arrays.asList(service));
    }

    public void removeService(final Service service) {
        this.services.remove(service);
    }
}
