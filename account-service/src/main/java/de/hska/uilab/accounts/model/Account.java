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

import org.bouncycastle.crypto.generators.BCrypt;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

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
    @Column(unique = true)
    private String email;

    private String username;

    @Length(min = 6, max = 64)
    private String password;

    private String firstname;
    private String lastname;
    private String company;
    private Long tenantId;
    private TenantStatus tenantStatus;
    private AccountType accountType;

    // TODO use a Set here!
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "service_account", joinColumns = @JoinColumn(name = "service_name"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    private List<Service> services;

    /**
     * Creates a new account for a Tenant as PROSPECT.
     *
     * @param email the email-address
     * @return the new account with a generated password
     */
    public static Account asProspect(final String email) {
        return new Account(null, null, null, email, null, AccountType.TENANT, TenantStatus.PROSPECT);
    }

    /**
     * Creates a new account of a user/employee of a tenant.
     *
     * @param tenantId          the id of the tenant account which created the user
     * @param firstname         the first name
     * @param lastname          the last name
     * @param email             the email-address
     * @param company           the company of the tenant
     * @param tenantStatus      the status of the tenant creating this user
     * @param inheritedServices the inherited services from the tenant
     * @return the new account with a generated password
     */
    public static Account asUser(final Long tenantId,
                                 final String firstname, final String lastname, final String email,
                                 final String company, final TenantStatus tenantStatus,
                                 final Service... inheritedServices) {
        return new Account(tenantId, firstname, lastname, email, company, AccountType.USER, tenantStatus, inheritedServices);
    }


    /**
     * JPA needs it
     */
    public Account() {
    }

    /**
     * C'tor
     */
    private Account(final Long tenantId,
                    final String firstname, final String lastname, final String email, final String company,
                    final AccountType accountType, final TenantStatus tenantStatus,
                    final Service... inheritedServices) {
        this.username = "";
        this.firstname = firstname != null ? firstname : "";
        this.lastname = lastname != null ? lastname : "";
        this.company = company != null ? company : "";
        this.email = email;
        this.accountType = accountType;
        this.tenantId = tenantId;
        this.tenantStatus = tenantStatus;
        this.password = Base64.getEncoder().encodeToString(String.valueOf(new Random().nextInt()).getBytes());
        this.services = inheritedServices != null ? new ArrayList<>(Arrays.asList(inheritedServices)) : new ArrayList<>();
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

    public Long getTenantId() {
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
        services.sort((s1, s2) -> s1.getName().ordinal() - s2.getName().ordinal());
        return services;
    }


    ////// SETTER
    public void setUsername(final String username) {
        this.username = username;
    }


    public void setTenantId(final Long tenantId) {
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

    public void addServices(final Service... services) {
        this.services.addAll(Arrays.asList(services));
    }

    public void removeService(final Service service) {
        this.services.remove(service);
    }

    public void setId(final Long id) {
        this.id = id;
    }
}
