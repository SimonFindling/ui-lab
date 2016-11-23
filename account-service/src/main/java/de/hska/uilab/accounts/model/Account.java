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
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

/**
 * Created by mavogel on 11/23/16.
 */
@Entity
public class Account implements Serializable {

    @Id
    @NotBlank
    private final String username;

    @Email @NotBlank
    private String email;

    @Length(min = 6, max = 64) private String password;
    @NotEmpty private Status status;

    private String firstname;
    private String lastname;
    private String company;

    @OneToMany
    private List<Service> services;

    /**
     * Creates a new account for a PROSPECT
     *
     * @param email the email
     * @return the new account
     */
    public static Account asProspect(final String email) {
        return new Account(email);
    }

    private Account(final String email) {
        this.email = email;
        this.username = email.substring(0, email.indexOf('@'));
        this.status = Status.PROSPECT;
        this.password = Base64.getEncoder().encodeToString(Instant.now().toString().getBytes());
    }

    ////// GETTER
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Status getStatus() {
        return status;
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
    public void setEmail(final String email) {
        this.email = email;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public void setStatus(final Status status) {
        this.status = status;
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
}
