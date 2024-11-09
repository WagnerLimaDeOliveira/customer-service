package com.insurance.customer_service.controller;

import com.insurance.customer_service.entity.Customer;
import com.insurance.customer_service.service.CustomerService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/customer")
public class CustomerController {
    @Inject
    CustomerService customerService;

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Customer getCustomerById(@PathParam("id") Long id) {
        return customerService.getCustomerById(id);
    }

    @Path("")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer createCustomer(Customer customer) {
        if (customer.getId() == null) {
            return customerService.createCustomer(customer);
        } else {
            return null;
        }
    }

    @Path("")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Customer updateCustomer(Customer customer) {
        if (customer.getId() != null) {
            return customerService.updateCustomer(customer);
        } else {
            return null;
        }
    }

    @Path("")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean deleteCustomer(Customer customer) {
        if (customer.getId() != null) {
            return customerService.deleteCustomerById(customer.getId());
        } else {
            return false;
        }
    }

}
