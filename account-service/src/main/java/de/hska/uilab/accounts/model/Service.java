package de.hska.uilab.accounts.model;/*
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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mavogel on 11/23/16.
 */
@Entity(name = "service")
public class Service implements Serializable {

    /**
     * The names of the implemented services
     */
    public enum ServiceName {
        PRODUCT,
        CUSTOMER,
        SALES,
        VENDOR
    }

    @Id
    private ServiceName name;

    /**
     * JPA needs it
     */
    private Service() {
    }

    /**
     * Creates a new service.
     *
     * @param name    the name of the service
     */
    public Service(final ServiceName name) {
        this.name = name;
    }

    public ServiceName getName() {
        return name;
    }

    /**
     * @return the standard services for a new prospect
     */
    public static List<ServiceName> getProspectStandardServices() {
        return Arrays.asList(
                ServiceName.PRODUCT,
                ServiceName.CUSTOMER,
                ServiceName.VENDOR);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Service{");
        sb.append("name=").append(name);
        sb.append('}');
        return sb.toString();
    }
}
